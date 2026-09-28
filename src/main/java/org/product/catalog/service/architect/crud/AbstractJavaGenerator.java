package org.product.catalog.service.architect.crud;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiPackage;

public abstract class AbstractJavaGenerator {

    private final JavaFileGenerator javaFileGenerator;

    public AbstractJavaGenerator(final JavaFileGenerator javaFileGenerator) {
        this.javaFileGenerator = javaFileGenerator;
    }

    public void generate(final Project project, final PsiDirectory psiDirectory, final String entityName) {

        final PsiPackage pkg = JavaDirectoryService.getInstance()
                                                   .getPackage(psiDirectory);
        final String packageName = (pkg != null) ? pkg.getQualifiedName() : "";

        final String fileName = getFileName(entityName);

        javaFileGenerator.runGeneration(project, psiDirectory, fileName, generateContent(project, packageName, entityName),
                                        getTargetDirectoryName());
    }

    public abstract String generateContent(Project project, String packageName, String entityName);

    public abstract String getTargetDirectoryName();

    public abstract String getFileName(String entityName);
}
