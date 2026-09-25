package org.product.catalog.service.architect;

import java.util.Properties;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

public class CreateRequestDTO extends AbstractCreateJavaFile {
    @Override
    public String generateContent(final Project project, final String packageName, final String entityName) {
        try {
            // Берем менеджер шаблонов
            final FileTemplateManager manager = FileTemplateManager.getInstance(project);

            // Ищем наш .ft файл по имени (без расширения)
            final FileTemplate template = manager.getInternalTemplate("RequestDTO.java");

            // Заполняем переменные для Velocity
            final Properties props = new Properties();
            props.setProperty("PACKAGE_NAME", packageName);
            props.setProperty("NAME", entityName);

            // Рендерим текст
            return template.getText(props);
        } catch (Exception ex) {
            Messages.showErrorDialog(project, "Ошибка Velocity: " + ex.getMessage(), "Generator Error");
            return null;
        }
    }

    @Override
    public String getTargetDirectoryName() {
        return "dto";
    }

    @Override
    public String getFileNameWithSuffix(final String entityName) {
        return entityName + "RequestDTO";
    }
}
