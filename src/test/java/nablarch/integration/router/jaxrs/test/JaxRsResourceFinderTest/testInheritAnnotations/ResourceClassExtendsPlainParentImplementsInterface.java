package nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testInheritAnnotations;

public class ResourceClassExtendsPlainParentImplementsInterface extends ParentPlainClass implements ResourceInterface {
    @Override
    public String simpleGet() {
        return null;
    }

    @Override
    public String getWithPath() {
        return null;
    }
}
