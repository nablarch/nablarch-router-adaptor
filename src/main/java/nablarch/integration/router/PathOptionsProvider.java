package nablarch.integration.router;

import java.util.List;

/**
 * ルーティング定義を収集する機能を提供するインターフェース。
 *
 * @author Tanaka Tomoyuki
 */
public interface PathOptionsProvider {

    /**
     * ルーティング定義を収集する。
     * @return ルーティング定義のリスト
     */
    List<PathOptions> provide();
}
