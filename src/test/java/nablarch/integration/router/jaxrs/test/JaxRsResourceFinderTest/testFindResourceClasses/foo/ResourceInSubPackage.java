package nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testFindResourceClasses.foo;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("subpackage")
public class ResourceInSubPackage {
    @GET
    public void get() {}
}
