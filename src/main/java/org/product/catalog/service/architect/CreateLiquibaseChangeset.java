package org.product.catalog.service.architect;


import groovy.util.logging.Slf4j;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;


public class CreateLiquibaseChangeset extends AbstractCreateXmlFile {

    @Override
    public String getTargetDirectoryName() {
        return "db/changelog/changesets";
    }

    @Override
    public String getFileNameWithSuffix(final String entityName) {
        return "db.changelog-create-" + entityName.toLowerCase() + "-table.xml";
    }

    @Override
    public String generateContent(final Project project, final String entityName) {
        try {

            final FileTemplateManager manager = FileTemplateManager.getInstance(project);

            final FileTemplate template = manager.getInternalTemplate("LiquibaseChangeset.xml");
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
}
