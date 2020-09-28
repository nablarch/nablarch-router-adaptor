package nablarch.integration.router.test.PathOptionsProviderRoutesMappingTest.testNotServletExecutionContext;

import nablarch.fw.ExecutionContext;
import nablarch.fw.web.HttpRequest;

public class FooAction {

    public String get(HttpRequest request, ExecutionContext context) {
        return "FooAction#get() method is invoked";
    }
}
