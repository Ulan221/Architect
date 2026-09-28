package org.product.catalog.service.architect.crud;

import java.util.Properties;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;

public class ControllerGenerator extends AbstractJavaGenerator {

    public ControllerGenerator(final JavaFileGenerator javaFileGenerator) {
        super(javaFileGenerator);
    }

    @Override
    public String generateContent(final Project project, final String packageName, final String entityName) {
        try {
            final FileTemplateManager manager = FileTemplateManager.getInstance(project);

            final FileTemplate template = manager.getInternalTemplate("Controller.java");

            final String servicePackage = packageName + ".service";
            final String rootLower = entityName.substring(0, 1).toLowerCase() + entityName.substring(1);
            final String pluralName = StringUtil.pluralize(entityName);
            final String lowPluralName = pluralName.toLowerCase();

            final Properties props = new Properties();
            props.setProperty("PACKAGE_NAME", packageName);
            props.setProperty("SERVICE_PACKAGE", servicePackage);
            props.setProperty("NAME", entityName);
            props.setProperty("ROOT_LOWER", rootLower);
            props.setProperty("PLURAL_NAME", pluralName);
            props.setProperty("LOW_PLURAL_NAME", lowPluralName);

            return template.getText(props);
        } catch (Exception ex) {
            Messages.showErrorDialog(project, "Ошибка Velocity: " + ex.getMessage(), "Generator Error");
            return null;
        }
    }


    @Override
    public String getTargetDirectoryName() {
        return "controller";
    }

    @Override
    public String getFileName(final String entityName) {
        return entityName + "Controller";

    }
}
