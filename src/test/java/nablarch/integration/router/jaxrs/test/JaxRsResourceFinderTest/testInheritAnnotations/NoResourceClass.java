package nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testInheritAnnotations;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

public class NoResourceClass extends ParentPlainClass implements PlainInterface {
    @GET
    public String simpleGet() {
        return null;
    }

    @GET
    @Path("/get-by-parent")
    public String getWithPath() {
        return null;
    }
}
