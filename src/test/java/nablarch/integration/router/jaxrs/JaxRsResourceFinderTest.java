package nablarch.integration.router.jaxrs;

import nablarch.core.util.ClassTraversal;
import nablarch.core.util.ResourcesUtil;
import nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testFindResourceMethods.ResourceClass;
import nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testFindResourceClasses.ClassAndMethodAreAnnotatedByPath;
import nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testFindResourceClasses.ClassIsAnnotatedByPath;
import nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testFindResourceClasses.MethodIsAnnotatedByPath;
import nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testFindResourceClasses.foo.ResourceInSubPackage;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.net.URL;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * {@link JaxRsResourceFinder} のテスト。
 * 
 * @author Tanaka Tomoyuki
 */
public class JaxRsResourceFinderTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();
    
    private JaxRsResourceFinder sut;

    @Before
    public void before() {
        sut = new JaxRsResourceFinder();
    }

    @Test
    public void testFindResourceClasses() {
        List<JaxRsResource> jaxRsResourceList = sut.find("nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testFindResourceClasses");

        assertThat(jaxRsResourceList, containsInAnyOrder(
            hasProperty("resourceClass", equalTo(ClassIsAnnotatedByPath.class)),
            hasProperty("resourceClass", equalTo(ClassAndMethodAreAnnotatedByPath.class)),
            hasProperty("resourceClass", equalTo(ResourceInSubPackage.class))
        ));
    }

    @Test
    public void testFindResourceMethods() {
        List<JaxRsResource> jaxRsResourceList = sut.find("nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testFindResourceMethods");

        JaxRsResource jaxRsResource = jaxRsResourceList.get(0);
        assertThat(jaxRsResource.getResourceClass(), Matchers.<Class<?>>equalTo(ResourceClass.class));

        assertThat(jaxRsResource.getResourceMethodList(), containsInAnyOrder(
            hasProperty("name", is("get")),
            hasProperty("name", is("post")),
            hasProperty("name", is("myHttpMethod"))
        ));
    }
    
    @Test
    public void testThrowsExceptionIfClassNotFound() {
        ResourcesUtil.addResourcesFactory("file", new ResourcesUtil.ResourcesFactory() {
            @Override
            public ResourcesUtil.Resources create(URL url, String rootPackage, String rootDir) {
                return new ResourcesUtil.Resources() {
                    @Override
                    public void forEach(ClassTraversal.ClassHandler handler) {
                        handler.process("not_exists_package", "NotExistsClass");
                    }

                    @Override public void close() {}
                };
            }
        });
        
        try {
            exception.expect(RuntimeException.class);
            exception.expectCause(Matchers.<Throwable>instanceOf(ClassNotFoundException.class));

            sut.find("nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testThrowsExceptionIfClassNotFound");

        } finally {
            ResourcesUtil.addResourcesFactory("file", new ResourcesUtil.ResourcesFactory() {
                @Override
                public ResourcesUtil.Resources create(final URL url, final String rootPackage, final String rootDir) {
                    return new ResourcesUtil.FileSystemResources(ResourcesUtil.getBaseDir(url, rootDir), rootPackage, rootDir);
                }
            });
        }
    }
}