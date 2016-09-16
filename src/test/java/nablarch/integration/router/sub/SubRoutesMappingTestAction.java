package nablarch.integration.router.sub;

import nablarch.fw.ExecutionContext;
import nablarch.fw.web.HttpRequest;
import nablarch.integration.router.RoutesMapping;

/**
 * {@link RoutesMapping}のテストで使うアクション。
 */
public class SubRoutesMappingTestAction {

    public String controllerActionForSubPackage(HttpRequest request, ExecutionContext context) {
        return "controllerActionForSubPackage method was invoked.";
    }
}
