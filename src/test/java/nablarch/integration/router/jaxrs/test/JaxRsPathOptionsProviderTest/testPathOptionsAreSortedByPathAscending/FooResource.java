package nablarch.integration.router.jaxrs.test.JaxRsPathOptionsProviderTest.testPathOptionsAreSortedByPathAscending;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

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
