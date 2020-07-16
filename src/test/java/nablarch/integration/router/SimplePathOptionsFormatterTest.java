package nablarch.integration.router;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static nablarch.integration.router.PathOptionsFactory.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * {@link SimplePathOptionsFormatter} のテストクラス。
 *
 * @author Tanaka Tomoyuki
 */
public class SimplePathOptionsFormatterTest {

    @Test
    public void testFormat() {
        List<PathOptions> pathOptionsList = Arrays.asList(
            pathOptions("GET", "/bbb/foo/(:param1)", "foo.bar.Action", "getMethod"),
            pathOptions("POST", "/aaa/fizz", "fizz.buzz.Action", "postMethod"),
            pathOptions("PUT", "/ccc/hoge", "hoge.fuga.Action", "putMethod")
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
}