package nablarch.integration.router.test.ClassTraversalRoutesMappingTest.testNotServletExecutionContext;

import nablarch.fw.ExecutionContext;
import nablarch.fw.web.HttpRequest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.List;

@Path("/foo")
public class FooAction {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> get(HttpRequest request, ExecutionContext context) {
        return Arrays.asList("FooAction", "get() method", "invoked");
    }
}
