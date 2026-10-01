package org.product.catalog.service.architect.crud;

import java.util.Properties;

import org.product.catalog.service.architect.exceptions.TemplateGenerationException;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.project.Project;

public class RepositoryGenerator extends AbstractJavaGenerator {

    public RepositoryGenerator(final JavaFileGenerator javaFileGenerator) {
        super(javaFileGenerator);
    }

    @Override
    public String generateContent(final Project project, final String packageName, final String entityName)
            throws TemplateGenerationException {
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
            throw new TemplateGenerationException(ex.getMessage(), ex);
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
