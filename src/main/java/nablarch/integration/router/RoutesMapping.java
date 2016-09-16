package nablarch.integration.router;

import nablarch.core.repository.initialization.Initializable;
import nablarch.core.util.FileUtil;
import nablarch.fw.ExecutionContext;
import nablarch.fw.web.HttpErrorResponse;
import nablarch.fw.web.HttpRequest;
import nablarch.fw.web.handler.RoutingHandlerSupport;
import nablarch.fw.web.servlet.ServletExecutionContext;
import net.unit8.http.router.ARStringUtil;
import net.unit8.http.router.Options;
import net.unit8.http.router.Routes;
import net.unit8.http.router.RoutingException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Map;

/**
 * Routes定義ファイルをベースにActionメソッドを特定するハンドラ。
 *
 * 本ハンドラを使用することで、自由なURLを使用することができる。
 *
 * @author kawasima
 * @author Kiyohito Itoh
 */
public class RoutesMapping
        extends RoutingHandlerSupport implements Initializable {

    private static volatile boolean loading = false;
    private static long lastLoaded = -1;

    private String baseUri;

    private URL routesUrl;
    private long checkInterval;
    private String basePackage;

    /**
     * コンストラクタ。
     * <p>
     * デフォルトで以下のプロパティを設定する。
     * <pre>
     * baseUri: ""
     * routes: routes.xml
     * checkInterval: 0L
     * </pre>
     */
    public RoutesMapping() {
        setBaseUri("");
        setRoutes("routes.xml");
        setCheckInterval(0L);
    }

    /**
     * Routes定義にしたがい、リクエストのパスからハンドラのクラスを返す。
     *
     * リクエストパスから処理対象のコントローラが特定できない場合には、
     * 404を表す{@link HttpErrorResponse}を送出する。
     *
     * @param request リクエスト
     * @param executionContext 実行コンテキスト
     * @return Handlerクラス
     * @throws ClassNotFoundException クラス不明例外
     */
    @Override
    protected Class<?> getHandlerClass(final HttpRequest request,
                                       final ExecutionContext executionContext) throws ClassNotFoundException {
        try {
            reloadRoutes();

            String path;
            if (executionContext instanceof ServletExecutionContext) {
                path = ((ServletExecutionContext) executionContext)
                        .getHttpRequest().getRequestPath();
            } else {
                path = request.getRequestPath();
            }
            String normalizedPath = ARStringUtil.removeStart(path, baseUri);
            if (!normalizedPath.startsWith("/")) {
                normalizedPath = "/" + normalizedPath;
            }
            final Options options = Routes.recognizePath(
                    normalizedPath,
                    request.getMethod());
            final String controller = options.getString("controller");

            executionContext.setMethodBinder(methodBinderFactory.create(options.getString("action")));

            final Options params = options.except("controller", "action");

            for (Map.Entry<String, Object> paramPair : params.entrySet()) {
                if (paramPair.getValue() != null) {
                    request.setParam(paramPair.getKey(), paramPair.getValue().toString());
                }
            }
            final ClassLoader loader = Thread.currentThread().getContextClassLoader();
            return loader.loadClass(basePackage + "." + controller + "Action");
        } catch (RoutingException e) {
            throw new HttpErrorResponse(404, e);
        }
    }

    /**
     * routes定義ファイルのプロトコルが"file"かつ更新されている場合、再読み込みする。
     */
    private void reloadRoutes() {
        if (loading || routesIsNotFile()) {
            return;
        }
        try {
            final File routesFile = new File(URLDecoder.decode(routesUrl.getPath(), "UTF-8"));
            final int milli = 1000;
            if (lastLoaded < 0 || checkInterval >= 0 &&
                    System.currentTimeMillis() > lastLoaded + checkInterval * milli) {
                synchronized(this) {
                    if (!loading) {
                        loading = true;
                    } else {
                        return;
                    }
                }
                if (loading) {
                    try {
                        final long lastModified = routesFile.lastModified();
                        if (lastModified > lastLoaded) {
                            Routes.load(routesFile);
                            lastLoaded = System.currentTimeMillis();
                        }
                    } finally {
                        loading = false;
                    }
                }
            }
        } catch (IOException ignore) {
            // do nothing.
        }
    }

    /**
     * @return boolean
     */
    private boolean routesIsNotFile() {
        return !routesUrl.getProtocol().equals("file");
    }

    /**
     * @param routes ルート
     */
    public void setRoutes(final String routes) {
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        routesUrl = loader.getResource(routes);
        if (routesUrl == null) {
            throw new IllegalArgumentException(
                    "routes resource could not be found. routes = [" + routes + "]");
        }
    }

    /**
     * @param checkInterval インターバル
     */
    public void setCheckInterval(final long checkInterval) {
        this.checkInterval = checkInterval;
    }

    /**
     * @return basePackage
     */
    public String getBasePackage() {
        return basePackage;
    }

    /**
     * @param basePackage ベースパッケージ
     */
    public void setBasePackage(final String basePackage) {
        this.basePackage = basePackage;
    }

    /**
     * @param baseUri ベースURI
     */
    public void setBaseUri(final String baseUri) {
        this.baseUri = baseUri;
    }

    /**
     * 初期化処理
     */
    public void initialize() {
        if (routesIsNotFile()) {
            // routes定義ファイルのプロトコルがfile以外の場合、
            // 初期化処理時のみロードする。
            InputStream in = null;
            try {
                in = routesUrl.openStream();
                Routes.load(in);
            } catch (IOException ignored) {
                // do nothing.
            } finally {
                FileUtil.closeQuietly(in);
            }
        } else {
            reloadRoutes();
        }
        if (methodBinderFactory == null) {
            // メソッドバインダーファクトリが設定されていない場合、
            // RoutesMethodBinderFactoryを利用する。
            setMethodBinderFactory(new RoutesMethodBinderFactory());
        }
    }
}
