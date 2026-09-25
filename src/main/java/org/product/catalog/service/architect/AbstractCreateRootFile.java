package org.product.catalog.service.architect;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.codeStyle.CodeStyleManager;

public abstract class AbstractCreateRootFile extends AnAction {

    @Override
    public void actionPerformed(@NotNull final AnActionEvent e) {
        final Project project = e.getProject();
        if (project == null) return;

        final PsiDirectory rootDir = getRootDirectory(project);
        if (rootDir == null) return;

        runGeneration(project, rootDir);
    }

    public void runGeneration(final Project project, final PsiDirectory rootDirectory) {
        final String content = generateContent(project);
        if (content == null) return;

        final String realFileName = getFileName();

        WriteCommandAction.runWriteCommandAction(project, () -> {
            // Если файл с таким именем уже существует в корне (например Dockerfile) - не перезаписываем
            if (rootDirectory.findFile(realFileName) != null) {
                return;
            }

            final PsiFileFactory fileFactory = PsiFileFactory.getInstance(project);
            final PsiFile newFile = fileFactory.createFileFromText(realFileName, PlainTextFileType.INSTANCE, content);

            CodeStyleManager.getInstance(project).reformat(newFile);

            final PsiElement savedFile = rootDirectory.add(newFile);

            if (savedFile instanceof PsiFile) {
                ((PsiFile) savedFile).navigate(true);
            }
        });
    }

    public abstract String generateContent(Project project);

    public abstract String getFileName();

    public static PsiDirectory getRootDirectory(final Project project) {
        if (project == null || project.getBaseDir() == null) {
            return null;
        }
        return PsiManager.getInstance(project).findDirectory(project.getBaseDir());
    }
}