package nablarch.integration.router.test.ClassTraversalRoutesMappingTest.testRoutingLog;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

@Path("log-test")
public class LogTestAction {

    @GET
    @Path("/bbb/{param1}")
    public void getMethod() {}

    @POST
    @Path("/aaa/{param2:\\d+}")
    public void postMethod() {}

    @PUT
    @Path("/ccc")
    public void putMethod() {}
}
