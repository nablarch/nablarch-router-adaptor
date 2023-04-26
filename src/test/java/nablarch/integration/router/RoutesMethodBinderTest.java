package nablarch.integration.router;

import jakarta.servlet.http.HttpServletRequest;
import nablarch.fw.ExecutionContext;
import nablarch.fw.Result;
import nablarch.fw.web.HttpRequest;
import nablarch.fw.web.HttpResponse;
import nablarch.fw.web.servlet.HttpRequestWrapper;
import nablarch.fw.web.servlet.ServletExecutionContext;
import org.junit.Test;
import org.mockito.MockedConstruction;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;

/**
 * {@link RoutesMethodBinder}のテスト。
 */
public class RoutesMethodBinderTest {

    private final HttpRequest request = mock(HttpRequest.class);
    private final HttpServletRequest servletRequest = mock(HttpServletRequest.class, RETURNS_DEEP_STUBS);

    private final ExecutionContext unusedContext = null;

    /**
     * 呼び出しメソッドのシグネチャが正しい場合、
     * メソッドが呼ばれ、メソッドの戻り値がそのまま返されること。
     */
    @Test
    public void bindForCorrectMethod() {
        try (final MockedConstruction<HttpRequestWrapper> mocked = mockConstruction(HttpRequestWrapper.class)) {
            final RoutesMethodBinder sut = new RoutesMethodBinder("handle");
            ServletExecutionContext context = new ServletExecutionContext(servletRequest, null, null);

            String response = (String) sut.bind(new Action()).handle(request, context);

            assertThat(response, is("invoking!!!"));
        }
    }

    /**
     * 呼び出しメソッドのシグネチャが正しくない場合、
     * メソッドが呼ばれず、{@link nablarch.fw.Result.NotFound}がスローされること。
     */
    @Test(expected = Result.NotFound.class)
    public void bindForMethodNotFound() {

        final RoutesMethodBinder sut = new RoutesMethodBinder("incorrectMethod");

        HttpResponse response = (HttpResponse) sut.bind(new Action()).handle(request, unusedContext);
    }

    public static final class Action {

        public String handle(HttpRequest request, ExecutionContext context) {
            return "invoking!!!";
        }

        public String incorrectMethod(Object request, ExecutionContext context) {
            throw new RuntimeException("unreachable");
        }
    }
}
