package nablarch.integration.router;

import nablarch.fw.MethodBinder;
import nablarch.fw.web.HttpRequest;
import nablarch.fw.web.handler.MethodBinderFactory;

/**
 * {@link RoutesMethodBinder}を生成するファクトリクラス。
 *
 * @author Hisaaki Shioiri
 */
public class RoutesMethodBinderFactory implements MethodBinderFactory {

    @Override
    public MethodBinder<HttpRequest, Object> create(final String methodName) {
        return new RoutesMethodBinder(methodName);
    }

}
