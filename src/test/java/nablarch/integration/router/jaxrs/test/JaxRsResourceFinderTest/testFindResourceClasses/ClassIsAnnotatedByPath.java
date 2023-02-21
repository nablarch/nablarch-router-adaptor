package nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testFindResourceClasses;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("class-is-annotated-by-path")
public class ClassIsAnnotatedByPath {
    @GET
    public void get() {}
}
