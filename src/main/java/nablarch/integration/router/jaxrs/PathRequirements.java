package nablarch.integration.router.jaxrs;

import net.unit8.http.router.Options;

/**
 * パス定義と、パスパラメータの書式定義(requirements)のセット。
 * 
 * @author Tanaka Tomoyuki
 */
public class PathRequirements {
    private final String path;
    private final Options requirements;

    /**
     * コンストラクタ。
     * @param path パス定義
     * @param requirements パスパラメータの書式定義
     */
    public PathRequirements(String path, Options requirements) {
        this.path = path;
        this.requirements = requirements;
    }

    /**
     * パス定義を取得する。
     * @return パス定義
     */
    public String getPath() {
        return path;
    }

    /**
     * パスパラメータの書式定義を取得する
     * @return パスパラメータの書式定義
     */
    public Options getRequirements() {
        return requirements;
    }
}
