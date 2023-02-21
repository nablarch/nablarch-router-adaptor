package nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testFindResourceClasses;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("class-annotated")
public class ClassAndMethodAreAnnotatedByPath {
    @GET
    @Path("method-annotated")
    public void get() {}
}
