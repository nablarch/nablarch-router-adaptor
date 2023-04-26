package nablarch.integration.router;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import nablarch.core.util.StringUtil;
import nablarch.fw.ExecutionContext;
import nablarch.fw.web.HttpErrorResponse;
import nablarch.fw.web.servlet.HttpRequestWrapper;
import nablarch.fw.web.servlet.ServletExecutionContext;
import nablarch.integration.router.test.PathOptionsProviderRoutesMappingTest.testNotServletExecutionContext.FooAction;
import nablarch.integration.router.test.PathOptionsProviderRoutesMappingTest.testPathParameter.PathParameterAction;
import nablarch.integration.router.test.PathOptionsProviderRoutesMappingTest.testSimpleRouting.SimpleAction;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.MockedConstruction;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static nablarch.integration.router.PathOptionsFactory.pathOptions;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link PathOptionsProvider} のテスト。
 *
 * @author Tanaka Tomoyuki
 */
public class PathOptionsProviderRoutesMappingTest {
    private MockedConstruction<HttpRequestWrapper> httpRequestWrapperMock;
    private HttpRequestWrapper request;
    private final HttpServletRequest servletRequest = mock(HttpServletRequest.class, RETURNS_DEEP_STUBS);

    @Rule
    public ExpectedException exception = ExpectedException.none();
    
    private PathOptionsProviderRoutesMapping sut;
    private MockPathOptionsProvider pathOptionsProvider;
    private ExecutionContext executionContext;
    
    @Before
    public void setUp() {
        httpRequestWrapperMock = mockConstruction(HttpRequestWrapper.class, (mock, context) -> {
            request = mock;
        });
        executionContext = new ServletExecutionContext(new MockHttpServletRequest(servletRequest), null, null);

        sut = new PathOptionsProviderRoutesMapping();
        
        sut.setMethodBinderFactory(new RoutesMethodBinderFactory());
        
        pathOptionsProvider = new MockPathOptionsProvider();
        sut.setPathOptionsProvider(pathOptionsProvider);
    }

    @After
    public void tearDown() {
        httpRequestWrapperMock.close();
    }

    @Test
    public void testThrowsExceptionIfOptionsProviderIsNotSetWhenInitializing() {
        exception.expect(IllegalStateException.class);
        exception.expectMessage("pathOptionsProvider is not set.");
        sut.setPathOptionsProvider(null);
        
        sut.initialize();
    }

    @Test
    public void testNotServletExecutionContext() throws Exception {
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestPath()).thenReturn("/test/foo");

        ExecutionContext plainExecutionContext = new ExecutionContext();
        pathOptionsProvider.add(pathOptions("GET", "/test/foo", FooAction.class, "get"));
        
        sut.initialize();

        Class<?> handlerClass = sut.getHandlerClass(request, plainExecutionContext);
        assertThat(handlerClass, Matchers.<Class<?>>sameInstance(FooAction.class));

