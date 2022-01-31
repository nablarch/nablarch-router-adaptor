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
            pathOptions("PUT", "/aaa/fizz", "fizz.buzz.Action", "putMethod"),
            pathOptions("POST", "/aaa/fizz", "fizz.buzz.Action", "postMethod"),
            pathOptions("DELETE", "/aaa/fizz", "fizz.buzz.Action", "deleteMethod"),
            pathOptions("PUT", "/ccc/hoge", "hoge.fuga.Action", "putMethod"),
            pathOptions("PATCH", "/ccc/hoge", "hoge.fuga.Action", "patchMethod")
        );

        SimplePathOptionsFormatter sut = new SimplePathOptionsFormatter();

        String result = sut.format(pathOptionsList);

        String lineSeparator = System.getProperty("line.separator");
        assertThat(result, is(
            "DELETE /aaa/fizz => fizz.buzz.Action#deleteMethod" + lineSeparator +
            "POST /aaa/fizz => fizz.buzz.Action#postMethod" + lineSeparator +
            "PUT /aaa/fizz => fizz.buzz.Action#putMethod" + lineSeparator +
            "GET /bbb/foo/(:param1) => foo.bar.Action#getMethod" + lineSeparator +
            "PATCH /ccc/hoge => hoge.fuga.Action#patchMethod" + lineSeparator +
            "PUT /ccc/hoge => hoge.fuga.Action#putMethod"
        ));
    }
}