package nablarch.integration.router.jaxrs;

import nablarch.integration.router.PathOptions;
import net.unit8.http.router.Options;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * {@link javax.ws.rs.Path} に設定された文字列を解析し、ルーティング定義に変換するクラス。
 *
 * @author Tanaka Tomoyuki
 */
public class JaxRsPathStringParser {
    private static final String PATH_SEPARATOR = "/";
    private final String applicationPath;

    /**
     * コンストラクタ。
     * @param applicationPath アプリケーションパス
     */
    public JaxRsPathStringParser(String applicationPath) {
        this.applicationPath = applicationPath;
    }

    /**
     * 指定されたリソースクラスの情報を解析して、ルーティング定義に変換する。
     * @param jaxRsResource リソースクラスの情報
     * @return ルーティング定義
     */
    public List<PathOptions> parse(JaxRsResource jaxRsResource) {
        List<PathOptions> pathOptionsList = new ArrayList<PathOptions>();
        Class<?> resourceClass = jaxRsResource.getResourceClass();

        for (Method resourceMethod : jaxRsResource.getResourceMethodList()) {
            Options options = Options.newInstance();
            options.put("controller", resourceClass.getName());
            options.put("action", resourceMethod.getName());
            options.put("conditions", buildConditions(resourceMethod));
            
            PathRequirements pathRequirements = buildRouterRequirements(resourceClass, resourceMethod);
            options.put("requirements", pathRequirements.requirements);
            
            PathOptions pathOptions = new PathOptions(pathRequirements.path, options);
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
        StringBuilder routerPath = new StringBuilder();
        Options requirements = Options.newInstance();
        
        String jaxRsPath = buildJaxRsPath(resourceClass, resourceMethod);
        CodePointIterator iterator = new CodePointIterator(jaxRsPath);
        
        while (iterator.hasNext()) {
            int codePoint = iterator.nextInt();
            
            if (codePoint == '{') {
                JaxRsPathParameter jaxRsPathParameter = parseJaxRsPathParameter(iterator);

                routerPath.append("(:").append(jaxRsPathParameter.name).append(')');
                if (jaxRsPathParameter.hasRegex()) {
                    requirements.put(jaxRsPathParameter.name, Pattern.compile(jaxRsPathParameter.regex));
                }
            } else {
                routerPath.appendCodePoint(codePoint);
            }
        }
        
        return new PathRequirements(routerPath.toString(), requirements);
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

    private JaxRsPathParameter parseJaxRsPathParameter(CodePointIterator iterator) {
        StringBuilder parameterName = new StringBuilder();
        while (iterator.hasNext()) {
            int codePoint = iterator.nextInt();
            if (codePoint == ' ') {
                continue;

            } else if (codePoint == '}') {
                return new JaxRsPathParameter(parameterName.toString());

            } else if (codePoint == ':') {
                String regex = parseJaxRsPathParameterRegex(iterator);
                return new JaxRsPathParameter(parameterName.toString(), regex);
            }
            parameterName.appendCodePoint(codePoint);
        }
        throw new IllegalStateException("Parameter segment has no end brackets. (parameterName='" + parameterName + "')");
    }

    private String parseJaxRsPathParameterRegex(CodePointIterator iterator) {
        StringBuilder regex = new StringBuilder();
        StringBuilder spaceBuf = new StringBuilder();
        int braceOpen = 0;
        boolean parameterSegmentClosed = false;
        boolean inRegexValue = false;
        while (iterator.hasNext()) {
            int codePoint = iterator.nextInt();
            if (codePoint == ' ') {
                if (inRegexValue) {
                    spaceBuf.appendCodePoint(codePoint);
                }
                continue;
            } else {
                inRegexValue = true;
            }
            if (codePoint == '{') {
                braceOpen++;

            } else if (codePoint == '}') {
                if (braceOpen == 0) {
                    parameterSegmentClosed = true;
                    break;
                }
                braceOpen--;
            }
            regex.append(spaceBuf.toString());
            regex.appendCodePoint(codePoint);
            spaceBuf.setLength(0);
        }
        if (!parameterSegmentClosed) {
            throw new IllegalStateException("Parameter segment has no end brackets. (regexp='" + regex + "')");
        }
        return regex.toString();
    }

    private static class CodePointIterator {
        private final String text;
        private final int codePointCount;
        private int index;

        private CodePointIterator(String text) {
            this.text = text;
            this.codePointCount = text.codePointCount(0, text.length());
        }

        private boolean hasNext() {
            return index < codePointCount;
        }

        private int nextInt() {
            int codePoint = text.codePointAt(index);
            index++;
            return codePoint;
        }
    }
    
    private static class JaxRsPathParameter {
        private final String name;
        private final String regex;

        private JaxRsPathParameter(String name) {
            this(name, null);
        }

        private JaxRsPathParameter(String name, String regex) {
            this.name = name;
            this.regex = regex;
        }

        private boolean hasRegex() {
            return regex != null;
        }
    }

    private static class PathRequirements {
        private final String path;
        private final Options requirements;

        private PathRequirements(String path, Options requirements) {
            this.path = path;
            this.requirements = requirements;
        }
    }
}
