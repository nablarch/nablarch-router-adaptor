package nablarch.integration.router.jaxrs.forJaxRs2_1upper;

import nablarch.integration.router.jaxrs.JaxRsResource;
import nablarch.integration.router.jaxrs.JaxRsResourceFinder;
import nablarch.integration.router.jaxrs.forJaxRs2_1upper.test.testFindResourceMethods.ResourceClass;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class JaxRsResourceFinderTest {
    private JaxRsResourceFinder sut;

    @Before
    public void before() {
        sut = new JaxRsResourceFinder();
    }

    @Test
    public void testFindResourceMethods() {
        List<JaxRsResource> jaxRsResourceList = sut.find("nablarch.integration.router.jaxrs.forJaxRs2_1upper.test.testFindResourceMethods");

        JaxRsResource jaxRsResource = jaxRsResourceList.get(0);
        assertThat(jaxRsResource.getResourceClass(), Matchers.<Class<?>>equalTo(ResourceClass.class));

        assertThat(jaxRsResource.getResourceMethodList(), contains(
                hasProperty("name", is("patch"))
        ));
    }
}
