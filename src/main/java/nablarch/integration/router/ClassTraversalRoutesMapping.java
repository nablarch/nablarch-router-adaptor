package nablarch.integration.router;

import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import nablarch.core.repository.initialization.Initializable;
import nablarch.fw.ExecutionContext;
import nablarch.fw.web.HttpErrorResponse;
import nablarch.fw.web.HttpRequest;
import nablarch.fw.web.HttpResponse;
import nablarch.fw.web.handler.RoutingHandlerSupport;
import nablarch.fw.web.servlet.ServletExecutionContext;
import net.unit8.http.router.ARStringUtil;
import net.unit8.http.router.Options;
import net.unit8.http.router.RouteSet;
import net.unit8.http.router.RoutingException;

import java.util.List;
import java.util.Map;

/**
 * 指定されたパッケージ配下のクラスを探索してActionメソッドを特定するハンドラ。
 *
 * @author Tanaka Tomoyuki
 */
public class ClassTraversalRoutesMapping extends RoutingHandlerSupport implements Initializable {
    private static final Logger LOGGER = LoggerManager.get(ClassTraversalRoutesMapping.class);

    private final RouteSet routeSet = new RouteSet();
    private String baseUri = "";
    private String basePackage = "";
    private ClassTraversalOptionsCollector optionsCollector;
    private PathOptionsFormatter pathOptionsFormatter = new SimplePathOptionsFormatter();

    @Override
    protected Class<?> getHandlerClass(HttpRequest request, ExecutionContext executionContext) throws ClassNotFoundException {
        try {
            String path = getPath(executionContext);
            Options options = routeSet.recognizePath(path, request.getMethod());

            executionContext.setMethodBinder(methodBinderFactory.create((String) options.get("action")));

            Options params = options.except("controller", "action");
            for (Map.Entry<String, Object> option : params.entrySet()) {
                if (option.getValue() != null) {
                    request.setParam(option.getKey(), option.getValue().toString());
                }
            }

            return Thread.currentThread().getContextClassLoader().loadClass((String) options.get("controller"));
        } catch (RoutingException e) {
            throw new HttpErrorResponse(HttpResponse.Status.NOT_FOUND.getStatusCode(), e);
        }
    }
    
    private String getPath(ExecutionContext executionContext) {
        String path = ((ServletExecutionContext) executionContext).getHttpRequest().getRequestPath();
        return ARStringUtil.removeStart(path, getBaseUri());
    }

    @Override
    public void initialize() {
        if (optionsCollector == null) {
            throw new IllegalStateException("optionsCollector is not set.");
        }

        List<PathOptions> pathOptionsList = optionsCollector.collect(basePackage);

        for (PathOptions pathOptions : pathOptionsList) {
            routeSet.addRoute(pathOptions.getPath() , pathOptions.getOptions());
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.logDebug(pathOptionsFormatter.format(pathOptionsList));
        }
    }

    /**
     * ベースURIを取得する。
     * @return ベースURI
     */
    public String getBaseUri() {
        return baseUri;
    }

    /**
     * ベースURIを設定する。
     * @param baseUri ベースURI
     */
    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri;
    }

    /**
     * 探索のベースとなるパッケージを設定する。
     * @param basePackage 探索のベースとなるパッケージ
     */
    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    /**
     * {@link ClassTraversalOptionsCollector} を設定する。
     * @param optionsCollector {@link ClassTraversalOptionsCollector}
     */
    public void setOptionsCollector(ClassTraversalOptionsCollector optionsCollector) {
        this.optionsCollector = optionsCollector;
    }

    /**
     * {@link PathOptionsFormatter} を設定する。
     * @param pathOptionsFormatter {@link PathOptionsFormatter}
     */
    public void setPathOptionsFormatter(PathOptionsFormatter pathOptionsFormatter) {
        this.pathOptionsFormatter = pathOptionsFormatter;
    }
}
