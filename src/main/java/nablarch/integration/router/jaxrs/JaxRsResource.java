package nablarch.integration.router.jaxrs;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 探索結果の JAX-RS のリソースクラスと、HTTPをマッピングしたメソッドの情報を格納したクラス。
 *
 * @author Tanaka Tomoyuki
 */
public class JaxRsResource {
    private final Class<?> resourceClass;
    private final List<Method> resourceMethodList;

    /**
     * コンストラクタ。
     * @param resourceClass リソースクラスの {@link Class} オブジェクト
     * @param resourceMethodList リソースメソッドのリスト
     */
    public JaxRsResource(Class<?> resourceClass, List<Method> resourceMethodList) {
        this.resourceClass = resourceClass;
        this.resourceMethodList = resourceMethodList;
    }

    /**
     * リソースクラスの {@link Class} オブジェクトを取得する。
     * @return リソースクラスの {@link Class} オブジェクト
     */
    public Class<?> getResourceClass() {
        return resourceClass;
    }

    /**
     * リソースメソッドのリストを取得する。
     * @return リソースメソッドのリスト
     */
    public List<Method> getResourceMethodList() {
        return resourceMethodList;
    }
}
