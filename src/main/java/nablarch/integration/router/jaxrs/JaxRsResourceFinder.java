package nablarch.integration.router.jaxrs;

import nablarch.core.util.ClassTraversal.ClassHandler;
import nablarch.core.util.ResourcesUtil;
import nablarch.core.util.ResourcesUtil.Resources;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.Path;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * JAX-RS のリソースクラスと、そこに定義されたHTTPメソッドをマッピングしたメソッド定義を検索するクラス。
 *
 * @author Tanaka Tomoyuki
 */
public class JaxRsResourceFinder {

    /**
     * 指定されたパッケージ配下を検索し、{@link jakarta.ws.rs.Path} アノテーションが設定された
     * クラスおよびメソッドを抽出する。
     * @param basePackage 検索対象のパッケージ
     * @return 検索結果
     */
    public List<JaxRsResource> find(String basePackage) {
        ResourceClassHandler resourceClassHandler = new ResourceClassHandler();

        for (Resources resourcesType : ResourcesUtil.getResourcesTypes(basePackage)) {
            try {
                resourcesType.forEach(resourceClassHandler);
            } finally {
                resourcesType.close();
            }
        }

        return resourceClassHandler.getJaxRsResourceList();
    }

    private static class ResourceClassHandler implements ClassHandler {
        private final ClassLoader classLoader = this.getClass().getClassLoader();
        private final List<JaxRsResource> jaxRsResourceList = new ArrayList<>();

        @Override
        public void process(String packageName, String className) {
            try {
                Class<?> clazz = classLoader.loadClass(packageName + "." + className);

                if (Modifier.isAbstract(clazz.getModifiers()) || clazz.isInterface()) {
                    // 抽象クラスやインターフェースは除外
                    return;
                }

                Class<?> jaxRsResourceClass = findJaxRsResourceClass(clazz);

                if (jaxRsResourceClass != null) {
                    // JAX-RSリソースクラスと判定されたClassクラスに定義されているメソッドをリソースメソッドとして扱う。
                    // それ以外の継承/実装関係にあるクラスのリソースメソッドは無視される
                    List<Method> resourceMethodList = findResourceMethods(jaxRsResourceClass);

                    // JAX-RSリソースクラスそのものは探索対象のパッケージ配下にあった具象クラスとする
                    jaxRsResourceList.add(new JaxRsResource(clazz, jaxRsResourceClass, resourceMethodList));
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * 指定された{@code clazz}がJAX-RSのリソースクラスであれば、そのクラスを返す。そうでない場合、親クラスや
         * インターフェースを再帰的に探索してJAX-RSのリソースクラスを探す。
         * <p>
         * 指定されたクラスが{@link Object}以外の親クラスおよびインターフェースの両方を拡張している場合は、
         * JAX-RS仕様の「Annotations on a super-class take precedence over those on an implemented interface.」
         * <a href="https://jakarta.ee/specifications/restful-ws/3.1/jakarta-restful-ws-spec-3.1.html#annotationinheritance">Annotation Inheritance</a>
         * に従い親クラスを優先して探索する。
         *
         * @param clazz 探索対象のクラス
         * @return 見つかったJAX-RSリソースクラス、見つからなかった場合は{@code null}
         */
        private Class<?> findJaxRsResourceClass(Class<?> clazz) {
            if (isJaxRsResource(clazz)) {
                return clazz;
            } else {
                // 親クラスを優先して探索する
                Class<?> superClass = clazz.getSuperclass();
                if (isJaxRsResource(superClass)) {
                    return superClass;
                }

                // インタフェースを検索する
                for (Class<?> interfaceClass : clazz.getInterfaces()) {
                    if (isJaxRsResource(interfaceClass)) {
                        return interfaceClass;
                    }
                }

                if (!superClass.equals(Object.class)) {
                    return findJaxRsResourceClass(superClass);
                }

                return null;
            }
        }

        /**
         * 指定された{@link Class}がJAX-RSリソースクラスの場合{@code true}を返す。
         *
         * @param clazz 確認対象のクラス
         * @return JAX-RSリソースクラスの場合{@code true}
         */
        private boolean isJaxRsResource(Class<?> clazz) {
            return clazz.isAnnotationPresent(Path.class);
        }

        private List<Method> findResourceMethods(Class<?> clazz) {
            List<Method> methodList = new ArrayList<>();
            for (Method method : clazz.getDeclaredMethods()) {
                if (isAnnotatedByHttpMethod(method)) {
                    methodList.add(method);
                }
            }
            return methodList;
        }

        private boolean isAnnotatedByHttpMethod(Method method) {
            int httpMethodAnnotationCount = 0;
            for (Annotation annotation : method.getDeclaredAnnotations()) {
                if (annotation.annotationType().isAnnotationPresent(HttpMethod.class)) {
                    httpMethodAnnotationCount++;
                }
            }
            
            if (2 <= httpMethodAnnotationCount) {
                throw new RuntimeException("'" + method.getName() + "' method has multiple HTTP method annotations.");
            } else {
                return httpMethodAnnotationCount == 1;
            }
        }

        private List<JaxRsResource> getJaxRsResourceList() {
            return jaxRsResourceList;
        }
    }
}