        plainExecutionContext.addHandler(new FooAction());
        assertThat((String)plainExecutionContext.handleNext(request), is("FooAction#get() method is invoked"));
    }
        
    @Test
    public void testSimpleRouting() throws Exception {
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestPath()).thenReturn("/test/simple");

        pathOptionsProvider
            .add(pathOptions("GET", "/test/simple", SimpleAction.class, "get"))
            .add(pathOptions("POST", "/test/simple", SimpleAction.class, "post"));
        
        sut.initialize();

        Class<?> handlerClass = sut.getHandlerClass(request, executionContext);
        assertThat(handlerClass, Matchers.<Class<?>>sameInstance(SimpleAction.class));
        
        executionContext.addHandler(new SimpleAction());
        assertThat((String)executionContext.handleNext(request), is("SimpleAction#get() method is invoked"));
    }
    
    @Test
    public void testThrowsExceptionIfRouteNotFound() throws Exception {
        when(request.getMethod()).thenReturn("PUT");
        when(request.getRequestPath()).thenReturn("/test/simple");

        pathOptionsProvider
            .add(pathOptions("GET", "/test/simple", SimpleAction.class, "get"))
            .add(pathOptions("POST", "/test/simple", SimpleAction.class, "post"));
        sut.initialize();

        exception.expect(HttpErrorResponse.class);
        exception.expect(
            hasProperty("response",
                hasProperty("statusCode", is(404))
            )
        );
        
        sut.getHandlerClass(request, executionContext);
    }
    
    @Test
    public void testBaseUri() throws Exception {
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestPath()).thenReturn("/base-uri/test/simple");

        pathOptionsProvider
            .add(pathOptions("GET", "/test/simple", SimpleAction.class, "get"))
            .add(pathOptions("POST", "/test/simple", SimpleAction.class, "post"));
        sut.setBaseUri("/base-uri");
        
        sut.initialize();

        Class<?> handlerClass = sut.getHandlerClass(request, executionContext);
        assertThat(handlerClass, Matchers.<Class<?>>sameInstance(SimpleAction.class));

        executionContext.addHandler(new SimpleAction());
        assertThat((String)executionContext.handleNext(request), is("SimpleAction#post() method is invoked"));
    }

    @Test
    public void testBaseUriEndsWithSlash() throws Exception {
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestPath()).thenReturn("/base-uri/test/simple");

        pathOptionsProvider
            .add(pathOptions("GET", "/test/simple", SimpleAction.class, "get"))
            .add(pathOptions("POST", "/test/simple", SimpleAction.class, "post"));
        sut.setBaseUri("/base-uri/");
        
        sut.initialize();

        Class<?> handlerClass = sut.getHandlerClass(request, executionContext);
        assertThat(handlerClass, Matchers.<Class<?>>sameInstance(SimpleAction.class));

        executionContext.addHandler(new SimpleAction());
        assertThat((String)executionContext.handleNext(request), is("SimpleAction#post() method is invoked"));
    }

    @Test
    public void testRoutingLog() throws Exception {
        PrintStream originalStdOut = System.out;
        try {
            ByteArrayOutputStream onMemoryOut = new ByteArrayOutputStream();
            System.setOut(new PrintStream(onMemoryOut, true, "UTF-8"));

            pathOptionsProvider
                .add(pathOptions("GET", "test/log-test/ccc", "Unused", "unused"))
                .add(pathOptions("POST", "test/log-test/aaa/(:param2)", "Unused", "unused"))
                .add(pathOptions("PUT", "test/log-test/bbb/(:param1)", "Unused", "unused"));
            sut.setPathOptionsFormatter(new PathOptionsFormatter() {
                @Override
                public String format(List<PathOptions> pathOptionsList) {
                    // path だけをソートして "," で連結する実装
                    List<String> pathList = new ArrayList<String>(pathOptionsList.size());
                    for (PathOptions pathOptions : pathOptionsList) {
                        pathList.add(pathOptions.getPath());
                    }
                    Collections.sort(pathList);
                    return StringUtil.join(", ", pathList);
                }
            });

            sut.initialize();

            String logText = new String(onMemoryOut.toByteArray(), "UTF-8");
            assertThat(logText, containsString("test/log-test/aaa/(:param2), test/log-test/bbb/(:param1), test/log-test/ccc"));
        } finally {
            System.setOut(originalStdOut);
        }
    }
    
    @Test
    public void testPathParameter() throws Exception {
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestPath()).thenReturn("/test/path-param/123/get/hello");

        pathOptionsProvider.add(pathOptions("GET", "/test/path-param/(:param1)/get/(:param2)", PathParameterAction.class, "get"));
        sut.initialize();
        
        sut.getHandlerClass(request, executionContext);

        verify(request).setParam("param1", "123");
        verify(request).setParam("param2", "hello");
        verify(request, never()).setParam(eq("controller"), any());
        verify(request, never()).setParam(eq("action"), any());
    }
    
    @Test
    public void testRoutesMethodBinderFactoryIsUsedIfMethodBinderFactoryIsNotSet() throws Exception {
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestPath()).thenReturn("/test/simple");

        sut.setMethodBinderFactory(null);
        pathOptionsProvider
                .add(pathOptions("GET", "/test/simple", SimpleAction.class, "get"))
                .add(pathOptions("POST", "/test/simple", SimpleAction.class, "post"));

        sut.initialize();

        Class<?> handlerClass = sut.getHandlerClass(request, executionContext);
        assertThat(handlerClass, Matchers.<Class<?>>sameInstance(SimpleAction.class));

        executionContext.addHandler(new SimpleAction());
        assertThat((String)executionContext.handleNext(request), is("SimpleAction#get() method is invoked"));
    }

    private static class MockPathOptionsProvider implements PathOptionsProvider {
        private List<PathOptions> pathOptionsList = new ArrayList<PathOptions>();

        private MockPathOptionsProvider add(PathOptions pathOptions) {
            pathOptionsList.add(pathOptions);
            return this;
        }

        @Override
        public List<PathOptions> provide() {
            return pathOptionsList;
        }
    }

    private static class MockHttpServletRequest extends HttpServletRequestWrapper {
        private Map<String, Object> requestMap = new HashMap<String, Object>();

        private MockHttpServletRequest(HttpServletRequest request) {
            super(request);
        }

        @Override
        public void setAttribute(String name, Object value) {
            requestMap.put(name, value);
        }

        @Override
        public Object getAttribute(String name) {
            return requestMap.get(name);
        }
    }
}