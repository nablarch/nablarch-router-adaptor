package nablarch.integration.router;

import mockit.Deencapsulation;
import mockit.Delegate;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import nablarch.core.repository.ObjectLoader;
import nablarch.core.repository.SystemRepository;
import nablarch.core.util.FileUtil;
import nablarch.fw.ExecutionContext;
import nablarch.fw.web.HttpErrorResponse;
import nablarch.fw.web.HttpRequest;
import nablarch.fw.web.HttpResponse;
import nablarch.fw.web.handler.MethodBinderFactory;
import nablarch.fw.web.servlet.HttpRequestWrapper;
import nablarch.fw.web.servlet.NablarchHttpServletRequestWrapper;
import nablarch.fw.web.servlet.ServletExecutionContext;
import nablarch.integration.router.sub.SubRoutesMappingTestAction;
import net.unit8.http.router.RoutingException;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * {@link RoutesMapping}のテスト。
 * <p>
 * {@link NablarchControllerDetector}も合わせてテストします。
 * <p>
 * ルーティングライブラリの仕様は以下を見てください。<br>
 * https://github.com/kawasima/sastruts-advanced-routes/blob/master/README.ja.md
 */
public class RoutesMappingTest {

    @Mocked
    private HttpRequestWrapper request;
    @Mocked
    private HttpServletRequest servletRequest;

    private RoutesMapping sut;
    private ServletExecutionContext context;

    @Before
    public void setUp() {

        sut = new RoutesMapping();
        sut.setBasePackage("nablarch.integration.router");
        sut.initialize();

        SystemRepository.clear();
        SystemRepository.load(new ObjectLoader() {
            @Override
            public Map<String, Object> load() {
                return new HashMap<String, Object>() {{
                    put("packageMapping", sut);
                }};
            }
        });

        context = new ServletExecutionContext(servletRequest, null, null);
    }

    /**
     * GETリクエストをマッピングできること。
     */
    @Test
    public void get() throws Exception {

        new Expectations() {{
            request.getRequestPath();
            result = "/method";
            request.getMethod();
            result = "GET";
        }};

        Class<?> cls = sut.getHandlerClass(request, context);
        assertThat(cls.getName(), is(RoutesMappingTestAction.class.getName()));

        context.addHandler(new RoutesMappingTestAction());
        assertThat((String) context.handleNext(request), is("get method was invoked."));
    }

    /**
     * POSTリクエストをマッピングできること。
     */
    @Test
    public void post() throws Exception {

        new Expectations() {{
            request.getRequestPath();
            result = "/method";
            request.getMethod();
            result = "POST";
        }};

        Class<?> cls = sut.getHandlerClass(request, context);
        assertThat(cls.getName(), is(RoutesMappingTestAction.class.getName()));

        context.addHandler(new RoutesMappingTestAction());
        assertThat((String) context.handleNext(request), is("post method was invoked."));
    }

    /**
     * PATCHリクエストをマッピングできること。
     */
    @Test
    public void patch() throws Exception {

        new Expectations() {{
            request.getRequestPath();
            result = "/method";
            request.getMethod();
            result = "PATCH";
        }};

        Class<?> cls = sut.getHandlerClass(request, context);
        assertThat(cls.getName(), is(RoutesMappingTestAction.class.getName()));

        context.addHandler(new RoutesMappingTestAction());
        assertThat((String) context.handleNext(request), is("patch method was invoked."));
    }

    /**
     * 特別なパラメータである:controllerと:actionを使ったマッピングができること。
     */
    @Test
    public void controllerAction() throws Exception {

        new Expectations() {{
            request.getRequestPath();
            result = "/RoutesMappingTest/controllerAction";
        }};

        Class<?> cls = sut.getHandlerClass(request, context);
        assertThat(cls.getName(), is(RoutesMappingTestAction.class.getName()));

        context.addHandler(new RoutesMappingTestAction());
        assertThat((String) context.handleNext(request), is("controllerAction method was invoked."));
    }

    /**
     * ベースパッケージの子パッケージにあるリソースにマッピングできること。
     */
    @Test
    public void controllerActionForSubPackage() throws Exception {

        new Expectations() {{
            request.getRequestPath();
            result = "/sub/SubRoutesMappingTest/controllerActionForSubPackage";
        }};

        Class<?> cls = sut.getHandlerClass(request, context);
        assertThat(cls.getName(), is(SubRoutesMappingTestAction.class.getName()));

        context.addHandler(new SubRoutesMappingTestAction());
        assertThat((String) context.handleNext(request), is("controllerActionForSubPackage method was invoked."));
    }

    /**
     * パスパラメータを使ったマッピングができること。
     */
    @Test
    public void pathParameter() throws Exception {

        final Map<String, String> parameters = new HashMap<String, String>();
        new Expectations() {{
            request.getRequestPath();
            result = "/RoutesMappingTest/controllerActionWithId/1234";
            request.setParam("id", "1234");
            result = new Delegate<Void>() {
                public void delegate(final String name, final String... params) {
                    parameters.put(name, params[0]);
                }
            };
        }};

        Class<?> cls = sut.getHandlerClass(request, context);
        assertThat(cls.getName(), is(RoutesMappingTestAction.class.getName()));

        context.addHandler(new RoutesMappingTestAction());
        assertThat((String) context.handleNext(request), is("controllerActionWithId method was invoked."));
        assertThat(parameters.get("id"), is("1234"));
    }

