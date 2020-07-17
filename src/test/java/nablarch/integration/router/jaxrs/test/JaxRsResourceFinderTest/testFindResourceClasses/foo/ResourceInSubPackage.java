package nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testFindResourceClasses.foo;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("subpackage")
public class ResourceInSubPackage {
    @GET
    public void get() {}
}
