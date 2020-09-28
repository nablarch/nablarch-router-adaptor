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
 * {@link PathOptionsProvider} から取得したルーティング定義をベースにActionメソッドを特定するハンドラ。
 * 
 * @author Tanaka Tomoyuki
 */
public class PathOptionsProviderRoutesMapping extends RoutingHandlerSupport implements Initializable {
    private static final Logger LOGGER = LoggerManager.get(PathOptionsProviderRoutesMapping.class);

    private final RouteSet routeSet = new RouteSet();
    private String baseUri = "";
    private PathOptionsProvider pathOptionsProvider;
    private PathOptionsFormatter pathOptionsFormatter = new SimplePathOptionsFormatter();

    @Override
    protected Class<?> getHandlerClass(HttpRequest request, ExecutionContext executionContext) throws ClassNotFoundException {
        try {
            String path = getPath(request, executionContext);
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
    
    private String getPath(HttpRequest request, ExecutionContext executionContext) {
        String path;
        if (executionContext instanceof ServletExecutionContext) {
            path = ((ServletExecutionContext) executionContext).getHttpRequest().getRequestPath();
        } else {
            path = request.getRequestPath();
        }
        String normalized = ARStringUtil.removeStart(path, getBaseUri());
        return normalized.startsWith("/") ? normalized : "/" + normalized;
    }

    @Override
    public void initialize() {
        if (pathOptionsProvider == null) {
            throw new IllegalStateException("pathOptionsProvider is not set.");
        }

        List<PathOptions> pathOptionsList = pathOptionsProvider.provide();

        for (PathOptions pathOptions : pathOptionsList) {
            routeSet.addRoute(pathOptions.getPath() , pathOptions.getOptions());
        }
        
        if (methodBinderFactory == null) {
            setMethodBinderFactory(new RoutesMethodBinderFactory());
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
     * {@link PathOptionsProvider} を設定する。
     * @param pathOptionsProvider {@link PathOptionsProvider}
     */
    public void setPathOptionsProvider(PathOptionsProvider pathOptionsProvider) {
        this.pathOptionsProvider = pathOptionsProvider;
    }

    /**
     * {@link PathOptionsFormatter} を設定する。
     * @param pathOptionsFormatter {@link PathOptionsFormatter}
     */
    public void setPathOptionsFormatter(PathOptionsFormatter pathOptionsFormatter) {
        this.pathOptionsFormatter = pathOptionsFormatter;
    }
}
