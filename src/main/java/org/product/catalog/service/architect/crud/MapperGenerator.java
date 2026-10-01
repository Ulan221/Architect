package org.product.catalog.service.architect.crud;

import java.util.Properties;

import org.product.catalog.service.architect.exceptions.TemplateGenerationException;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;

public class MapperGenerator extends AbstractJavaGenerator {
    public MapperGenerator(final JavaFileGenerator javaFileGenerator) {
        super(javaFileGenerator);
    }

    @Override
    public String generateContent(final Project project, final String packageName, final String entityName)
            throws TemplateGenerationException {
        try {
            final FileTemplateManager manager = FileTemplateManager.getInstance(project);
            final FileTemplate template = manager.getInternalTemplate("Mapper.java");

            final String pluralName = StringUtil.pluralize(entityName);

            final String rootLower = StringUtil.decapitalize(pluralName);

            final Properties props = new Properties();
            props.setProperty("PACKAGE_NAME", packageName);
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
        return "mapper";
    }

    @Override
    public String getFileName(final String entityName) {
        return entityName + "Mapper";
    }
}
