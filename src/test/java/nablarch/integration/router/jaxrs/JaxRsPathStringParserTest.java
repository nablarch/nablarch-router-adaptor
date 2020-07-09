package nablarch.integration.router.jaxrs;

import nablarch.integration.router.PathOptions;
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
import java.util.regex.Pattern;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * {@link JaxRsPathStringParser} のテスト。
 *
 * @author Tanaka Tomoyuki
 */
public class JaxRsPathStringParserTest {
    @Test
    public void testPathAnnotatedOnlyClass() {
        @Path("test-resource")
        class TestResource {
            @GET
            void get() {}
        }

        JaxRsPathStringParser sut = new JaxRsPathStringParser("test");

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
        
        JaxRsPathStringParser sut = new JaxRsPathStringParser("test");

        List<PathOptions> pathOptionsList = sut.parse(jaxRsResource(TestResource.class, "get"));

        assertThat(pathOptionsList, contains(hasProperty("path", is("test/test-resource/get-method"))));
    }

    @Test
    public void testPathAnnotatedOnlyMethod() {
        class TestResource {
            @GET
            @Path("get-method")
            void get() {}
        }

        JaxRsPathStringParser sut = new JaxRsPathStringParser("test");

        List<PathOptions> pathOptionsList = sut.parse(jaxRsResource(TestResource.class, "get"));

        assertThat(pathOptionsList, contains(hasProperty("path", is("test/get-method"))));
    }

    @Test
    public void testPathWhenExistsSeparatorsEachPathValues() {
        @Path("/test-resource/")
        class TestResource {
            @GET
            @Path("/get-method/")
            void get() {}
        }

        JaxRsPathStringParser sut = new JaxRsPathStringParser("/test/");

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

        JaxRsPathStringParser sut = new JaxRsPathStringParser("test");

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

        JaxRsPathStringParser sut = new JaxRsPathStringParser("test");

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

        JaxRsPathStringParser sut = new JaxRsPathStringParser("test");

        List<PathOptions> pathOptionsList = sut.parse(jaxRsResource(TestResource.class, "getMethod"));

        assertThat(pathOptionsList, contains(
            hasProperty("options",
                hasEntry(is("conditions"), hasEntry("method", "GET"))
            )
        ));
    }

    @Test
    public void testPathParameterConverted() {
        @Path("test-resource/{param1}")
        class TestResource {
            @GET
            @Path("/get/{ param2 }/fizz")
            void get() {}
        }

        JaxRsPathStringParser sut = new JaxRsPathStringParser("test");

        List<PathOptions> pathOptionsList = sut.parse(jaxRsResource(TestResource.class, "get"));

        assertThat(pathOptionsList, contains(hasProperty("path", is("test/test-resource/(:param1)/get/(:param2)/fizz"))));
    }

    @Test
    public void testPathParameterHasRegularExpression() {
        @Path("test-resource/{param1:\\d+}")
        class TestResource {
            @GET
            @Path("/get/{ param2 : [a-zA-Z{} ]+ }/fizz")
            void get() {}
        }

        JaxRsPathStringParser sut = new JaxRsPathStringParser("test");

        List<PathOptions> pathOptionsList = sut.parse(jaxRsResource(TestResource.class, "get"));

        assertThat(pathOptionsList, contains(
            allOf(
                hasProperty("path", is("test/test-resource/(:param1)/get/(:param2)/fizz")),
                hasProperty("options",
                    hasEntry(is("requirements"),
                        allOf(
                            hasEntry(
                                is("param1"), allOf(hasToString("\\d+"), instanceOf(Pattern.class))
                            ),
                            hasEntry(
                                is("param2"), allOf(hasToString("[a-zA-Z{} ]+"), instanceOf(Pattern.class))
                            )
                        )
                    )
                )
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

        JaxRsPathStringParser sut = new JaxRsPathStringParser("test");

        List<PathOptions> pathOptionsList = sut.parse(jaxRsResource(TestResource.class, "get", "post", "put"));

        assertThat(pathOptionsList, containsInAnyOrder(
            hasProperty("path", is("test/test-resource/get-method")),
            hasProperty("path", is("test/test-resource/post-method")),
            hasProperty("path", is("test/test-resource/put-method"))
        ));
    }
    
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testThrowsExceptionIfMethodIsNotAnnotatedByHttpMethod() {
        @Path("test-resource")
        class TestResource {
            void get() {}
        }

        JaxRsPathStringParser sut = new JaxRsPathStringParser("test");

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("'get' method has no HttpMethod annotation.");

        sut.parse(jaxRsResource(TestResource.class, "get"));
    }

    @Test
    public void testThrowsExceptionIfParameterSegmentHasNoEndBrackets() {
        @Path("test-resource")
        class TestResource {
            @GET
            @Path("foo/{param1/bar")
            void get() {}
        }

        JaxRsPathStringParser sut = new JaxRsPathStringParser("test");

        exception.expect(IllegalStateException.class);
        exception.expectMessage("Parameter segment has no end brackets. (parameterName='param1/bar')");
        
        sut.parse(jaxRsResource(TestResource.class, "get"));
    }

    @Test
    public void testThrowsExceptionIfParameterSegmentWithRegexpHasNoEndBrackets() {
        @Path("test-resource")
        class TestResource {
            @GET
            @Path("foo/{param1: [a-z]+ /bar")
            void get() {}
        }

        JaxRsPathStringParser sut = new JaxRsPathStringParser("test");

        exception.expect(IllegalStateException.class);
        exception.expectMessage("Parameter segment has no end brackets. (regexp='[a-z]+ /bar')");
        
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