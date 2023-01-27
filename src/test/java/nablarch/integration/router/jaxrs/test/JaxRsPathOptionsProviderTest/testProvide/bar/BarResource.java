package nablarch.integration.router.jaxrs.test.JaxRsPathOptionsProviderTest.testProvide.bar;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("bar")
public class BarResource {
    
    @POST
    public void post() {}
}
