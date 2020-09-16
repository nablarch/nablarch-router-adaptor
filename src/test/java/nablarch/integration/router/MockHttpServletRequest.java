package nablarch.integration.router;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.HashMap;
import java.util.Map;

/**
 * リクエストスコープに設定される値をテストできるようにするためのテストサポートクラス。
 * <p>
 * {@link HttpServletRequest}や{@link nablarch.fw.web.servlet.NablarchHttpServletRequestWrapper}を
 * jmockitでモック化しただけでは、リクエストスコープへの値の出し入れの処理がモック化されてしまい検証ができない。<br>
 * このクラスはリクエストスコープに保存された値を記録できるように実装しているので、
 * {@link nablarch.fw.web.servlet.ServletExecutionContext}のコンストラクタでこのインスタンスを渡すことで、
 * リクエストスコープに保存された値の検証ができるようになる。
 * </p>
 * @author Tanaka Tomoyuki
 */
public class MockHttpServletRequest extends HttpServletRequestWrapper {
    private Map<String, Object> requestMap = new HashMap<String, Object>();

    public MockHttpServletRequest(HttpServletRequest request) {
        super(request);
    }

    @Override
    public void setAttribute(String name, Object value) {
        requestMap.put(name, value);
    }

    @Override
    public Object getAttribute(String name) {
        return requestMap.get(name);
    }
}