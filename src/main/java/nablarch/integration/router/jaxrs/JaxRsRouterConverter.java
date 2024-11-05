package nablarch.integration.router.jaxrs;

import nablarch.integration.router.PathOptions;
import net.unit8.http.router.Options;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.Path;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * JAX-RSのリソースクラスの情報を解析し、ルーティング定義に変換するクラス。
 *
 * @author Tanaka Tomoyuki
 */
public class JaxRsRouterConverter {
    private static final String PATH_SEPARATOR = "/";
    private final String applicationPath;
    private final JaxRsPathParser jaxRsPathParser;

    /**
     * アプリケーションパスのみを指定するコンストラクタ。
     * @param applicationPath アプリケーションパス
     */
    public JaxRsRouterConverter(String applicationPath) {
        this(applicationPath, new JaxRsPathParser());
    }
    
    /**
     * アプリケーションパスと {@link JaxRsPathParser} を指定するコンストラクタ。
     * @param applicationPath アプリケーションパス
     * @param jaxRsPathParser {@link JaxRsPathParser}
     */
    public JaxRsRouterConverter(String applicationPath, JaxRsPathParser jaxRsPathParser) {
        this.applicationPath = applicationPath;
        this.jaxRsPathParser = jaxRsPathParser;
    }

    /**
     * 指定されたリソースクラスの情報を解析して、ルーティング定義に変換する。
     * @param jaxRsResource リソースクラスの情報
     * @return ルーティング定義
     */
    public List<PathOptions> parse(JaxRsResource jaxRsResource) {
        List<PathOptions> pathOptionsList = new ArrayList<PathOptions>();
        Class<?> actualClass = jaxRsResource.getActionClass();
        Class<?> resourceClass = jaxRsResource.getResourceClass();

        for (Method resourceMethod : jaxRsResource.getResourceMethodList()) {
            Options options = Options.newInstance();
            options.put("controller", actualClass.getName());
            options.put("action", resourceMethod.getName());
            options.put("conditions", buildConditions(resourceMethod));
            
            PathRequirements pathRequirements = buildRouterRequirements(resourceClass, resourceMethod);
            options.put("requirements", pathRequirements.getRequirements());
            
            PathOptions pathOptions = new PathOptions(pathRequirements.getPath(), options);
            pathOptionsList.add(pathOptions);
        }

        return pathOptionsList;
    }
    
    private Options buildConditions(Method resourceMethod) {
        for (Annotation annotation : resourceMethod.getDeclaredAnnotations()) {
            Class<?> annotationType = annotation.annotationType();
            
            if (annotationType.isAnnotationPresent(HttpMethod.class)) {
                Options conditions = Options.newInstance();
                conditions.put("method", annotationType.getAnnotation(HttpMethod.class).value());
                return conditions;
            }
        }
        throw new IllegalArgumentException("'" + resourceMethod.getName() + "' method has no HttpMethod annotation.");
    }
    
    private PathRequirements buildRouterRequirements(Class<?> resourceClass, Method resourceMethod) {
        String jaxRsPath = buildJaxRsPath(resourceClass, resourceMethod);
        return jaxRsPathParser.parse(jaxRsPath);
    }

    private String buildJaxRsPath(Class<?> clazz, Method method) {
        String classAppended = appendPath(applicationPath, clazz);
        return appendPath(classAppended, method);
    }

    private String appendPath(String basePath, AnnotatedElement target) {
        if (!target.isAnnotationPresent(Path.class)) {
            return basePath;
        }

        StringBuilder result = new StringBuilder(basePath);
        if (!basePath.endsWith(PATH_SEPARATOR)) {
            result.append(PATH_SEPARATOR);
        }
        String pathValue = target.getAnnotation(Path.class).value();
        if (pathValue.startsWith(PATH_SEPARATOR)) {
            result.append(pathValue.substring(1));
        } else {
            result.append(pathValue);
        }
        return result.toString();
    }
}
