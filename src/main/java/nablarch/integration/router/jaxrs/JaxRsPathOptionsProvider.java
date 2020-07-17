package nablarch.integration.router.jaxrs;

import nablarch.integration.router.PathOptionsProvider;
import nablarch.integration.router.PathOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link javax.ws.rs.Path} アノテーションが設定されたクラスを探索してルーティング定義を収集するクラス。
 *
 * @author Tanaka Tomoyuki
 */
public class JaxRsPathOptionsProvider implements PathOptionsProvider {
    private String basePackage;
    private String applicationPath;
    
    @Override
    public List<PathOptions> provide() {
        if (applicationPath == null) {
            throw new IllegalStateException("applicationPath is not set.");
        }
        if (basePackage == null) {
            throw new IllegalStateException("basePackage is not set.");
        }

        JaxRsResourceFinder resourceFinder = new JaxRsResourceFinder();
        JaxRsRouterConverter pathStringParser = new JaxRsRouterConverter(applicationPath);
        
        List<PathOptions> pathOptionsList = new ArrayList<PathOptions>();
        
        for (JaxRsResource jaxRsResource : resourceFinder.find(basePackage)) {
            pathOptionsList.addAll(pathStringParser.parse(jaxRsResource));
        }
        
        return pathOptionsList;
    }

    /**
     * 検索ルートとなるパッケージを設定する。
     * @param basePackage 検索ルートとなるパッケージ
     */
    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    /**
     * アプリケーションパスを設定する。
     *  @param applicationPath アプリケーションパス
     */
    public void setApplicationPath(String applicationPath) {
        this.applicationPath = applicationPath;
    }
}
