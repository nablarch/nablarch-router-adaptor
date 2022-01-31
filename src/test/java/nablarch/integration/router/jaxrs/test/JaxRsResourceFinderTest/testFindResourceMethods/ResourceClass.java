package nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testFindResourceMethods;

import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("resource")
public class ResourceClass {
    @GET
    public void get() {}

    @POST
    private void post() {}

    @PATCH
    public void patch() {}

    public void notAnnotated() {}

    @MyHttpMethod
    public void myHttpMethod() {}
}
