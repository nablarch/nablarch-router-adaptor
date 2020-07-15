package nablarch.integration.router.test.ClassTraversalRoutesMappingTest.testPathParameter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/path-param/{param1}")
public class PathParameterAction {
    
    @GET
    @Path("/get/{param2}")
    public void get() {}
}
