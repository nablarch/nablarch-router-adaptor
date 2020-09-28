package nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testFindResourceClasses;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

public class MethodIsAnnotatedByPath {
    @GET
    @Path("/method-is-annotated-by-path")
    public void get() {}
}
