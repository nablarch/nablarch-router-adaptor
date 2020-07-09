package nablarch.integration.router.jaxrs.test.JaxRsOptionsCollectorTest.bar;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

public class BarResource {
    
    @GET
    @Path("bar")
    public void get() {}
}
