package nablarch.integration.router;

import nablarch.fw.ExecutionContext;
import nablarch.fw.HandlerWrapper;
import nablarch.fw.MethodBinder;
import nablarch.fw.handler.MethodBinding;
import nablarch.fw.web.HttpRequest;

import java.lang.reflect.Method;

/**
 * ルーティング用の{@link MethodBinder}の実装クラス。
 *
 * @author Naoki Yamamoto
 */
public class RoutesMethodBinder implements MethodBinder<HttpRequest, Object> {

    /** ディスパッチするメソッド名 */
    private final String methodName;

    /**
     * コンストラクタ。
     *
     * @param methodName メソッド名
     */
    public RoutesMethodBinder(final String methodName) {
        this.methodName = methodName;
    }

    @Override
    public HandlerWrapper<HttpRequest, Object> bind(final Object delegate) {
        return new MethodBinding<HttpRequest, Object>(delegate) {
            @Override
            protected Method getMethodBoundTo(final HttpRequest httpRequest, final ExecutionContext executionContext) {
                try {
                    return delegate.getClass().getMethod(methodName, HttpRequest.class, ExecutionContext.class);
                } catch (NoSuchMethodException e) {
                    return null;
                }
            }
        };
    }
}
