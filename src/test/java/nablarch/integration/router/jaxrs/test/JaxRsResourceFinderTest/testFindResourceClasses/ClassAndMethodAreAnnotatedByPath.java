package nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testFindResourceClasses;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("class-annotated")
public class ClassAndMethodAreAnnotatedByPath {
    @GET
    @Path("method-annotated")
    public void get() {}
}
