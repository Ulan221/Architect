package org.product.catalog.service.architect;

import org.jetbrains.annotations.NotNull;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.codeStyle.CodeStyleManager;

public abstract class AbstractCreateJavaFile extends AnAction {
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


        final String fileName = Messages.showInputDialog(project, "Please input entity name:", "Architect", Messages.getQuestionIcon());
        if (fileName == null || fileName.isEmpty()) {
            return;
        }

        runGeneration(project, directory, fileName);

    }

    // Создание файлов
    public void runGeneration(final Project project, final PsiDirectory psiDirectory, final String entityName) {

        // 1. Определяем имя пакета
        final PsiPackage pkg = JavaDirectoryService.getInstance()
                                                   .getPackage(psiDirectory);
        final String packageName = (pkg != null) ? pkg.getQualifiedName() : "";

        // 2. Генерируем контент через Velocity
        final String content = generateContent(project, packageName, entityName);
        if (content == null) {
            return;
        }

        // 3. Создаем файл
        WriteCommandAction.runWriteCommandAction(project, () -> {
            final PsiFileFactory fileFactory = PsiFileFactory.getInstance(project);
            final String realFileName = getFileNameWithSuffix(entityName) + ".java";
            final PsiFile newFile = fileFactory.createFileFromText(realFileName, JavaFileType.INSTANCE, content);
            CodeStyleManager.getInstance(project)
                            .reformat(newFile);

            PsiDirectory newDir = psiDirectory.findSubdirectory(getTargetDirectoryName());
            if (newDir == null) {
                newDir = psiDirectory.createSubdirectory(getTargetDirectoryName());
            }

            final PsiElement savedFile = newDir.add(newFile);

            // Открываем в редакторе именно реальный сохраненный файл
            if (savedFile instanceof PsiFile) {
                ((PsiFile) savedFile).navigate(true);
            }
        });
    }

    public abstract String generateContent(Project project, String packageName, String fileName);

    public abstract String getTargetDirectoryName();

    public abstract String getFileNameWithSuffix(String entityName);

    public static PsiDirectory getTargetDirectory(final AnActionEvent e) {
        final Object data = e.getData(CommonDataKeys.PSI_ELEMENT);
        if (data instanceof PsiDirectory dir) {
            return dir;
        }
        if (data instanceof PsiJavaFile file) {
            return file.getContainingDirectory();
        }
        return null;
    }
}
