package nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testInheritAnnotations;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/interface-path")
public interface ResourceInterface {
    @GET
    String simpleGet();

    @GET
    @Path("/get-by-interface")
    String getWithPath();
}
