package nablarch.integration.router.jaxrs;

import nablarch.integration.router.PathOptions;
import nablarch.integration.router.jaxrs.test.JaxRsOptionsCollectorTest.FooResource;
import nablarch.integration.router.jaxrs.test.JaxRsOptionsCollectorTest.bar.BarResource;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * {@link JaxRsOptionsCollector} のテスト。
 * 
 * @author Tanaka Tomoyuki
 */
public class JaxRsOptionsCollectorTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testThrowsExceptionIfApplicationPathIsNull() {
        exception.expect(IllegalStateException.class);
        exception.expectMessage("applicationPath is not set.");

        JaxRsOptionsCollector sut = new JaxRsOptionsCollector();
        sut.collect("test");
    }
    
    @Test
    public void testCollect() {
        JaxRsOptionsCollector sut = new JaxRsOptionsCollector();
        sut.setApplicationPath("test");
        
        List<PathOptions> pathOptionsList = sut.collect("nablarch.integration.router.jaxrs.test.JaxRsOptionsCollectorTest");
        
        assertThat(pathOptionsList, containsInAnyOrder(
            allOf(
                hasProperty("path", is("test/foo")),
                hasProperty("options",
                    allOf(
                        hasEntry("controller", FooResource.class.getName()),
                        hasEntry("action", "get")
                    )
                )
            ),
            allOf(
                hasProperty("path", is("test/bar")),
                hasProperty("options",
                    allOf(
                        hasEntry("controller", BarResource.class.getName()),
                        hasEntry("action", "post")
                    )
                )
            )
        ));
    }
}