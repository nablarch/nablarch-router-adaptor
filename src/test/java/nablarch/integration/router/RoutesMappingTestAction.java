package nablarch.integration.router;

import nablarch.fw.ExecutionContext;
import nablarch.fw.web.HttpRequest;

/**
 * {@link RoutesMapping}のテストで使うアクション。
 */
public class RoutesMappingTestAction {

    public String root(HttpRequest request, ExecutionContext context) {
        return "root method was invoked.";
    }

    public String get(HttpRequest request, ExecutionContext context) {
        return "get method was invoked.";
    }

    public String post(HttpRequest request, ExecutionContext context) {
        return "post method was invoked.";
    }

    public String controllerAction(HttpRequest request, ExecutionContext context) {
        return "controllerAction method was invoked.";
    }

    public String controllerActionWithId(HttpRequest request, ExecutionContext context) {
        return "controllerActionWithId method was invoked.";
    }
}
