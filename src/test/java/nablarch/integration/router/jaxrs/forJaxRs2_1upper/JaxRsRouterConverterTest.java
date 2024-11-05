package nablarch.integration.router.jaxrs.forJaxRs2_1upper;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import nablarch.integration.router.PathOptions;
import nablarch.integration.router.jaxrs.JaxRsResource;
import nablarch.integration.router.jaxrs.JaxRsRouterConverter;
import org.junit.Test;

public class JaxRsRouterConverterTest {
    @Test
    public void testMultipleResourceMethods() {
        @Path("test-resource")
        class TestResource {
            @GET
            @Path("get-method")
            void get() {
            }

            @POST
            @Path("post-method")
            void post() {
            }

            @PUT
            @Path("put-method")
            void put() {
            }

            @PATCH
            @Path("patch-method")
            void patch() {
            }
        }

        JaxRsRouterConverter sut = new JaxRsRouterConverter("test");

        List<PathOptions> pathOptionsList = sut.parse(jaxRsResource(TestResource.class, TestResource.class, "get", "post", "put", "patch"));

        assertThat(pathOptionsList, containsInAnyOrder(
                hasProperty("path", is("test/test-resource/get-method")),
                hasProperty("path", is("test/test-resource/post-method")),
                hasProperty("path", is("test/test-resource/put-method")),
                hasProperty("path", is("test/test-resource/patch-method"))
        ));
    }

    private JaxRsResource jaxRsResource(Class<?> actionClass, Class<?> resourceClass, String... methodNames) {
        return new JaxRsResource(actionClass, resourceClass, methods(resourceClass, methodNames));
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
