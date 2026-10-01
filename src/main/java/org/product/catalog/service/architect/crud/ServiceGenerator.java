package org.product.catalog.service.architect.crud;

import java.util.Properties;

import org.product.catalog.service.architect.exceptions.TemplateGenerationException;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;

public class ServiceGenerator extends AbstractJavaGenerator {

    public ServiceGenerator(final JavaFileGenerator javaFileGenerator) {
        super(javaFileGenerator);
    }

    @Override
    public String generateContent(final Project project, final String packageName, final String entityName)
            throws TemplateGenerationException {
        try {
            final FileTemplateManager manager = FileTemplateManager.getInstance(project);
            final FileTemplate template = manager.getInternalTemplate("Service.java");

            final String repositoryPackage = packageName + ".repository";

            final String pluralName = StringUtil.pluralize(entityName);
            final String rootLower = StringUtil.decapitalize(pluralName);

            final Properties props = new Properties();
            props.setProperty("PACKAGE_NAME", packageName);
            props.setProperty("REPOSITORY_PACKAGE", repositoryPackage);
            props.setProperty("NAME", entityName);
            props.setProperty("PLURAL_NAME", pluralName);
            props.setProperty("ROOT_LOWER", rootLower);

            return template.getText(props);
        } catch (Exception ex) {
            throw new TemplateGenerationException(ex.getMessage(), ex);
        }
    }

    @Override
    public String getTargetDirectoryName() {
        return "service";
    }

    @Override
    public String getFileName(final String entityName) {
        return entityName + "Service";
    }
}
