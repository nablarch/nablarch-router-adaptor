package nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testInheritAnnotations;

public class ResourceClassExtendsGrandParentImplementsPlainInterface extends ParentPlainClassExtendsParentResourceClass implements PlainInterface {
    @Override
    public String simpleGet() {
        return null;
    }

    @Override
    public String getWithPath() {
        return null;
    }
}
