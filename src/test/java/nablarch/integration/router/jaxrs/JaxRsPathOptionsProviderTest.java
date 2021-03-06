package nablarch.integration.router.jaxrs;

import nablarch.integration.router.PathOptions;
import nablarch.integration.router.jaxrs.test.JaxRsPathOptionsProviderTest.testProvide.FooResource;
import nablarch.integration.router.jaxrs.test.JaxRsPathOptionsProviderTest.testProvide.bar.BarResource;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * {@link JaxRsPathOptionsProvider} のテスト。
 * 
 * @author Tanaka Tomoyuki
 */
public class JaxRsPathOptionsProviderTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testThrowsExceptionIfApplicationPathIsNull() {
        exception.expect(IllegalStateException.class);
        exception.expectMessage("applicationPath is not set.");

        JaxRsPathOptionsProvider sut = new JaxRsPathOptionsProvider();
        sut.setBasePackage("test");
        sut.provide();
    }

    @Test
    public void testThrowsExceptionIfBasePackageIsNull() {
        exception.expect(IllegalStateException.class);
        exception.expectMessage("basePackage is not set.");

        JaxRsPathOptionsProvider sut = new JaxRsPathOptionsProvider();
        sut.setApplicationPath("test");
        sut.provide();
    }
    
    @Test
    public void testProvide() {
        JaxRsPathOptionsProvider sut = new JaxRsPathOptionsProvider();
        sut.setApplicationPath("test");
        sut.setBasePackage("nablarch.integration.router.jaxrs.test.JaxRsPathOptionsProviderTest.testProvide");
        
        List<PathOptions> pathOptionsList = sut.provide();
        
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

    @Test
    public void testPathOptionsAreSortedByPathAscending() {
        JaxRsPathOptionsProvider sut = new JaxRsPathOptionsProvider();
        sut.setApplicationPath("/test");
        sut.setBasePackage("nablarch.integration.router.jaxrs.test.JaxRsPathOptionsProviderTest.testPathOptionsAreSortedByPathAscending");

        List<PathOptions> pathOptionsList = sut.provide();

        assertThat(pathOptionsList, contains(
            hasProperty("path", is("/test/bar")),
            hasProperty("path", is("/test/bar/one")),
            hasProperty("path", is("/test/foo")),
            hasProperty("path", is("/test/foo/fizz")),
            hasProperty("path", is("/test/foo/fizz/(:param)")),
            hasProperty("path", is("/test/foo/fizz/alpha")),
            hasProperty("path", is("/test/foo/fizz/beta"))
        ));
    }
}