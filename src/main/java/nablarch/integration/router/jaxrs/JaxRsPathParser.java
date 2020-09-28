package nablarch.integration.router.jaxrs;

import net.unit8.http.router.Options;

import java.util.regex.Pattern;

/**
 * {@link javax.ws.rs.Path} に設定されたパス文字列を解析して、 {@link PathRequirements} に変換するクラス。
 * 
 * @author Tanaka Tomoyuki
 */
public class JaxRsPathParser {

    /**
     * JAX-RS のパス文字列を解析して、 {@link PathRequirements} を返す。
     * @param jaxRsPath JAX-RS のパス文字列
     * @return 変換後の {@link PathRequirements}
     */
    public PathRequirements parse(String jaxRsPath) {
        StringBuilder routerPath = new StringBuilder();
        Options requirements = Options.newInstance();
        
        CodePointIterator iterator = new CodePointIterator(jaxRsPath);

        while (iterator.hasNext()) {
            int codePoint = iterator.nextInt();

            if (codePoint == '{') {
                JaxRsPathParameter jaxRsPathParameter = parseJaxRsPathParameter(iterator);

                routerPath.append("(:").append(jaxRsPathParameter.name).append(')');
                if (jaxRsPathParameter.hasRegex()) {
                    requirements.put(jaxRsPathParameter.name, Pattern.compile(jaxRsPathParameter.regex));
                }
            } else {
                routerPath.appendCodePoint(codePoint);
            }
        }

        return new PathRequirements(routerPath.toString(), requirements);
    }

    private JaxRsPathParameter parseJaxRsPathParameter(CodePointIterator iterator) {
        StringBuilder parameterName = new StringBuilder();
        while (iterator.hasNext()) {
            int codePoint = iterator.nextInt();
            if (codePoint == ' ') {
                continue;

            } else if (codePoint == '}') {
                return new JaxRsPathParameter(parameterName.toString());

            } else if (codePoint == ':') {
                String regex = parseJaxRsPathParameterRegex(iterator);
                return new JaxRsPathParameter(parameterName.toString(), regex);
            }
            parameterName.appendCodePoint(codePoint);
        }
        throw new IllegalStateException("Parameter segment has no end brackets. (parameterName='" + parameterName + "')");
    }

    private String parseJaxRsPathParameterRegex(CodePointIterator iterator) {
        StringBuilder regex = new StringBuilder();
        StringBuilder spaceBuf = new StringBuilder();
        int braceOpen = 0;
        boolean parameterSegmentClosed = false;
        boolean inRegexValue = false;
        while (iterator.hasNext()) {
            int codePoint = iterator.nextInt();
            if (codePoint == ' ') {
                if (inRegexValue) {
                    spaceBuf.appendCodePoint(codePoint);
                }
                continue;
            } else {
                inRegexValue = true;
            }
            if (codePoint == '{') {
                braceOpen++;

            } else if (codePoint == '}') {
                if (braceOpen == 0) {
                    parameterSegmentClosed = true;
                    break;
                }
                braceOpen--;
            }
            regex.append(spaceBuf.toString());
            regex.appendCodePoint(codePoint);
            spaceBuf.setLength(0);
        }
        if (!parameterSegmentClosed) {
            throw new IllegalStateException("Parameter segment has no end brackets. (regexp='" + regex + "')");
        }
        return regex.toString();
    }
    
    private static class CodePointIterator {
        private final String text;
        private final int codePointCount;
        private int index;

        private CodePointIterator(String text) {
            this.text = text;
            this.codePointCount = text.codePointCount(0, text.length());
        }

        private boolean hasNext() {
            return index < codePointCount;
        }

        private int nextInt() {
            int codePoint = text.codePointAt(index);
            index++;
            return codePoint;
        }
    }
    
    private static class JaxRsPathParameter {
        private final String name;
        private final String regex;

        private JaxRsPathParameter(String name) {
            this(name, null);
        }

        private JaxRsPathParameter(String name, String regex) {
            this.name = name;
            this.regex = regex;
        }

        private boolean hasRegex() {
            return regex != null;
        }
    }
}
