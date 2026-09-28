package org.product.catalog.service.architect.crud;

import java.util.Properties;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

public class RepositoryGenerator extends AbstractJavaGenerator {

    public RepositoryGenerator(final JavaFileGenerator javaFileGenerator) {
        super(javaFileGenerator);
    }

    @Override
    public String generateContent(final Project project, final String packageName, final String entityName) {
        try {
            final FileTemplateManager manager = FileTemplateManager.getInstance(project);
            final FileTemplate template = manager.getInternalTemplate("Repository.java");

            String entityPackage = packageName.replace(".repository", ".entity");
            if (entityPackage.equals(packageName)) {
                entityPackage = packageName + ".entity";
            }

            final Properties props = new Properties();
            props.setProperty("PACKAGE_NAME", packageName);
            props.setProperty("ENTITY_PACKAGE", entityPackage);
            props.setProperty("NAME", entityName);

            return template.getText(props);
        } catch (Exception ex) {
            Messages.showErrorDialog(project, "Ошибка Velocity: " + ex.getMessage(), "Generator Error");
            return null;
        }
    }

    @Override
    public String getTargetDirectoryName() {
        return "repository";
    }

    @Override
    public String getFileName(final String entityName) {
        return entityName + "Repository";
    }
}
