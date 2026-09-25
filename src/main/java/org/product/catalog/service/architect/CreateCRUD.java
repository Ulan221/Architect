package org.product.catalog.service.architect;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDirectory;

public class CreateCRUD extends AnAction {

    @Override
    public void actionPerformed(@NotNull final AnActionEvent e) {
        final Project project = e.getProject();
        if (project == null) {
            return;
        }

        final PsiDirectory baseJavaDirectory = AbstractCreateJavaFile.getTargetDirectory(e);
        if (baseJavaDirectory == null) {
            return;
        }

        final PsiDirectory baseLiquibaseDirectory = AbstractCreateXmlFile.getTargetDirectory(e);
        if (baseLiquibaseDirectory == null) {
            return;
        }

        final String entityName = Messages.showInputDialog(project, "Enter Entity Name for CRUD:", "Create CRUD",
                                                           Messages.getQuestionIcon());
        if (entityName == null || entityName.isEmpty()) {
            return;
        }

        final MavenDependencyManager dependencyManager = new MavenDependencyManager();



        dependencyManager.addDependencies(project,
                                          new MavenDependencyManager.DependencyDto("org.springframework.boot", "spring-boot-starter-web"),
                                          new MavenDependencyManager.DependencyDto("org.springframework.boot", "spring-boot-starter-data-jpa"),
                                          new MavenDependencyManager.DependencyDto("org.postgresql", "postgresql"),
                                          new MavenDependencyManager.DependencyDto("org.liquibase", "liquibase-core"),
                                          new MavenDependencyManager.DependencyDto("org.mapstruct", "mapstruct", "1.6.3"), // <-- ТУТ ЯВНАЯ ВЕРСИЯ
                                          new MavenDependencyManager.DependencyDto("org.projectlombok", "lombok")
        );

        dependencyManager.ensureMapstructCompilerPlugin(project);


        final CreateEntity createEntity = new CreateEntity();
        createEntity.runGeneration(project, baseJavaDirectory, entityName);

        final CreateRequestDTO createRequestDTO = new CreateRequestDTO();
        createRequestDTO.runGeneration(project, baseJavaDirectory, entityName);

        final CreateResponseDTO createResponseDTO = new CreateResponseDTO();
        createResponseDTO.runGeneration(project, baseJavaDirectory, entityName);

        final CreateRepository createRepository = new CreateRepository();
        createRepository.runGeneration(project, baseJavaDirectory, entityName);

        final CreateService createService = new CreateService();
        createService.runGeneration(project, baseJavaDirectory, entityName);

        final CreateController createController = new CreateController();
        createController.runGeneration(project, baseJavaDirectory, entityName);

        final CreateMapper createMapper = new CreateMapper();
        createMapper.runGeneration(project, baseJavaDirectory, entityName);

        final CreateLiquibaseMaster createLiquibaseMaster = new CreateLiquibaseMaster();
        createLiquibaseMaster.runGeneration(project, baseLiquibaseDirectory, entityName);

        final CreateLiquibaseChangeset createLiquibaseChangeset = new CreateLiquibaseChangeset();
        createLiquibaseChangeset.runGeneration(project, baseLiquibaseDirectory, entityName);

        final PsiDirectory rootDir = AbstractCreateRootFile.getRootDirectory(project);

        if (rootDir != null) {
            final CreateDockerfile createDockerfile = new CreateDockerfile();
            createDockerfile.runGeneration(project, rootDir);

            final CreateDockerCompose createDockerCompose = new CreateDockerCompose();
            createDockerCompose.runGeneration(project, rootDir);
        }
    }

}
