package nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testInheritAnnotations;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/resource-path")
public class ResourceClassPreferParent extends ParentResourceClass {
    @POST // 変更
    @Override
    public String simpleGet() {
        return null;
    }

    @POST // 変更
    @Path("/get-by-resource") // 変更
    @Override
    public String getWithPath() {
        return null;
    }
}
