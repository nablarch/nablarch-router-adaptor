package nablarch.integration.router.test.PathOptionsProviderRoutesMappingTest.testSimpleRouting;

import nablarch.fw.ExecutionContext;
import nablarch.fw.web.HttpRequest;

public class SimpleAction {
    public String get(HttpRequest request, ExecutionContext context) {
        return "SimpleAction#get() method is invoked";
    }
    
    public String post(HttpRequest request, ExecutionContext context) {
        return "SimpleAction#post() method is invoked";
    }
}
