package org.product.catalog.service.architect.facade;

import org.jetbrains.annotations.NotNull;
import org.product.catalog.service.architect.crud.ControllerGenerator;
import org.product.catalog.service.architect.crud.EntityGenerator;
import org.product.catalog.service.architect.crud.JavaFileGenerator;
import org.product.catalog.service.architect.crud.JavaSourceDirectoryValidator;
import org.product.catalog.service.architect.crud.MapperGenerator;
import org.product.catalog.service.architect.crud.RepositoryGenerator;
import org.product.catalog.service.architect.crud.RequestDTOGenerator;
import org.product.catalog.service.architect.crud.ResponseDTOGenerator;
import org.product.catalog.service.architect.crud.ServiceGenerator;
import org.product.catalog.service.architect.docker.AbstractCreateRootFile;
import org.product.catalog.service.architect.docker.CreateDockerCompose;
import org.product.catalog.service.architect.docker.CreateDockerfile;
import org.product.catalog.service.architect.exceptions.TemplateGenerationException;
import org.product.catalog.service.architect.liquibase.AbstractCreateXmlFile;
import org.product.catalog.service.architect.liquibase.CreateLiquibaseChangeset;
import org.product.catalog.service.architect.liquibase.CreateLiquibaseMaster;
import org.product.catalog.service.architect.maven.MavenDependencyManager;
import org.product.catalog.service.architect.ui.GenerationOptionsDialog;
import org.product.catalog.service.architect.utils.EntityNameValidator;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;

public class GenerateTemplates extends AnAction {

