package nablarch.integration.router;

import java.util.List;

/**
 * 指定されたパッケージ配下のクラスを探索し、ルーティング定義を収集する機能を提供するインターフェース。
 *
 * @author Tanaka Tomoyuki
 */
public interface ClassTraversalOptionsCollector {

    /**
     * パッケージ配下のクラスを探索し、ルーティング定義を収集する。
     * @param basePackage 探索対象のパッケージ
     * @return ルーティング定義のリスト
     */
    List<PathOptions> collect(String basePackage);
}
