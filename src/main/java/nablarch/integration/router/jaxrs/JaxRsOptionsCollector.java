package nablarch.integration.router.jaxrs;

import nablarch.integration.router.ClassTraversalOptionsCollector;
import nablarch.integration.router.PathOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link javax.ws.rs.Path} アノテーションが設定されたクラスを探索してルーティング定義を収集するクラス。
 *
 * @author Tanaka Tomoyuki
 */
public class JaxRsOptionsCollector implements ClassTraversalOptionsCollector {
    private String applicationPath;
    
    @Override
    public List<PathOptions> collect(String basePackage) {
        if (applicationPath == null) {
            throw new IllegalStateException("applicationPath is not set.");
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
     * アプリケーションパスを設定する。
     *  @param applicationPath アプリケーションパス
     */
    public void setApplicationPath(String applicationPath) {
        this.applicationPath = applicationPath;
    }
}
