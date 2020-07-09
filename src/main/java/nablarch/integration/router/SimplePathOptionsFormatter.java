package nablarch.integration.router;

import nablarch.core.util.StringUtil;
import net.unit8.http.router.Options;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * {@link PathOptions} を単純な形式でフォーマットする機能を提供するクラス。
 * <p>
 * このフォーマッタは、まず {@link PathOptions} のリストを {@code path} の昇順でソートする。<br>
 * そして、それぞれの {@link PathOptions} を {@code "<options.conditions.method> <path> => <options.controller>#<options.action>"}
 * という書式でフォーマットし、改行コード({@code System.getProperty("line.separator")})で連結したものをフォーマット結果として返す。<br>
 * </p>
 *
 * @author Tanaka Tomoyuki
 */
public class SimplePathOptionsFormatter implements PathOptionsFormatter {

    @Override
    public String format(List<PathOptions> pathOptionsList) {
        SortedMap<String, PathOptions> sortedByPath = new TreeMap<String, PathOptions>();

        for (PathOptions pathOptions : pathOptionsList) {
            sortedByPath.put(pathOptions.getPath(), pathOptions);
        }

        List<String> logStringList = new ArrayList<String>(pathOptionsList.size());
        for (PathOptions pathOptions : sortedByPath.values()) {
            logStringList.add(format(pathOptions));
        }

        return StringUtil.join(System.getProperty("line.separator"), logStringList);
    }

    private String format(PathOptions pathOptions) {
        Options options = pathOptions.getOptions();
        Options conditions = (Options) options.get("conditions");
        return String.format("%s %s => %s#%s", conditions.get("method"), pathOptions.getPath(), options.get("controller"), options.get("action"));
    }
}
