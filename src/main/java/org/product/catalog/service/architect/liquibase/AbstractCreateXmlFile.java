package org.product.catalog.service.architect.liquibase;

import org.jetbrains.annotations.NotNull;

import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.codeStyle.CodeStyleManager;

public abstract class AbstractCreateXmlFile extends AnAction {

    @Override
    public void actionPerformed(@NotNull final AnActionEvent e) {
        final Project project = e.getProject();
        if (project == null) {
            return;
        }

        final PsiDirectory directory = getTargetDirectory(e);
        if (directory == null) {
            return;
        }

        // Спрашиваем имя файла
        final String fileName = Messages.showInputDialog(project, "Please input file name:", "Architect", Messages.getQuestionIcon());
        if (fileName == null || fileName.isEmpty()) {
            return;
        }

        runGeneration(project, directory, fileName);

    }

    public void runGeneration(final Project project, final PsiDirectory psiDirectory, final String entityName) {
        // 2. Генерируем контент через Velocity
        final String content = generateContent(project, entityName);
        if (content == null) {
            return;
        }

        // 3. Создаем файл
        WriteCommandAction.runWriteCommandAction(project, () -> {
            final PsiFileFactory fileFactory = PsiFileFactory.getInstance(project);
            final String realFileName = getFileNameWithSuffix(entityName);
            final PsiFile newFile = fileFactory.createFileFromText(realFileName, XmlFileType.INSTANCE, content);

            CodeStyleManager.getInstance(project)
                            .reformat(newFile);

            PsiDirectory currentDir = psiDirectory;
            final String[] targetDirs = getTargetDirectoryName().split("/");

            for (String dirName : targetDirs) {
                if (dirName.isBlank()) continue;

                PsiDirectory subDir = currentDir.findSubdirectory(dirName);
                if (subDir == null) {
                    subDir = currentDir.createSubdirectory(dirName);
                }
                currentDir = subDir;
            }

            final PsiElement savedFile = currentDir.add(newFile);

            if (savedFile instanceof PsiFile file) {
                file.navigate(true);
            }
        });
    }

    public abstract String getTargetDirectoryName();

    public abstract String getFileNameWithSuffix(String entityName);

    public abstract String generateContent(Project project, String fileName);

    public static PsiDirectory getTargetDirectory(final AnActionEvent e) {
        final Object data = e.getData(CommonDataKeys.PSI_ELEMENT);

        PsiDirectory currentDir = null;
        if (data instanceof PsiDirectory dir) {
            currentDir = dir;
        } else if (data instanceof PsiJavaFile file) {
            currentDir = file.getContainingDirectory();
        }

        if (currentDir == null) {
            return null;
        }

        PsiDirectory mainDir = currentDir;
        while (mainDir != null && !"main".equals(mainDir.getName())) {
            mainDir = mainDir.getParentDirectory();
        }

        if (mainDir != null) {
            PsiDirectory resourcesDir = mainDir.findSubdirectory("resources");
            if (resourcesDir == null) {
                resourcesDir = mainDir.createSubdirectory("resources");
            }
            return resourcesDir;
        }

        return null;
    }
}
