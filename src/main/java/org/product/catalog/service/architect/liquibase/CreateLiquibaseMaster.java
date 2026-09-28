package org.product.catalog.service.architect.liquibase;

import groovy.util.logging.Slf4j;

import java.util.Properties;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;

@Slf4j
public class CreateLiquibaseMaster extends AbstractCreateXmlFile {

    @Override
    public String generateContent(final Project project, final String entityName) {
        try {

            final FileTemplateManager manager = FileTemplateManager.getInstance(project);

            // Ищем наш .ft файл по имени (без расширения)
            final FileTemplate template = manager.getInternalTemplate("LiquibaseMaster.xml");
            final String pluralName = StringUtil.pluralize(entityName);
            final String lowPluralName = pluralName.toLowerCase();

            // Заполняем переменные для Velocity
            final Properties props = new Properties();
            props.setProperty("NAME", entityName);
            props.setProperty("PLURAL_NAME", pluralName);
            props.setProperty("LOW_PLURAL_NAME", lowPluralName);

            // Рендерим текст
            return template.getText(props);
        } catch (Exception ex) {
            Messages.showErrorDialog(project, "Ошибка Velocity: " + ex.getMessage(), "Generator Error");
            return null;
        }
    }

    @Override
    public String getTargetDirectoryName() {
        return "db/changelog";
    }

    @Override
    public String getFileNameWithSuffix(final String entityName) {
        return "db.changelog-master.xml";
    }
}
