package org.product.catalog.service.architect.crud;

import org.product.catalog.service.architect.exceptions.TemplateGenerationException;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiPackage;

public abstract class AbstractJavaGenerator {

    private final JavaFileGenerator javaFileGenerator;

    public AbstractJavaGenerator(final JavaFileGenerator javaFileGenerator) {
        this.javaFileGenerator = javaFileGenerator;
    }

    public void generate(final Project project, final PsiDirectory psiDirectory, final String entityName)
            throws TemplateGenerationException {

        final String fileName = getFileName(entityName);

        final String generatedContent = ReadAction.compute(() -> {
            final PsiPackage pkg = JavaDirectoryService.getInstance().getPackage(psiDirectory);
            final String packageName = (pkg != null) ? pkg.getQualifiedName() : "";

            return generateContent(project, packageName, entityName);
        });


        javaFileGenerator.runGeneration(project,
                                        psiDirectory,
                                        fileName,
                                        generatedContent,
                                        getTargetDirectoryName());

    }

    public abstract String generateContent(Project project, String packageName, String entityName) throws TemplateGenerationException;

    public abstract String getTargetDirectoryName();

    public abstract String getFileName(String entityName);
}
