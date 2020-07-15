package nablarch.integration.router.jaxrs;

import net.unit8.http.router.Options;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.regex.Pattern;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * {@link JaxRsPathParser} のテスト。
 * @author Tanaka Tomoyuki
 */
public class JaxRsPathParserTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();
    
    private JaxRsPathParser sut = new JaxRsPathParser();
    
    @Test
    public void testReplacePathParameterFormat() {
        PathRequirements pathRequirements = sut.parse("/test-resource/{param1}/get/{ param2 }/fizz");
        assertThat(pathRequirements.getPath(), is("/test-resource/(:param1)/get/(:param2)/fizz"));
    }

    @Test
    public void testParseRegularExpressionInPathParameter() {
        PathRequirements pathRequirements = sut.parse("/test-resource/{param1:\\d+}/get/{ param2 : [a-zA-Z{} ]+ }/fizz");
        
        assertThat(pathRequirements.getPath(), is("/test-resource/(:param1)/get/(:param2)/fizz"));

        Options requirements = pathRequirements.getRequirements();
        assertThat(requirements.get("param1"), allOf(hasToString("\\d+"), instanceOf(Pattern.class)));
        assertThat(requirements.get("param2"), allOf(hasToString("[a-zA-Z{} ]+"), instanceOf(Pattern.class)));
    }

    @Test
    public void testThrowsExceptionIfParameterSegmentHasNoEndBrackets() {
        exception.expect(IllegalStateException.class);
        exception.expectMessage("Parameter segment has no end brackets. (parameterName='param1/bar')");

        sut.parse("/foo/{param1/bar");
    }
    
    @Test
    public void testThrowsExceptionIfParameterSegmentWithRegexpHasNoEndBrackets() {
        exception.expect(IllegalStateException.class);
        exception.expectMessage("Parameter segment has no end brackets. (regexp='[a-z]+ /bar')");

        sut.parse("/foo/{param1: [a-z]+ /bar");
    }
}