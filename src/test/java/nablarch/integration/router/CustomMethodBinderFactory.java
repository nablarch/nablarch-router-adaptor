package nablarch.integration.router;

import nablarch.fw.HandlerWrapper;
import nablarch.fw.MethodBinder;
import nablarch.fw.web.HttpRequest;
import nablarch.fw.web.handler.MethodBinderFactory;

/**
 * テスト用の{@link MethodBinderFactory}実装クラス。
 */
public class CustomMethodBinderFactory implements MethodBinderFactory {

    @Override
    public MethodBinder<HttpRequest, Object> create(final String methodName) {
        return new CustomMethodBinder();
    }

    public static class CustomMethodBinder implements MethodBinder<HttpRequest, Object> {

        @Override
        public HandlerWrapper<HttpRequest, Object> bind(Object delegate) {
            return null;
        }
    }
}
