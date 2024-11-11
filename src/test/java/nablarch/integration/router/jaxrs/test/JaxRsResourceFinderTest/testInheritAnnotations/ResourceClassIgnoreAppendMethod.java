package nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testInheritAnnotations;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

public class ResourceClassIgnoreAppendMethod implements ResourceInterface {
    @Override
    public String simpleGet() {
        return null;
    }

    @Override
    public String getWithPath() {
        return null;
    }

    @POST
    @Path("/append-method")
    public String appendMethod() {
        return null;
    }
}
