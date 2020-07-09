package nablarch.integration.router.test.ClassTraversalRoutesMappingTest.testSimpleRouting;

import nablarch.fw.ExecutionContext;
import nablarch.fw.web.HttpRequest;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.List;

@Path("simple")
public class SimpleAction {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> get(HttpRequest request, ExecutionContext context) {
        return Arrays.asList("SimpleAction", "get() method", "invoked");
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> post(HttpRequest request, ExecutionContext context) {
        return Arrays.asList("SimpleAction", "post() method", "invoked");
    }
}
