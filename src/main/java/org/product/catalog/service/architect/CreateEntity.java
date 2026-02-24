package org.product.catalog.service.architect;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;

import org.jetbrains.annotations.NotNull;

import java.util.Properties;

public class CreateEntity extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }

        PsiDirectory directory = getTargetDirectory(e);
        if (directory == null) {return;}

        // 2. Определяем имя пакета
        PsiPackage pkg = JavaDirectoryService.getInstance()
                                             .getPackage(directory);
        String packageName = (pkg != null) ? pkg.getQualifiedName() : "";

        // 3. Спрашиваем имя Entity
        String entityName = Messages.showInputDialog(project, "Please input Entity name:", "Architect", Messages.getQuestionIcon());
        if (entityName == null || entityName.isEmpty()) {
            return;
        }

        // 4. Генерируем контент через Velocity
        String content = generateContent(project, packageName, entityName);
        if (content == null) {
            return;
        }

        // 5. Создаем файл
        WriteCommandAction.runWriteCommandAction(project, () -> {
            PsiFileFactory factory = PsiFileFactory.getInstance(project);
            PsiFile newFile = factory.createFileFromText(entityName + ".java", JavaFileType.INSTANCE, content);
            CodeStyleManager.getInstance(project).reformat(newFile);
            directory.add(newFile);
            newFile.navigate(true); // Открываем созданный файл
        });
    }

    private String generateContent(Project project, String packageName, String entityName) {
        try {
            // Берем менеджер шаблонов
            FileTemplateManager manager = FileTemplateManager.getInstance(project);

            // Ищем наш .ft файл по имени (без расширения)
            FileTemplate template = manager.getInternalTemplate("MyEntity.java" );

            // Заполняем переменные для Velocity
            Properties props = new Properties();
            props.setProperty("PACKAGE_NAME", packageName);
            props.setProperty("NAME", entityName);
            props.setProperty("TABLE_NAME", entityName.toLowerCase() + "s" );

            // Рендерим текст
            return template.getText(props);
        } catch (Exception ex) {
            Messages.showErrorDialog(project, "Ошибка Velocity: " + ex.getMessage(), "Generator Error" );
            return null;
        }
    }

    private PsiDirectory getTargetDirectory(AnActionEvent e) {
        Object data = e.getData(CommonDataKeys.PSI_ELEMENT);
        if (data instanceof PsiDirectory dir) {
            return dir;
        }
        if (data instanceof PsiJavaFile file) {
            return file.getContainingDirectory();
        }
        return null;
    }
}
