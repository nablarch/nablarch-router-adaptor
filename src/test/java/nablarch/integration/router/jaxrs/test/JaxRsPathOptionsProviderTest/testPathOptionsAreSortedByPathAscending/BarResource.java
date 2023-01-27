package nablarch.integration.router.jaxrs.test.JaxRsPathOptionsProviderTest.testPathOptionsAreSortedByPathAscending;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/bar")
public class BarResource {

    @GET
    @Path("/one")
    public void two() {}

    @GET
    public void one() {}
}
