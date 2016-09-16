package nablarch.integration.router;

import java.util.ArrayList;
import java.util.List;

import net.unit8.http.router.ARStringUtil;
import net.unit8.http.router.ControllerDetector;
import net.unit8.http.router.ControllerUtil;

import nablarch.core.repository.SystemRepository;
import nablarch.core.util.ClassTraversal.ClassHandler;
import nablarch.core.util.ResourcesUtil;
import nablarch.core.util.ResourcesUtil.Resources;
import nablarch.core.util.StringUtil;

/**
 * @author kawasima
 */
public class NablarchControllerDetector implements ControllerDetector {
    @Override
    public List<String> detect() {
        final List<String> controllers = new ArrayList<String>();
        RoutesMapping mapping = (RoutesMapping) SystemRepository.getObject("packageMapping");
        final String actionBasePackage = mapping.getBasePackage();

        for (Resources resources: ResourcesUtil.getResourcesTypes(actionBasePackage)) {
            resources.forEach(new ClassHandler() {
                @Override
                public void process(String packageName, String shortClassName) {
                    final String pkgPath = ARStringUtil.removeStart(packageName, actionBasePackage);
                    final String uncapitalizedShortClassName = ControllerUtil.fromClassNameToPath(shortClassName);
                    final String actionPath = StringUtil.chomp(uncapitalizedShortClassName, "Action");

                    StringBuilder sb = new StringBuilder(256);
                    if (StringUtil.hasValue(pkgPath)) {
                        sb.append(pkgPath.substring(1).replace('.', '/')).append('/');
                    }
                    sb.append(actionPath);
                    controllers.add(sb.toString());
                }
            });
        }
        return controllers;
    }
}