    /**
     * 引数の{@link ExecutionContext}に{@link ServletExecutionContext}以外が指定された場合、
     * 引数の{@link HttpRequest}から取得したリクエストパスを使ってマッピングできること。
     */
    @Test
    public void notServletExecutionContext(@Mocked final HttpRequest request) throws Exception {

        new Expectations() {{
            request.getRequestPath();
            result = "/";
        }};

        final ExecutionContext executionContext = new ExecutionContext();
        Class<?> cls = sut.getHandlerClass(request, executionContext);
        assertThat(cls.getName(), is(RoutesMappingTestAction.class.getName()));

        executionContext.addHandler(new RoutesMappingTestAction());
        assertThat((String) executionContext.handleNext(request), is("root method was invoked."));
    }

    /**
     * 引数の{@link ExecutionContext}に{@link ServletExecutionContext}が指定された場合、
     * 引数の{@link ServletExecutionContext}経由で取得したリクエストパスを使ってマッピングできること。
     */
    @Test
    public void servletExecutionContext() throws Exception {

        new Expectations() {{
            request.getRequestPath();
            result = "/";
        }};

        Class<?> cls = sut.getHandlerClass(request, context);
        assertThat(cls.getName(), is(RoutesMappingTestAction.class.getName()));

        context.addHandler(new RoutesMappingTestAction());
        assertThat((String) context.handleNext(request), is("root method was invoked."));
    }

    /**
     * ベースURIを指定してもマッピングできること。
     */
    @Test
    public void baseUri() throws Exception {

        new Expectations() {{
            request.getRequestPath();
            result = "/action/RoutesMappingTest/controllerAction";
        }};

        sut.setBaseUri("/action/");

        Class<?> cls = sut.getHandlerClass(request, context);
        assertThat(cls.getName(), is(RoutesMappingTestAction.class.getName()));

        context.addHandler(new RoutesMappingTestAction());
        assertThat((String) context.handleNext(request), is("controllerAction method was invoked."));
    }

    /**
     * マッピングできない場合、404を表すエラーレスポンスがスローされること。
     */
    @Test
    public void mappingNotFound() throws Exception {

        new Expectations() {{
            request.getRequestPath();
            result = "/unknown/path";
        }};

        try {
            sut.getHandlerClass(request, context);
            fail("HttpErrorResponseがスローされる");
        } catch (HttpErrorResponse e) {
            assertThat(e.getResponse().getStatusCode(), is(HttpResponse.Status.NOT_FOUND.getStatusCode()));
            assertThat(e.getResponse().getContentLength(), is("0"));
            assertThat(e.getCause(), instanceOf(RoutingException.class));
        }
    }

    /**
     * リソースファイルが見つからない場合、実行時例外がスローされること。
     */
    @Test
    public void routesFileNotFound() throws Exception {
        try {
            sut.setRoutes("unknown.xml");
            fail("IllegalArgumentExceptionがスローされる");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("routes resource could not be found. routes = [unknown.xml]"));
        }
    }

    /**
     * 繰り返し呼んで、毎回リソースをリロードしても、問題ないこと。
     */
    @Test
    public void multipleInvoking() throws Exception {
        get();
        updateLastModified();
        post();
        updateLastModified();
        pathParameter();
    }

    private static void updateLastModified() {
        final File file = new File(FileUtil.getResourceURL("classpath:routes.xml").getPath());
        if (!file.setLastModified(System.currentTimeMillis())) {
            throw new RuntimeException("routes.xml could not be update last modified time.");
        }
    }

    /**
     * routes.xmlがfileプロトコル以外でも読み込めること。
     */
    @Test
    public void protocolOfRoutesFileIsNotFile(final @Injectable URL mockUrl) throws Exception {

        new Expectations() {{
            request.getRequestPath();
            result = "/RoutesMappingTest/controllerAction";
        }};

        final RoutesMapping sut = new RoutesMapping();
        sut.setBasePackage("nablarch.integration.router");

        // zipプロトコルのURLを作れないため、モックを使う。
        // プロトコルだけ変更し、InputStreamは元のURLのものを使う。
        final URL originalUrl = Deencapsulation.getField(sut, "routesUrl");
        Deencapsulation.setField(sut, "routesUrl", mockUrl);
        new Expectations() {{
            mockUrl.getProtocol();
            result = "zip";
            mockUrl.openStream();
            result = originalUrl.openStream();
        }};

        sut.initialize();

        final Class<?> cls = sut.getHandlerClass(request, context);
        assertThat(cls.getName(), is(RoutesMappingTestAction.class.getName()));

        context.addHandler(new RoutesMappingTestAction());
        assertThat((String) context.handleNext(request), is("controllerAction method was invoked."));
    }

    /**
     * カスタムの{@link MethodBinderFactory}を設定した場合、
     * カスタムの{@link MethodBinderFactory}によって生成された
     * {@link nablarch.fw.MethodBinder}がコンテキストに設定されること。
     */
    @Test
    public void customMethodBinderFactory() throws Exception {

        new Expectations() {{
            request.getRequestPath();
            result = "/method";
            request.getMethod();
            result = "GET";
        }};

        final RoutesMapping sut = new RoutesMapping();
        sut.setMethodBinderFactory(new CustomMethodBinderFactory());
        sut.setBasePackage("nablarch.integration.router");

        sut.initialize();
        sut.getHandlerClass(request, context);

        assertThat(context.getMethodBinder(), instanceOf(CustomMethodBinderFactory.CustomMethodBinder.class));
    }
}
