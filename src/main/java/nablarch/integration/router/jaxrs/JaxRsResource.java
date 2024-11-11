package nablarch.integration.router.jaxrs;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 探索結果の JAX-RS のアクションクラスとリソースクラス、HTTPをマッピングしたメソッドの情報を格納したクラス。
 * <p>
 * アクションクラスとリソースクラスは{@link jakarta.ws.rs.Path}などの JAX-RS のアノテーションがアクションクラスに
 * 設定されている場合は同一となるが、アクションクラスが親クラスを継承していたりインターフェースを実装しており、
 * JAX-RS のアノテーションが付与されているのが上位のクラスになる場合は別々となる。
 *
 * @author Tanaka Tomoyuki
 */
public class JaxRsResource {
    private final Class<?> actionClass;
    private final Class<?> resourceClass;
    private final List<Method> resourceMethodList;

    /**
     * コンストラクタ。
     * @param actionClass アクションクラスの {@link Class} オブジェクト
     * @param resourceClass リソースクラスの {@link Class} オブジェクト
     * @param resourceMethodList リソースメソッドのリスト
     */
    public JaxRsResource(Class<?> actionClass, Class<?> resourceClass, List<Method> resourceMethodList) {
        this.actionClass = actionClass;
        this.resourceClass = resourceClass;
        this.resourceMethodList = resourceMethodList;
    }

    /**
     * アクションクラスの {@link Class} オブジェクトを取得する。
     * @return アクションクラスの {@link Class} オブジェクト
     */
    public Class<?> getActionClass() {
        return actionClass;
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
