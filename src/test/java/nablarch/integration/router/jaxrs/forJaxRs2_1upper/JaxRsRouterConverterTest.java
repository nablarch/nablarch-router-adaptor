package nablarch.integration.router.jaxrs.forJaxRs2_1upper;

import nablarch.integration.router.PathOptions;
import nablarch.integration.router.jaxrs.JaxRsResource;
import nablarch.integration.router.jaxrs.JaxRsRouterConverter;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class JaxRsRouterConverterTest {
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

            @PATCH
            @Path("patch-method")
            void patch() {}
        }

        JaxRsRouterConverter sut = new JaxRsRouterConverter("test");

        List<PathOptions> pathOptionsList = sut.parse(jaxRsResource(TestResource.class, "get", "post", "put", "patch"));

        assertThat(pathOptionsList, containsInAnyOrder(
                hasProperty("path", is("test/test-resource/get-method")),
                hasProperty("path", is("test/test-resource/post-method")),
                hasProperty("path", is("test/test-resource/put-method")),
                hasProperty("path", is("test/test-resource/patch-method"))
        ));
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
