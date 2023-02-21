package nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testThrowsExceptionIfHttpMethodAnnotationIsDuplicate;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/duplicate")
public class DuplicateResource {
    
    @GET
    @POST
    public void get() {}
}
