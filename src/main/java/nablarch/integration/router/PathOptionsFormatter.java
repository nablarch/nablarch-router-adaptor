package nablarch.integration.router;

import java.util.List;

/**
 * ログに出力するために {@link PathOptions} をフォーマットする機能を提供するインターフェース。
 *
 * @author Tanaka Tomoyuki
 */
public interface PathOptionsFormatter {

    /**
     * {@link PathOptions} のリストをログ出力用にフォーマットする。
     * @param pathOptionsList フォーマット対象の {@link PathOptions} のリスト
     * @return フォーマット結果
     */
    String format(List<PathOptions> pathOptionsList);
}
