package nablarch.integration.router.jaxrs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import nablarch.core.util.ClassTraversal;
import nablarch.core.util.ResourcesUtil;
import nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testFindResourceClasses.ClassAndMethodAreAnnotatedByPath;
import nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testFindResourceClasses.ClassIsAnnotatedByPath;
import nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testFindResourceClasses.foo.ResourceInSubPackage;
import nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testFindResourceMethods.ResourceClass;
import nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testInheritAnnotations.ParentResourceClass;
import nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testInheritAnnotations.ParentResourceClassImplementsInterface;
import nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testInheritAnnotations.ResourceClassExtendsInterfaceImplementsParent;
import nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testInheritAnnotations.ResourceClassExtendsParent;
import nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testInheritAnnotations.ResourceClassExtendsParentImplementsInterface;
import nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testInheritAnnotations.ResourceClassIgnoreAppendMethod;
import nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testInheritAnnotations.ResourceClassImplementsInterface;
import nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testInheritAnnotations.ResourceClassPreferParent;
import nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testInheritAnnotations.ResourceInterface;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * {@link JaxRsResourceFinder} のテスト。
 * 
 * @author Tanaka Tomoyuki
 */
public class JaxRsResourceFinderTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();
    
    private JaxRsResourceFinder sut;

    @Before
    public void before() {
        sut = new JaxRsResourceFinder();
    }

    @Test
    public void testFindResourceClasses() {
        List<JaxRsResource> jaxRsResourceList = sut.find("nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testFindResourceClasses");

        jaxRsResourceList.sort(Comparator.comparing(r -> r.getActionClass().getName()));

        assertThat(jaxRsResourceList.get(0).getActionClass(), equalTo(ClassAndMethodAreAnnotatedByPath.class));
        assertThat(jaxRsResourceList.get(0).getResourceClass(), equalTo(ClassAndMethodAreAnnotatedByPath.class));
        assertThat(jaxRsResourceList.get(1).getActionClass(), equalTo(ClassIsAnnotatedByPath.class));
        assertThat(jaxRsResourceList.get(1).getResourceClass(), equalTo(ClassIsAnnotatedByPath.class));
        assertThat(jaxRsResourceList.get(2).getActionClass(), equalTo(ResourceInSubPackage.class));
        assertThat(jaxRsResourceList.get(2).getResourceClass(), equalTo(ResourceInSubPackage.class));
    }

    @Test
    public void testFindResourceMethods() {
        List<JaxRsResource> jaxRsResourceList = sut.find("nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testFindResourceMethods");

        JaxRsResource jaxRsResource = jaxRsResourceList.get(0);
        assertThat(jaxRsResource.getActionClass(), equalTo(ResourceClass.class));
        assertThat(jaxRsResource.getResourceClass(), equalTo(ResourceClass.class));

        assertThat(jaxRsResource.getResourceMethodList(), containsInAnyOrder(
            hasProperty("name", is("get")),
            hasProperty("name", is("post")),
            hasProperty("name", is("myHttpMethod"))
        ));
    }
    
    @Test
    public void testThrowsExceptionIfClassNotFound() {
        ResourcesUtil.addResourcesFactory("file", new ResourcesUtil.ResourcesFactory() {
            @Override
            public ResourcesUtil.Resources create(URL url, String rootPackage, String rootDir) {
                return new ResourcesUtil.Resources() {
                    @Override
                    public void forEach(ClassTraversal.ClassHandler handler) {
                        handler.process("not_exists_package", "NotExistsClass");
                    }

                    @Override public void close() {}
                };
            }
        });
        
        try {
            exception.expect(RuntimeException.class);
            exception.expectCause(Matchers.<Throwable>instanceOf(ClassNotFoundException.class));

            sut.find("nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testThrowsExceptionIfClassNotFound");

        } finally {
            ResourcesUtil.addResourcesFactory("file", new ResourcesUtil.ResourcesFactory() {
                @Override
                public ResourcesUtil.Resources create(final URL url, final String rootPackage, final String rootDir) {
                    return new ResourcesUtil.FileSystemResources(ResourcesUtil.getBaseDir(url, rootDir), rootPackage, rootDir);
                }
            });
        }
    }
    
    @Test
    public void testThrowsExceptionIfHttpMethodAnnotationIsDuplicate() {
        exception.expect(RuntimeException.class);
        exception.expectMessage("'get' method has multiple HTTP method annotations.");
        
        sut.find("nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testThrowsExceptionIfHttpMethodAnnotationIsDuplicate");
    }

    @Test
    public void testResourceClassInherit() {
        List<JaxRsResource> jaxRsResourceList = sut.find("nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testInheritAnnotations");

        assertThat(jaxRsResourceList, hasSize(6));

        Map<Class<?>, JaxRsResource> jaxRsResources = jaxRsResourceList
                .stream()
                .collect(Collectors.toMap(JaxRsResource::getActionClass, Function.identity()));

        // インターフェースがリソースクラスとなる
        assertThat(jaxRsResources.get(ResourceClassImplementsInterface.class).getActionClass(), equalTo(ResourceClassImplementsInterface.class));
        assertThat(jaxRsResources.get(ResourceClassImplementsInterface.class).getResourceClass(), equalTo(ResourceInterface.class));

        // 親クラスがリソースクラスとなる
        assertThat(jaxRsResources.get(ResourceClassExtendsParent.class).getActionClass(), equalTo(ResourceClassExtendsParent.class));
        assertThat(jaxRsResources.get(ResourceClassExtendsParent.class).getResourceClass(), equalTo(ParentResourceClass.class));

        // 親クラスを継承してインターフェースも実装している場合は、親クラスがリソースクラスとなる
        assertThat(jaxRsResources.get(ResourceClassExtendsParentImplementsInterface.class).getActionClass(), equalTo(ResourceClassExtendsParentImplementsInterface.class));
        assertThat(jaxRsResources.get(ResourceClassExtendsParentImplementsInterface.class).getResourceClass(), equalTo(ParentResourceClass.class));

        // 親クラスがインターフェースを実装している場合は、親クラスがリソースクラスとなる
        assertThat(jaxRsResources.get(ResourceClassExtendsInterfaceImplementsParent.class).getActionClass(), equalTo(ResourceClassExtendsInterfaceImplementsParent.class));
        assertThat(jaxRsResources.get(ResourceClassExtendsInterfaceImplementsParent.class).getResourceClass(), equalTo(ParentResourceClassImplementsInterface.class));

        // ActionクラスにJAX-RSアノテーションが付与されている場合は、Actionクラスがリソースクラスとなる
        assertThat(jaxRsResources.get(ResourceClassPreferParent.class).getActionClass(), equalTo(ResourceClassPreferParent.class));
        assertThat(jaxRsResources.get(ResourceClassPreferParent.class).getResourceClass(), equalTo(ResourceClassPreferParent.class));

        // Actionクラスにリソースメソッドが定義されていても、クラスに@Pathアノテーションが付与されているクラスが優先される
        assertThat(jaxRsResources.get(ResourceClassIgnoreAppendMethod.class).getActionClass(), equalTo(ResourceClassIgnoreAppendMethod.class));
        assertThat(jaxRsResources.get(ResourceClassIgnoreAppendMethod.class).getResourceClass(), equalTo(ResourceInterface.class));
    }

    @Test
    public void testResourceMethodInherit() {
        List<JaxRsResource> jaxRsResourceList = sut.find("nablarch.integration.router.jaxrs.test.JaxRsResourceFinderTest.testInheritAnnotations");

        assertThat(jaxRsResourceList, hasSize(6));

        Map<Class<?>, JaxRsResource> jaxRsResources = jaxRsResourceList
                .stream()
                .collect(Collectors.toMap(JaxRsResource::getActionClass, Function.identity()));

        // 実装しているインターフェースのメソッドがリソースメソッドとなる
        JaxRsResource resourceClassImplementsInterface = jaxRsResources.get(ResourceClassImplementsInterface.class);
        assertThat(resourceClassImplementsInterface.getResourceClass().getAnnotation(Path.class).value(), is("/interface-path"));

        List<Method> resourceClassImplementsInterfaceMethods =
                resourceClassImplementsInterface
                        .getResourceMethodList()
                        .stream()
                        .sorted(Comparator.comparing(Method::getName))
                        .toList();
        assertThat(resourceClassImplementsInterfaceMethods, hasSize(2));
        assertThat(resourceClassImplementsInterfaceMethods.get(0).getName(), is("getWithPath"));
        assertThat(resourceClassImplementsInterfaceMethods.get(0).getAnnotation(GET.class), notNullValue());
        assertThat(resourceClassImplementsInterfaceMethods.get(0).getAnnotation(Path.class).value(), is("/get-by-interface"));
        assertThat(resourceClassImplementsInterfaceMethods.get(1).getName(), is("simpleGet"));
        assertThat(resourceClassImplementsInterfaceMethods.get(1).getAnnotation(GET.class), notNullValue());

        // 継承しているクラスのメソッドがリソースメソッドとなる
        JaxRsResource resourceClassExtendsParent = jaxRsResources.get(ResourceClassExtendsParent.class);
        assertThat(resourceClassExtendsParent.getResourceClass().getAnnotation(Path.class).value(), is("/parent-path"));

        List<Method> resourceClassExtendsParentMethods =
                resourceClassExtendsParent
                        .getResourceMethodList()
                        .stream()
                        .sorted(Comparator.comparing(Method::getName))
                        .toList();
        assertThat(resourceClassExtendsParentMethods, hasSize(2));
        assertThat(resourceClassExtendsParentMethods.get(0).getName(), is("getWithPath"));
        assertThat(resourceClassExtendsParentMethods.get(0).getAnnotation(GET.class), notNullValue());
        assertThat(resourceClassExtendsParentMethods.get(0).getAnnotation(Path.class).value(), is("/get-by-parent"));
        assertThat(resourceClassExtendsParentMethods.get(1).getName(), is("simpleGet"));
        assertThat(resourceClassExtendsParentMethods.get(1).getAnnotation(GET.class), notNullValue());

        // 親クラスを継承してインターフェースも実装している場合は、親クラスのメソッドがリソースメソッドとなる
        JaxRsResource resourceClassExtendsParentImplementsInterface = jaxRsResources.get(ResourceClassExtendsParentImplementsInterface.class);
        assertThat(resourceClassExtendsParentImplementsInterface.getResourceClass().getAnnotation(Path.class).value(), is("/parent-path"));

        List<Method> resourceClassExtendsParentImplementsInterfaceMethods =
                resourceClassExtendsParentImplementsInterface
                        .getResourceMethodList()
                        .stream()
                        .sorted(Comparator.comparing(Method::getName))
                        .toList();
        assertThat(resourceClassExtendsParentImplementsInterfaceMethods, hasSize(2));
        assertThat(resourceClassExtendsParentImplementsInterfaceMethods.get(0).getName(), is("getWithPath"));
        assertThat(resourceClassExtendsParentImplementsInterfaceMethods.get(0).getAnnotation(GET.class), notNullValue());
        assertThat(resourceClassExtendsParentImplementsInterfaceMethods.get(0).getAnnotation(Path.class).value(), is("/get-by-parent"));
        assertThat(resourceClassExtendsParentImplementsInterfaceMethods.get(1).getName(), is("simpleGet"));
        assertThat(resourceClassExtendsParentImplementsInterfaceMethods.get(1).getAnnotation(GET.class), notNullValue());

        // 親クラスがインターフェースを実装している場合は、親クラスのメソッドがリソースメソッドとなる
        JaxRsResource resourceClassExtendsInterfaceImplementsParent = jaxRsResources.get(ResourceClassExtendsInterfaceImplementsParent.class);
        assertThat(resourceClassExtendsInterfaceImplementsParent.getResourceClass().getAnnotation(Path.class).value(), is("/parent-with-interface-path"));

        List<Method> resourceClassExtendsInterfaceImplementsParentMethods =
                resourceClassExtendsInterfaceImplementsParent
                        .getResourceMethodList()
                        .stream()
                        .sorted(Comparator.comparing(Method::getName))
                        .toList();
        assertThat(resourceClassExtendsInterfaceImplementsParentMethods, hasSize(2));
        assertThat(resourceClassExtendsInterfaceImplementsParentMethods.get(0).getName(), is("getWithPath"));
        assertThat(resourceClassExtendsInterfaceImplementsParentMethods.get(0).getAnnotation(GET.class), notNullValue());
        assertThat(resourceClassExtendsInterfaceImplementsParentMethods.get(0).getAnnotation(Path.class).value(), is("/get-by-parent"));
        assertThat(resourceClassExtendsInterfaceImplementsParentMethods.get(1).getName(), is("simpleGet"));
        assertThat(resourceClassExtendsInterfaceImplementsParentMethods.get(1).getAnnotation(GET.class), notNullValue());

        // ActionクラスにJAX-RSアノテーションが付与されている場合は、Actionクラスのメソッドがリソースメソッドとなる
        JaxRsResource resourceClassPreferParent = jaxRsResources.get(ResourceClassPreferParent.class);
        assertThat(resourceClassPreferParent.getResourceClass().getAnnotation(Path.class).value(), is("/resource-path"));

        List<Method> resourceClassPreferParentMethods =
                resourceClassPreferParent
                        .getResourceMethodList()
                        .stream()
                        .sorted(Comparator.comparing(Method::getName))
                        .toList();
        assertThat(resourceClassPreferParentMethods, hasSize(2));
        assertThat(resourceClassPreferParentMethods.get(0).getName(), is("getWithPath"));
        assertThat(resourceClassPreferParentMethods.get(0).getAnnotation(GET.class), nullValue());
        assertThat(resourceClassPreferParentMethods.get(0).getAnnotation(POST.class), notNullValue());
        assertThat(resourceClassPreferParentMethods.get(0).getAnnotation(Path.class).value(), is("/get-by-resource"));
        assertThat(resourceClassPreferParentMethods.get(1).getName(), is("simpleGet"));
        assertThat(resourceClassPreferParentMethods.get(1).getAnnotation(GET.class), nullValue());
        assertThat(resourceClassPreferParentMethods.get(1).getAnnotation(POST.class), notNullValue());
    }
}