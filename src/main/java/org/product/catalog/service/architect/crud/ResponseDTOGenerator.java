package org.product.catalog.service.architect.crud;

import java.util.Properties;

import org.product.catalog.service.architect.exceptions.TemplateGenerationException;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.project.Project;

public class ResponseDTOGenerator extends AbstractJavaGenerator {

    public ResponseDTOGenerator(final JavaFileGenerator javaFileGenerator) {
        super(javaFileGenerator);
    }

    @Override
    public String generateContent(final Project project, final String packageName, final String entityName)
            throws TemplateGenerationException {
        try {
            final FileTemplateManager manager = FileTemplateManager.getInstance(project);
            final FileTemplate template = manager.getInternalTemplate("ResponseDTO.java");

            final Properties props = new Properties();
            props.setProperty("PACKAGE_NAME", packageName);
            props.setProperty("NAME", entityName);

            return template.getText(props);
        } catch (Exception ex) {
            throw new TemplateGenerationException(ex.getMessage(), ex);
        }
    }

    @Override
    public String getTargetDirectoryName() {
        return "dto";
    }

    @Override
    public String getFileName(final String entityName) {
        return entityName + "ResponseDTO";
    }
}
