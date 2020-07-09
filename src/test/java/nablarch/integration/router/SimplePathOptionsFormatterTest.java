package nablarch.integration.router;

import net.unit8.http.router.Options;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * {@link SimplePathOptionsFormatter} のテストクラス。
 *
 * @author Tanaka Tomoyuki
 */
public class SimplePathOptionsFormatterTest {

    @Test
    public void testFormat() {
        List<PathOptions> pathOptionsList = Arrays.asList(
            pathOptions("/bbb/foo/(:param1)", "GET", "foo.bar.Action", "getMethod"),
            pathOptions("/aaa/fizz", "POST", "fizz.buzz.Action", "postMethod"),
            pathOptions("/ccc/hoge", "PUT", "hoge.fuga.Action", "putMethod")
        );

        SimplePathOptionsFormatter sut = new SimplePathOptionsFormatter();

        String result = sut.format(pathOptionsList);

        String lineSeparator = System.getProperty("line.separator");
        assertThat(result, is(
            "POST /aaa/fizz => fizz.buzz.Action#postMethod" + lineSeparator +
            "GET /bbb/foo/(:param1) => foo.bar.Action#getMethod" + lineSeparator +
            "PUT /ccc/hoge => hoge.fuga.Action#putMethod"
        ));
    }

    private PathOptions pathOptions(String path, String method, String controller, String action) {
        Options options = Options.newInstance().$("controller", controller)
                .$("action", action)
                .$("conditions", Options.newInstance().$("method", method))
                .$("requirements", Options.newInstance().$("controller", controller).$("action", action));
        return new PathOptions(path, options);
    }
}