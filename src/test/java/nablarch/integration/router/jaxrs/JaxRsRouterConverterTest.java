package nablarch.integration.router.jaxrs;

import nablarch.integration.router.PathOptions;
import net.unit8.http.router.Options;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * {@link JaxRsRouterConverter} のテスト。
 *
 * @author Tanaka Tomoyuki
 */
public class JaxRsRouterConverterTest {
    @Test
    public void testPathAnnotatedOnlyClass() {
        @Path("test-resource")
        class TestResource {
            @GET
            void get() {}
        }

        JaxRsRouterConverter sut = new JaxRsRouterConverter("test");

        List<PathOptions> pathOptionsList = sut.parse(jaxRsResource(TestResource.class, "get"));

        assertThat(pathOptionsList, contains(hasProperty("path", is("test/test-resource"))));
    }
    
    @Test
    public void testPathAnnotatedClassAndMethod() {
        @Path("test-resource")
        class TestResource {
            @GET
            @Path("get-method")
            void get() {}
        }
        
        JaxRsRouterConverter sut = new JaxRsRouterConverter("test");

        List<PathOptions> pathOptionsList = sut.parse(jaxRsResource(TestResource.class, "get"));

        assertThat(pathOptionsList, contains(hasProperty("path", is("test/test-resource/get-method"))));
    }

    @Test
    public void testPathWhenExistsSeparatorsEachPathValues() {
        @Path("/test-resource/")
        class TestResource {
            @GET
            @Path("/get-method/")
            void get() {}
        }

        JaxRsRouterConverter sut = new JaxRsRouterConverter("/test/");

        List<PathOptions> pathOptionsList = sut.parse(jaxRsResource(TestResource.class, "get"));

        assertThat(pathOptionsList, contains(hasProperty("path", is("/test/test-resource/get-method/"))));
    }
    
    @Test
    public void testOptionsControllerIsClassName() {
        @Path("test-resource")
        class TestResource {
            @GET
            void get() {}
        }

        JaxRsRouterConverter sut = new JaxRsRouterConverter("test");

        List<PathOptions> pathOptionsList = sut.parse(jaxRsResource(TestResource.class, "get"));

        assertThat(pathOptionsList, contains(
            hasProperty("options", hasEntry("controller", TestResource.class.getName())                 )
        ));
    }

    @Test
    public void testOptionsActionIsMethodName() {
        @Path("test-resource")
        class TestResource {
            @GET
            void getMethod() {}
        }

        JaxRsRouterConverter sut = new JaxRsRouterConverter("test");

        List<PathOptions> pathOptionsList = sut.parse(jaxRsResource(TestResource.class, "getMethod"));

        assertThat(pathOptionsList, contains(
            hasProperty("options", hasEntry("action", "getMethod"))
        ));
    }

    @Test
    public void testOptionsConditionMethodIsHttpMethodName() {
        @Path("test-resource")
        class TestResource {
            @GET
            void getMethod() {}
        }

        JaxRsRouterConverter sut = new JaxRsRouterConverter("test");

        List<PathOptions> pathOptionsList = sut.parse(jaxRsResource(TestResource.class, "getMethod"));

        assertThat(pathOptionsList, contains(
            hasProperty("options",
                hasEntry(is("conditions"), hasEntry("method", "GET"))
            )
        ));
    }

    @Test
    public void testMultipleResourceMethods() {
        @Path("test-resource")
        class TestResource {
            @GET
            @Path("get-method")
            void get() {}
            
            @POST
            @Path("post-method")
            void post() {}

            @PUT
            @Path("put-method")
            void put() {}
        }

        JaxRsRouterConverter sut = new JaxRsRouterConverter("test");

        List<PathOptions> pathOptionsList = sut.parse(jaxRsResource(TestResource.class, "get", "post", "put"));

        assertThat(pathOptionsList, containsInAnyOrder(
            hasProperty("path", is("test/test-resource/get-method")),
            hasProperty("path", is("test/test-resource/post-method")),
            hasProperty("path", is("test/test-resource/put-method"))
        ));
    }
    
    @Test
    public void testRequirementsIsPathParserResult() {
        @Path("test-resource")
        class TestResource {
            @GET
            void get() {}
        }

        final PathRequirements mockPathRequirements = new PathRequirements("mock", Options.newInstance());
        JaxRsRouterConverter sut = new JaxRsRouterConverter("test", new JaxRsPathParser() {
            @Override
            public PathRequirements parse(String jaxRsPath) {
                return mockPathRequirements;
            }
        });
        
        List<PathOptions> pathOptionsList = sut.parse(jaxRsResource(TestResource.class, "get"));

        Options requirements = (Options)pathOptionsList.get(0).getOptions().get("requirements");
        assertThat(requirements, sameInstance(mockPathRequirements.getRequirements()));
    }
    
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testThrowsExceptionIfMethodIsNotAnnotatedByHttpMethod() {
        @Path("test-resource")
        class TestResource {
            @Deprecated
            void get() {}
        }

        JaxRsRouterConverter sut = new JaxRsRouterConverter("test");

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("'get' method has no HttpMethod annotation.");

        sut.parse(jaxRsResource(TestResource.class, "get"));
    }

    private JaxRsResource jaxRsResource(Class<?> clazz, String... methodNames) {
        return new JaxRsResource(clazz, methods(clazz, methodNames));
    }

    private List<Method> methods(Class<?> clazz, String... methodNames) {
        try {
            List<Method> methodList = new ArrayList<Method>(methodNames.length);
            for (String methodName : methodNames) {
                Method method = clazz.getDeclaredMethod(methodName);
                methodList.add(method);
            }
            return methodList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}