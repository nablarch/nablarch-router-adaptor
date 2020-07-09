package nablarch.integration.router.jaxrs.test.JaxRsOptionsCollectorTest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/foo")
public class FooResource {
    @GET
    public void get() {}
}
