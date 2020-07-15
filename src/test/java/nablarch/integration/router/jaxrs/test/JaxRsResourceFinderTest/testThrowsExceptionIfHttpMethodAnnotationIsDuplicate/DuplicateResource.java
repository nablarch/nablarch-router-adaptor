package nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testThrowsExceptionIfHttpMethodAnnotationIsDuplicate;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/duplicate")
public class DuplicateResource {
    
    @GET
    @POST
    public void get() {}
}