    private final JavaSourceDirectoryValidator javaSourceDirectoryValidator = new JavaSourceDirectoryValidator();

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
            Messages.showErrorDialog(project, "A file was selected. Please select a package (directory) inside the production source root",
                                     "Package Required");
            return;
        }

        if (!javaSourceDirectoryValidator.validateDirectory(project, baseJavaPsiDirectory)) {
            Messages.showErrorDialog(project, "Invalid directory selected. Please select a package inside the production source root",
                                     "Invalid Directory");
            return;
        }

        final GenerationOptionsDialog generationOptionsDialog = new GenerationOptionsDialog();

        if (!generationOptionsDialog.showAndGet()) {
            return;
        }

        final MavenDependencyManager dependencyManager = new MavenDependencyManager();

        final boolean needLiquibase = generationOptionsDialog.isGenerateLiquibase();

        final boolean needDocker = generationOptionsDialog.isGenerateDocker();

        final JavaFileGenerator javaFileGenerator = new JavaFileGenerator();

        final EntityGenerator entityGenerator = new EntityGenerator(javaFileGenerator);

        final RepositoryGenerator repositoryGenerator = new RepositoryGenerator(javaFileGenerator);

        final ServiceGenerator serviceGenerator = new ServiceGenerator(javaFileGenerator);

        final ControllerGenerator controllerGenerator = new ControllerGenerator(javaFileGenerator);

        final RequestDTOGenerator requestDTOGenerator = new RequestDTOGenerator(javaFileGenerator);

        final ResponseDTOGenerator responseDTOGenerator = new ResponseDTOGenerator(javaFileGenerator);

        final MapperGenerator mapperGenerator = new MapperGenerator(javaFileGenerator);

        final boolean entityFileExist = javaFileGenerator.fileExistsInDirectory(baseJavaPsiDirectory,
                                                                                entityGenerator.getTargetDirectoryName(),
                                                                                entityGenerator.getFileName(entityName));

        final boolean repositoryFileExist = javaFileGenerator.fileExistsInDirectory(baseJavaPsiDirectory,
                                                                                    repositoryGenerator.getTargetDirectoryName(),
                                                                                    repositoryGenerator.getFileName(entityName));

        final boolean serviceFileExist = javaFileGenerator.fileExistsInDirectory(baseJavaPsiDirectory,
                                                                                 serviceGenerator.getTargetDirectoryName(),
                                                                                 serviceGenerator.getFileName(entityName));

        final boolean requestDtoFileExist = javaFileGenerator.fileExistsInDirectory(baseJavaPsiDirectory,
                                                                                    requestDTOGenerator.getTargetDirectoryName(),
                                                                                    requestDTOGenerator.getFileName(entityName));

        final boolean responseDtoFileExist = javaFileGenerator.fileExistsInDirectory(baseJavaPsiDirectory,
                                                                                     responseDTOGenerator.getTargetDirectoryName(),
                                                                                     responseDTOGenerator.getFileName(entityName));

        final boolean controllerFileExist = javaFileGenerator.fileExistsInDirectory(baseJavaPsiDirectory,
                                                                                    controllerGenerator.getTargetDirectoryName(),
                                                                                    controllerGenerator.getFileName(entityName));

        final boolean mapperFileExist = javaFileGenerator.fileExistsInDirectory(baseJavaPsiDirectory,
                                                                                mapperGenerator.getTargetDirectoryName(),
                                                                                mapperGenerator.getFileName(entityName));

        ProgressManager.getInstance()
                       .run(new Task.Backgroundable(project, "Architect: generate", true) {

                           @Override
                           public void run(
                                   @NotNull final ProgressIndicator indicator) {

                               indicator.setText("Generation crud...");
                               indicator.setFraction(0.2);

                               if (!entityFileExist && !repositoryFileExist && !serviceFileExist && !requestDtoFileExist
                                       && !responseDtoFileExist && !controllerFileExist && !mapperFileExist) {

                                   try {
                                       entityGenerator.generate(project, baseJavaPsiDirectory, entityName);

                                       repositoryGenerator.generate(project, baseJavaPsiDirectory, entityName);

                                       serviceGenerator.generate(project, baseJavaPsiDirectory, entityName);

                                       controllerGenerator.generate(project, baseJavaPsiDirectory, entityName);

                                       requestDTOGenerator.generate(project, baseJavaPsiDirectory, entityName);

                                       responseDTOGenerator.generate(project, baseJavaPsiDirectory, entityName);

                                       mapperGenerator.generate(project, baseJavaPsiDirectory, entityName);
                                   } catch (TemplateGenerationException e1) {
                                       Messages.showErrorDialog(e1.getMessage(), "Template Generation Error");
                                   }
                               }

                               if (needLiquibase) {
                                   indicator.setText("Generation liquibase...");
                                   indicator.setFraction(0.5);

                                   final PsiDirectory baseLiquibasePsiDirectory = AbstractCreateXmlFile.getTargetDirectory(e);

                                   if (baseLiquibasePsiDirectory == null) {
                                       return;
                                   }
                                   final CreateLiquibaseMaster createLiquibaseMaster = new CreateLiquibaseMaster();
                                   final String liquibaseMasterFile = createLiquibaseMaster.getFileNameWithSuffix(entityName);


                                   final CreateLiquibaseChangeset createLiquibaseChangeset = new CreateLiquibaseChangeset();
                                   final String liquibaseChangeSetFile = createLiquibaseChangeset.getFileNameWithSuffix(entityName);
                                   final PsiFile[] foundLiquibaseChangesetFile = new PsiFile[1];
                                   final PsiFile[] foundLiquibaseMasterFile = new PsiFile[1];

                                   ReadAction.compute(() -> {
                                       final VirtualFile vfChangeSet = baseLiquibasePsiDirectory.getVirtualFile()
                                                                                                .findFileByRelativePath(
                                                                                                        "db/changelog/changesets");

                                       if (vfChangeSet != null) {
                                           final PsiDirectory changesetsDir = PsiManager.getInstance(project)
                                                                                        .findDirectory(vfChangeSet);

                                           foundLiquibaseChangesetFile[0] = changesetsDir.findFile(liquibaseChangeSetFile);
                                       } else {
                                           foundLiquibaseChangesetFile[0] = null;
                                       }


                                       final VirtualFile vfMaster = baseLiquibasePsiDirectory.getVirtualFile()
                                                                                             .findFileByRelativePath("db/changelog");

                                       if (vfMaster != null) {
                                           final PsiDirectory masterDir = PsiManager.getInstance(project)
                                                                                    .findDirectory(vfMaster);
                                           foundLiquibaseMasterFile[0] = masterDir.findFile(liquibaseMasterFile);
                                       } else {
                                           foundLiquibaseMasterFile[0] = null;
                                       }

                                       return null;
                                   });

                                   if (foundLiquibaseChangesetFile[0] == null && !liquibaseChangeSetFile.isEmpty()) {
                                       createLiquibaseChangeset.runGeneration(project, baseLiquibasePsiDirectory, entityName);
                                   }

                                   if (foundLiquibaseMasterFile[0] == null && !liquibaseMasterFile.isEmpty()) {

                                       createLiquibaseMaster.runGeneration(project, baseLiquibasePsiDirectory, entityName);

                                       dependencyManager.addDependencies(project,
                                                                         new MavenDependencyManager.DependencyDto("org.liquibase",
                                                                                                                  "liquibase-core",
                                                                                                                  "4.29.0"));

                                   }
                               }

                               if (needDocker) {
                                   indicator.setText("Generation docker...");
                                   indicator.setFraction(0.7);

                                   final PsiDirectory rootDir = AbstractCreateRootFile.getRootDirectory(project);

                                   if (rootDir != null) {
                                       final CreateDockerfile createDockerfile = new CreateDockerfile();

                                       createDockerfile.runGeneration(project, rootDir);

                                       final CreateDockerCompose createDockerCompose = new CreateDockerCompose();

                                       createDockerCompose.runGeneration(project, rootDir);
                                   }
                               }

                               indicator.setText("Add dependencies...");
                               indicator.setFraction(0.9);

                               dependencyManager.addDependencies(project,
                                                                 new MavenDependencyManager.DependencyDto("org.springframework.boot",
                                                                                                          "spring-boot-starter-web",
                                                                                                          "3.3.4"),
                                                                 new MavenDependencyManager.DependencyDto("org.springframework.boot",
                                                                                                          "spring-boot-starter-data-jpa",
                                                                                                          "3.3.4"),
                                                                 new MavenDependencyManager.DependencyDto("org.postgresql", "postgresql",
                                                                                                          "42.7.4"),
                                                                 new MavenDependencyManager.DependencyDto("org.mapstruct", "mapstruct",
                                                                                                          "1.6.3"),
                                                                 new MavenDependencyManager.DependencyDto("org.projectlombok", "lombok",
                                                                                                          "1.18.48"));

                               dependencyManager.ensureMapstructCompilerPlugin(project);
                               dependencyManager.reload(project);

                               indicator.setFraction(1.0);
                           }
                       });
    }

    public static PsiDirectory getTargetDirectory(final AnActionEvent e) {
        final Object data = e.getData(CommonDataKeys.PSI_ELEMENT);

        if (data instanceof PsiDirectory dir) {
            return dir;
        }

        return null;
    }
}
