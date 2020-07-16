package nablarch.integration.router;

import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import nablarch.core.util.StringUtil;
import nablarch.fw.ExecutionContext;
import nablarch.fw.jaxrs.JaxRsMethodBinderFactory;
import nablarch.fw.web.HttpErrorResponse;
import nablarch.fw.web.HttpResponse;
import nablarch.fw.web.servlet.HttpRequestWrapper;
import nablarch.fw.web.servlet.ServletExecutionContext;
import nablarch.integration.jaxrs.jersey.JerseyJaxRsHandlerListFactory;
import nablarch.integration.router.jaxrs.JaxRsOptionsCollector;
import nablarch.integration.router.test.ClassTraversalRoutesMappingTest.testNotServletExecutionContext.FooAction;
import nablarch.integration.router.test.ClassTraversalRoutesMappingTest.testSimpleRouting.SimpleAction;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * {@link ClassTraversalOptionsCollector} のテスト。
 *
 * @author Tanaka Tomoyuki
 */
public class ClassTraversalRoutesMappingTest {
    @Mocked
    private HttpRequestWrapper request;
    @Mocked
    private HttpServletRequest servletRequest;

    @Rule
    public ExpectedException exception = ExpectedException.none();
    
    private ClassTraversalRoutesMapping sut;
    private ExecutionContext executionContext;
    
    @Before
    public void setUp() {
        executionContext = new ServletExecutionContext(new MockHttpServletRequest(servletRequest), null, null);

        sut = new ClassTraversalRoutesMapping();
        
        JaxRsOptionsCollector jaxRsOptionsCollector = new JaxRsOptionsCollector();
        jaxRsOptionsCollector.setApplicationPath("test");
        sut.setOptionsCollector(jaxRsOptionsCollector);

        JaxRsMethodBinderFactory methodBinderFactory = new JaxRsMethodBinderFactory();
        methodBinderFactory.setHandlerList(new JerseyJaxRsHandlerListFactory().createObject());
        sut.setMethodBinderFactory(methodBinderFactory);
    }

    @Test
    public void testThrowsExceptionIfOptionsCollectorIsNotSetWhenInitializing() {
        exception.expect(IllegalStateException.class);
        exception.expectMessage("optionsCollector is not set.");
        sut.setOptionsCollector(null);
        sut.initialize();
    }

    @Test
    public void testNotServletExecutionContext() throws Exception {
        new Expectations() {{
            request.getMethod(); result = "GET";
            request.getRequestPath(); result = "/test/foo";
        }};

        sut.setBasePackage("nablarch.integration.router.test.ClassTraversalRoutesMappingTest.testNotServletExecutionContext");
        sut.initialize();

        ExecutionContext plainExecutionContext = new ExecutionContext();
        Class<?> handlerClass = sut.getHandlerClass(request, plainExecutionContext);
        
        assertThat(handlerClass, Matchers.<Class<?>>sameInstance(FooAction.class));

        plainExecutionContext.addHandler(new FooAction());
        final HttpResponse response = plainExecutionContext.handleNext(request);
        assertThat(response.getBodyString(), is("[\"FooAction\",\"get() method\",\"invoked\"]"));
    }

    @Test
    public void testSimpleRouting() throws Exception {
        new Expectations() {{
            request.getMethod(); result = "GET";
            request.getRequestPath(); result = "/test/simple";
        }};

        sut.setBasePackage("nablarch.integration.router.test.ClassTraversalRoutesMappingTest.testSimpleRouting");
        sut.initialize();

        Class<?> handlerClass = sut.getHandlerClass(request, executionContext);
        
        assertThat(handlerClass, Matchers.<Class<?>>sameInstance(SimpleAction.class));
        
        executionContext.addHandler(new SimpleAction());
        final HttpResponse response = executionContext.handleNext(request);
        assertThat(response.getBodyString(), is("[\"SimpleAction\",\"get() method\",\"invoked\"]"));
    }
    
    @Test
    public void testThrowsExceptionIfRouteNotFound() throws Exception {
        new Expectations() {{
            request.getMethod(); result = "PUT";
            request.getRequestPath(); result = "/test/simple";
        }};

        sut.setBasePackage("nablarch.integration.router.test.ClassTraversalRoutesMappingTest.testSimpleRouting");
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
        new Expectations() {{
            request.getMethod(); result = "POST";
            request.getRequestPath(); result = "/base-uri/test/simple";
        }};

        sut.setBaseUri("/base-uri");
        sut.setBasePackage("nablarch.integration.router.test.ClassTraversalRoutesMappingTest.testSimpleRouting");
        sut.initialize();

        Class<?> handlerClass = sut.getHandlerClass(request, executionContext);

        assertThat(handlerClass, Matchers.<Class<?>>sameInstance(SimpleAction.class));

        executionContext.addHandler(new SimpleAction());
        final HttpResponse response = executionContext.handleNext(request);
        assertThat(response.getBodyString(), is("[\"SimpleAction\",\"post() method\",\"invoked\"]"));
    }

    @Test
    public void testRoutingLog() throws Exception {
        PrintStream originalStdOut = System.out;
        try {
            ByteArrayOutputStream onMemoryOut = new ByteArrayOutputStream();
            System.setOut(new PrintStream(onMemoryOut, true, "UTF-8"));

            sut.setBasePackage("nablarch.integration.router.test.ClassTraversalRoutesMappingTest.testRoutingLog");
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
        new Expectations() {{
            request.getMethod(); result = "GET";
            request.getRequestPath(); result = "/test/path-param/123/get/hello";
        }};

        sut.setBasePackage("nablarch.integration.router.test.ClassTraversalRoutesMappingTest.testPathParameter");
        sut.initialize();
        final Class<?> handlerClass = sut.getHandlerClass(request, executionContext);
        executionContext.addHandler(handlerClass.getConstructor().newInstance());
        executionContext.handleNext(request);
        
        new Verifications() {{
            request.setParam("param1", "123"); times = 1;
            request.setParam("param2", "hello"); times = 1;
            request.setParam("controller", (String[])any); times = 0;
            request.setParam("action", (String[])any); times = 0;
        }};
    }

    private static class MockHttpServletRequest extends HttpServletRequestWrapper {
        private Map<String, Object> requestMap = new HashMap<String, Object>();

        public MockHttpServletRequest(HttpServletRequest request) {
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