package nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testFindResourceClasses;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

public class MethodIsAnnotatedByPath {
    @GET
    @Path("/method-is-annotated-by-path")
    public void get() {}
}
