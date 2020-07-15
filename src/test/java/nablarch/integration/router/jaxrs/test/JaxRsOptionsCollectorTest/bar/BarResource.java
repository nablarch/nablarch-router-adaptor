package nablarch.integration.router.jaxrs.test.JaxRsOptionsCollectorTest.bar;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("bar")
public class BarResource {
    
    @POST
    public void post() {}
}
