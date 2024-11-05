package nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testInheritAnnotations;

public class ResourceClassExtendsInterfaceImplementsParent extends ParentResourceClassImplementsInterface {
    @Override
    public String simpleGet() {
        return null;
    }

    @Override
    public String getWithPath() {
        return null;
    }
}
