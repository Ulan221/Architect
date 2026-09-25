package org.product.catalog.service.architect;

import java.util.Properties;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;

public class CreateService extends AbstractCreateJavaFile {
    @Override
    public String getFileNameWithSuffix(final String entityName) {
        return entityName + "Service";
    }

    @Override
    public String generateContent(final Project project, final String packageName, final String entityName) {
        try {
            final FileTemplateManager manager = FileTemplateManager.getInstance(project);
            final FileTemplate template = manager.getInternalTemplate("Service.java");

            final String repositoryPackage = packageName + ".repository";

            final String pluralName = StringUtil.pluralize(entityName);
            final String rootLower = entityName.substring(0, 1).toLowerCase() + entityName.substring(1);

            final Properties props = new Properties();
            props.setProperty("PACKAGE_NAME", packageName);
            props.setProperty("REPOSITORY_PACKAGE", repositoryPackage);
            props.setProperty("NAME", entityName);
            props.setProperty("PLURAL_NAME", pluralName);
            props.setProperty("ROOT_LOWER", rootLower);

            return template.getText(props);
        } catch (Exception ex) {
            Messages.showErrorDialog(project, "Ошибка Velocity: " + ex.getMessage(), "Generator Error");
            return null;
        }
    }

    @Override
    public String getTargetDirectoryName() {
        return "service";
    }
}
