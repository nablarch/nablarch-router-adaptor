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
    private JaxRsResourceFinder jaxRsResourceFinder;
    
    public JaxRsOptionsCollector() {
        setJaxRsResourceFinder(new JaxRsResourceFinder());
    }
    
    @Override
    public List<PathOptions> collect(String basePackage) {
        if (applicationPath == null) {
            throw new IllegalStateException("applicationPath is not set.");
        }

        JaxRsRouterConverter pathStringParser = new JaxRsRouterConverter(applicationPath);
        
        List<PathOptions> pathOptionsList = new ArrayList<PathOptions>();
        
        for (JaxRsResource jaxRsResource : jaxRsResourceFinder.find(basePackage)) {
            pathOptionsList.addAll(pathStringParser.parse(jaxRsResource));
        }
        
        return pathOptionsList;
    }

    /**
     * {@link JaxRsResourceFinder} を設定する。
     * @param jaxRsResourceFinder {@link JaxRsResourceFinder}
     */
    public void setJaxRsResourceFinder(JaxRsResourceFinder jaxRsResourceFinder) {
        this.jaxRsResourceFinder = jaxRsResourceFinder;
    }

    /**
     * アプリケーションパスを設定する。
     *  @param applicationPath アプリケーションパス
     */
    public void setApplicationPath(String applicationPath) {
        this.applicationPath = applicationPath;
    }
}
