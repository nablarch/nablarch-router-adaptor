package nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testInheritAnnotations;

public class ResourceClassExtendsParentImplementsInterface extends ParentResourceClass implements ResourceInterface {
    @Override
    public String simpleGet() {
        return null;
    }

    @Override
    public String getWithPath() {
        return null;
    }
}
