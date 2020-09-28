package nablarch.integration.router.jaxrs.test.JaxRsPathOptionsProviderTest.testProvide.bar;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("bar")
public class BarResource {
    
    @POST
    public void post() {}
}
