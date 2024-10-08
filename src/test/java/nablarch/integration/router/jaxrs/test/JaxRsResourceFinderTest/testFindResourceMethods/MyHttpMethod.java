package nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testFindResourceMethods;

import jakarta.ws.rs.HttpMethod;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@HttpMethod("TEST")
public @interface MyHttpMethod {
}
