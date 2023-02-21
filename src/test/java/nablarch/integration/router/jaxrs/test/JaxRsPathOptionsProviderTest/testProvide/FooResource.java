package nablarch.integration.router.jaxrs.test.JaxRsPathOptionsProviderTest.testProvide;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/foo")
public class FooResource {
    @GET
    public void get() {}
}
