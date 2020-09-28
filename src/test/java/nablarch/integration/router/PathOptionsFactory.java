package nablarch.integration.router;

import net.unit8.http.router.Options;

import java.lang.reflect.Method;

/**
 * テスト用の {@link PathOptions} の生成をサポートするクラス。
 * 
 * @author Tanaka Tomoyuki
 */
public class PathOptionsFactory {

    /**
     * 指定した条件で {@link PathOptions} を生成する。
     * <p>
     * このメソッドで生成されるパス定義には、パスパラメータの書式設定(requirements)が存在しないことが前提です。
     * </p>
     *
     * @param httpMethod HTTPメソッド
     * @param path パス
     * @param controller マッピング先のアクションクラス
     * @param action マッピング先のメソッド名（{@link Method#getName()} で得られる値）
     * @return 生成された {@link PathOptions}
     */
    public static PathOptions pathOptions(String httpMethod, String path, Class<?> controller, String action) {
        return pathOptions(httpMethod, path, controller.getName(), action);
    }

    /**
     * 指定した条件で {@link PathOptions} を生成する。
     * <p>
     * このメソッドで生成されるパス定義には、パスパラメータの書式設定(requirements)が存在しないことが前提です。
     * </p>
     *
     * @param httpMethod HTTPメソッド
     * @param path パス
     * @param controller マッピング先のアクションクラス（{@link Class#getName()} で得られる値）
     * @param action マッピング先のメソッド名（{@link Method#getName()} で得られる値）
     * @return 生成された {@link PathOptions}
     */
    public static PathOptions pathOptions(String httpMethod, String path, String controller, String action) {
        Options options = Options.newInstance().$("controller", controller)
                .$("action", action)
                .$("conditions", Options.newInstance().$("method", httpMethod))
                .$("requirements", Options.newInstance().$("controller", controller).$("action", action));
        return new PathOptions(path, options);
    }
    
    private PathOptionsFactory() {}
}
