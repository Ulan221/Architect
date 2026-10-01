package org.product.catalog.service.architect.crud;

import org.jetbrains.jps.model.java.JavaSourceRootType;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiPackage;

public class JavaSourceDirectoryValidator {


    public boolean validateDirectory(final Project project, final PsiDirectory psiDirectory) {
        final ProjectFileIndex projectFileIndex = ProjectFileIndex.getInstance(project);
        final VirtualFile virtualFile = psiDirectory.getVirtualFile();
        final PsiPackage psiPackage = JavaDirectoryService.getInstance().getPackage(psiDirectory);

        return ApplicationManager.getApplication()
                                 .runReadAction((Computable<Boolean>) () ->
                                                            JavaSourceRootType.SOURCE.equals(
                                                                    projectFileIndex.getContainingSourceRootType(virtualFile))
                                                                    && psiPackage != null
                                                                    && !psiPackage.getQualifiedName().isEmpty()
                                                                    );
    }
}
