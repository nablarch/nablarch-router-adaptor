package nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testFindResourceClasses;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("class-is-annotated-by-path")
public class ClassIsAnnotatedByPath {
    @GET
    public void get() {}
}
