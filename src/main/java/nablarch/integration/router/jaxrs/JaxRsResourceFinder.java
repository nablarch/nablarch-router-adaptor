package nablarch.integration.router.jaxrs;

import nablarch.core.util.ClassTraversal.ClassHandler;
import nablarch.core.util.ResourcesUtil;
import nablarch.core.util.ResourcesUtil.Resources;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * JAX-RS のリソースクラスと、そこに定義されたHTTPメソッドをマッピングしたメソッド定義を検索するクラス。
 *
 * @author Tanaka Tomoyuki
 */
public class JaxRsResourceFinder {

    /**
     * 指定されたパッケージ配下を検索し、{@link javax.ws.rs.Path} アノテーションが設定された
     * クラスおよびメソッドを抽出する。
     * @param basePackage 検索対象のパッケージ
     * @return 検索結果
     */
    public List<JaxRsResource> find(String basePackage) {
        ResourceClassHandler resourceClassHandler = new ResourceClassHandler();

        for (Resources resourcesType : ResourcesUtil.getResourcesTypes(basePackage)) {
            resourcesType.forEach(resourceClassHandler);
        }

        return resourceClassHandler.getJaxRsResourceList();
    }

    private static class ResourceClassHandler implements ClassHandler {
        private final ClassLoader classLoader = this.getClass().getClassLoader();
        private final List<JaxRsResource> jaxRsResourceList = new ArrayList<JaxRsResource>();

        @Override
        public void process(String packageName, String className) {
            try {
                Class<?> clazz = classLoader.loadClass(packageName + "." + className);

                if (isJaxRsResource(clazz)) {
                    List<Method> resourceMethodList = findResourceMethods(clazz);
                    jaxRsResourceList.add(new JaxRsResource(clazz, resourceMethodList));
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        private boolean isJaxRsResource(Class<?> clazz) {
            return clazz.isAnnotationPresent(Path.class);
        }

        private List<Method> findResourceMethods(Class<?> clazz) {
            List<Method> methodList = new ArrayList<Method>();
            for (Method method : clazz.getDeclaredMethods()) {
                if (isAnnotatedByHttpMethod(method)) {
                    methodList.add(method);
                }
            }
            return methodList;
        }

        private boolean isAnnotatedByHttpMethod(Method method) {
            for (Annotation annotation : method.getDeclaredAnnotations()) {
                if (annotation.annotationType().isAnnotationPresent(HttpMethod.class)) {
                    return true;
                }
            }
            return false;
        }

        private List<JaxRsResource> getJaxRsResourceList() {
            return jaxRsResourceList;
        }
    }
}
