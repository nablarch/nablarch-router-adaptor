package nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testInheritAnnotations;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/parent-with-interface-path")
public abstract class ParentResourceClassImplementsInterface implements ResourceInterface {
    @GET
    public abstract String simpleGet();

    @GET
    @Path("/get-by-parent")
    public abstract String getWithPath();
}
