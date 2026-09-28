package org.product.catalog.service.architect.facade;

import org.jetbrains.annotations.NotNull;
import org.product.catalog.service.architect.crud.ControllerGenerator;
import org.product.catalog.service.architect.crud.EntityGenerator;
import org.product.catalog.service.architect.crud.JavaFileGenerator;
import org.product.catalog.service.architect.crud.MapperGenerator;
import org.product.catalog.service.architect.crud.RepositoryGenerator;
import org.product.catalog.service.architect.crud.RequestDTOGenerator;
import org.product.catalog.service.architect.crud.ResponseDTOGenerator;
import org.product.catalog.service.architect.crud.ServiceGenerator;
import org.product.catalog.service.architect.docker.AbstractCreateRootFile;
import org.product.catalog.service.architect.docker.CreateDockerCompose;
import org.product.catalog.service.architect.docker.CreateDockerfile;
import org.product.catalog.service.architect.liquibase.AbstractCreateXmlFile;
import org.product.catalog.service.architect.liquibase.CreateLiquibaseChangeset;
import org.product.catalog.service.architect.liquibase.CreateLiquibaseMaster;
import org.product.catalog.service.architect.maven.MavenDependencyManager;
import org.product.catalog.service.architect.ui.GenerationOptionsDialog;
import org.product.catalog.service.architect.utils.EntityNameValidator;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiJavaFile;

public class GenerateTemplates extends AnAction {

    @Override
    public void actionPerformed(@NotNull final AnActionEvent e) {
        final Project project = e.getProject();
        if (project == null) {
            return;
        }

        final String entityName = Messages.showInputDialog(project, "Enter entity name for CRUD (e.g. user, orderItem):", "Create CRUD",
                                                           Messages.getQuestionIcon(), "", new EntityNameValidator());

        if (entityName == null || entityName.isEmpty()) {
            return;
        }

        final PsiDirectory baseJavaPsiDirectory = getTargetDirectory(e);
        if (baseJavaPsiDirectory == null) {
            return;
        }

        final PsiDirectory baseLiquibasePsiDirectory = AbstractCreateXmlFile.getTargetDirectory(e);
        if (baseLiquibasePsiDirectory == null) {
            return;
        }

        final GenerationOptionsDialog generationOptionsDialog = new GenerationOptionsDialog();
        if (!generationOptionsDialog.showAndGet()) {
            return;
        }

        final boolean needLiquibase = generationOptionsDialog.isGenerateLiquiBase();

        final boolean needDocker = generationOptionsDialog.isGenerateDocker();

        final MavenDependencyManager dependencyManager = new MavenDependencyManager();

        dependencyManager.addDependencies(project,
                                          new MavenDependencyManager.DependencyDto("org.springframework.boot", "spring-boot-starter-web"),
                                          new MavenDependencyManager.DependencyDto("org.springframework.boot",
                                                                                   "spring-boot-starter-data-jpa"),
                                          new MavenDependencyManager.DependencyDto("org.postgresql", "postgresql"),
                                          new MavenDependencyManager.DependencyDto("org.liquibase", "liquibase-core"),
                                          new MavenDependencyManager.DependencyDto("org.mapstruct", "mapstruct", "1.6.3"),
                                          // <-- ТУТ ЯВНАЯ ВЕРСИЯ
                                          new MavenDependencyManager.DependencyDto("org.projectlombok", "lombok"));

        dependencyManager.ensureMapstructCompilerPlugin(project);

        final JavaFileGenerator javaFileGenerator = new JavaFileGenerator();

        final EntityGenerator entityGenerator = new EntityGenerator(javaFileGenerator);
        entityGenerator.generate(project, baseJavaPsiDirectory, entityName);

        final RepositoryGenerator repositoryGenerator = new RepositoryGenerator(javaFileGenerator);
        repositoryGenerator.generate(project, baseJavaPsiDirectory, entityName);

        final ServiceGenerator serviceGenerator = new ServiceGenerator(javaFileGenerator);
        serviceGenerator.generate(project, baseJavaPsiDirectory, entityName);

        final ControllerGenerator controllerGenerator = new ControllerGenerator(javaFileGenerator);
        controllerGenerator.generate(project, baseJavaPsiDirectory, entityName);

        final RequestDTOGenerator requestDTOGenerator = new RequestDTOGenerator(javaFileGenerator);
        requestDTOGenerator.generate(project, baseJavaPsiDirectory, entityName);

        final ResponseDTOGenerator responseDTOGenerator = new ResponseDTOGenerator(javaFileGenerator);
        responseDTOGenerator.generate(project, baseJavaPsiDirectory, entityName);

        final MapperGenerator mapperGenerator = new MapperGenerator(javaFileGenerator);
        mapperGenerator.generate(project, baseJavaPsiDirectory, entityName);

        if (needLiquibase) {
            final CreateLiquibaseMaster createLiquibaseMaster = new CreateLiquibaseMaster();
            createLiquibaseMaster.runGeneration(project, baseLiquibasePsiDirectory, entityName);

            final CreateLiquibaseChangeset createLiquibaseChangeset = new CreateLiquibaseChangeset();
            createLiquibaseChangeset.runGeneration(project, baseLiquibasePsiDirectory, entityName);
        }

        if (needDocker) {
            final PsiDirectory rootDir = AbstractCreateRootFile.getRootDirectory(project);

            if (rootDir != null) {
                final CreateDockerfile createDockerfile = new CreateDockerfile();
                createDockerfile.runGeneration(project, rootDir);

                final CreateDockerCompose createDockerCompose = new CreateDockerCompose();
                createDockerCompose.runGeneration(project, rootDir);
            }
        }
    }

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
