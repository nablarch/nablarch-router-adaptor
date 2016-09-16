package nablarch.integration.router;

import nablarch.fw.ExecutionContext;
import nablarch.fw.MethodBinder;
import nablarch.fw.web.HttpRequest;
import nablarch.fw.web.HttpRequestHandler;
import nablarch.fw.web.HttpResponse;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * {@link RoutesMethodBinderFactory}のテストクラス。
 */
public class RoutesMethodBinderFactoryTest {

    private RoutesMethodBinderFactory sut = new RoutesMethodBinderFactory();

    /**
     * {@link RoutesMethodBinderFactory#create(String)}のテストケース
     */
    @Test
    public void testCreate() throws Exception {
        MethodBinder<HttpRequest, Object> factory = sut.create("handle");
        assertThat("RoutesMethodBinderが生成されること", factory, is(instanceOf(RoutesMethodBinder.class)));

        // factoryに指定したメソッドが呼び出せることも確認する
        HttpResponse result = (HttpResponse)factory.bind(new Action()).handle(null, new ExecutionContext());
        assertThat(result.getStatusCode(), is(200));
        assertThat(result.getBodyString(), is("success"));
    }

    /**
     * テスト用のアクションクラス。
     */
    public static class Action implements HttpRequestHandler {
        @Override
        public HttpResponse handle(HttpRequest request, ExecutionContext context) {
            HttpResponse response = new HttpResponse(200);
            response.write("success");
            return response;
        }
    }
}
