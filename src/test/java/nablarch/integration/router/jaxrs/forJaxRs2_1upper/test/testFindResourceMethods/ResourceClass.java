package nablarch.integration.router.jaxrs.forJaxRs2_1upper.test.testFindResourceMethods;

import javax.ws.rs.PATCH;
import javax.ws.rs.Path;

@Path("resource")
public class ResourceClass {
    @PATCH
    public void patch() {}
}
