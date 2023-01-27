package nablarch.integration.router.jaxrs.test.JaxRsPathOptionsProviderTest.testPathOptionsAreSortedByPathAscending;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;

@Path("/foo")
public class FooResource {

    @GET
    public void aaa() {}

    @POST
    @Path("/fizz")
    public void bbb() {}

    @POST
    @Path("/fizz/beta")
    public void ccc() {}

    @PUT
    @Path("/fizz/{param}")
    public void ddd() {}

    @PUT
    @Path("/fizz/alpha")
    public void eee() {}
}
