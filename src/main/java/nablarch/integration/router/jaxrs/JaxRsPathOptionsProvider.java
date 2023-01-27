package nablarch.integration.router.jaxrs;

import nablarch.integration.router.PathOptionsProvider;
import nablarch.integration.router.PathOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * {@link jakarta.ws.rs.Path} アノテーションが設定されたクラスを探索してルーティング定義を収集するクラス。
 *
 * @author Tanaka Tomoyuki
 */
public class JaxRsPathOptionsProvider implements PathOptionsProvider {
    private static final Comparator<PathOptions> ORDER_BY_PATH_ASC = new Comparator<PathOptions>() {
        @Override
        public int compare(PathOptions left, PathOptions right) {
            return left.getPath().compareTo(right.getPath());
        }
    };

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

        /*
         * http-request-router はルーティング定義のリストを順番に調べて、
         * 最初にマッチした定義を使用するようになっている。
         *
         * したがって、以下のような順番でルーティング定義がリストに入っていると、
         * "/foo" へのリクエストが "/foo/(:param)" の定義とマッチしてしまう。
         *
         * 1. "/foo/(:param)"
         * 2. "/foo"
         *
         * これを回避するため、定義のリストをパスの昇順でソートしている。
         */
        Collections.sort(pathOptionsList, ORDER_BY_PATH_ASC);

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
