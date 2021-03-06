package nablarch.integration.router;

import mockit.Mocked;
import nablarch.fw.ExecutionContext;
import nablarch.fw.Result;
import nablarch.fw.web.HttpRequest;
import nablarch.fw.web.HttpResponse;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * {@link RoutesMethodBinder}のテスト。
 */
public class RoutesMethodBinderTest {

    @Mocked
    private HttpRequest request;

    private final ExecutionContext unusedContext = null;

    /**
     * 呼び出しメソッドのシグネチャが正しい場合、
     * メソッドが呼ばれ、メソッドの戻り値がそのまま返されること。
     */
    @Test
    public void bindForCorrectMethod() {

        final RoutesMethodBinder sut = new RoutesMethodBinder("handle");

        String response = (String) sut.bind(new Action()).handle(request, unusedContext);

        assertThat(response, is("invoking!!!"));
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
