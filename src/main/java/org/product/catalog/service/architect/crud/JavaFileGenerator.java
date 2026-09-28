package org.product.catalog.service.architect.crud;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.codeStyle.CodeStyleManager;

public class JavaFileGenerator {

    public void runGeneration(final Project project,
                              final PsiDirectory psiDirectory,
                              final String fileName,
                              final String content,
                              final String targetDirectory) {

        WriteCommandAction.runWriteCommandAction(project, () -> {
            final PsiFileFactory fileFactory = PsiFileFactory.getInstance(project);
            final String newFileName = fileName + ".java";
            final PsiFile newFile = fileFactory.createFileFromText(newFileName, JavaFileType.INSTANCE, content);
            CodeStyleManager.getInstance(project)
                            .reformat(newFile);

            PsiDirectory newDir = psiDirectory.findSubdirectory(targetDirectory);
            if (newDir == null) {
                newDir = psiDirectory.createSubdirectory(targetDirectory);
            }

            final PsiElement savedFile = newDir.add(newFile);

            if (savedFile instanceof PsiFile) {
                ((PsiFile) savedFile).navigate(true);
            }
        });
    }
}
