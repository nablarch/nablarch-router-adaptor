package nablarch.integration.router;

import net.unit8.http.router.Options;

/**
 * パスと、それに割り当てられている {@link Options} のセット。
 *
 * @author Tanaka Tomoyuki
 */
public class PathOptions {
    private final String path;
    private final Options options;

    /**
     * コンストラクタ。
     * @param path パス
     * @param options パスに割り当てられた設定
     */
    public PathOptions(String path, Options options) {
        this.path = path;
        this.options = options;
    }

    /**
     * パスを取得する。
     * @return パス
     */
    public String getPath() {
        return path;
    }

    /**
     * 設定を取得する
     * @return 設定
     */
    public Options getOptions() {
        return options;
    }
}
