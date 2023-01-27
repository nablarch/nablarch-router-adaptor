package nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testFindResourceMethods;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("resource")
public class ResourceClass {
    @GET
    public void get() {}

    @POST
    private void post() {}

    public void notAnnotated() {}

    @MyHttpMethod
    public void myHttpMethod() {}
}
