package nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testInheritAnnotations;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/parent-path")
public abstract class ParentResourceClass {
    @GET
    public abstract String simpleGet();

    @GET
    @Path("/get-by-parent")
    public abstract String getWithPath();
}
