package nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testInheritAnnotations;

public class ResourceClassExtendsParent extends ParentResourceClass {
    @Override
    public String simpleGet() {
        return null;
    }

    @Override
    public String getWithPath() {
        return null;
    }
}
