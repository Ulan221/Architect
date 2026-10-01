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
            final FileTemplate template = manager.getInternalTemplate("LiquibaseMaster.xml");

            return template.getText();
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
