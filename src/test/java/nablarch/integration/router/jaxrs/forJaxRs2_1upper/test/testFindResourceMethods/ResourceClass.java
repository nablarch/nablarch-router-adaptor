package nablarch.integration.router.jaxrs.forJaxRs2_1upper.test.testFindResourceMethods;

import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.Path;

@Path("resource")
public class ResourceClass {
    @PATCH
    public void patch() {}
}
