package nablarch.integration.router.jaxrs.test.JaxRsPathOptionsProviderTest.testPathOptionsAreSortedByPathAscending;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/bar")
public class BarResource {

    @GET
    @Path("/one")
    public void two() {}

    @GET
    public void one() {}
}
