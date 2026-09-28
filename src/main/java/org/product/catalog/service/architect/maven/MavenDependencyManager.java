package org.product.catalog.service.architect.maven;

import org.product.catalog.service.architect.docker.AbstractCreateRootFile;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.XmlElementFactory;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;

public class MavenDependencyManager {


    public record DependencyDto(String groupId, String artifactId, String version) {
        public DependencyDto(final String groupId, final String artifactId) {
            this(groupId, artifactId, null);
        }
    }

    public void addDependencies(final Project project, final DependencyDto... dependencies) {
        for (final DependencyDto dep : dependencies) {
            addDependency(project, dep.groupId(), dep.artifactId(), dep.version());
        }
    }

    public void addDependency(final Project project, final String groupId, final String artifactId, final String version) {
        final PsiDirectory psiDirectory = AbstractCreateRootFile.getRootDirectory(project);
        if (psiDirectory == null) return;

        final PsiFile psiPomFile = psiDirectory.findFile("pom.xml");
        if (!(psiPomFile instanceof XmlFile xmlPomFile)) return;

        WriteCommandAction.runWriteCommandAction(project, () -> {
            final XmlTag projectTag = xmlPomFile.getRootTag();
            if (projectTag == null) return;

            final XmlElementFactory factory = XmlElementFactory.getInstance(project);

            XmlTag dependenciesTag = projectTag.findFirstSubTag("dependencies");
            if (dependenciesTag == null) {
                final XmlTag newDependenciesTag = factory.createTagFromText("<dependencies>\n</dependencies>");
                dependenciesTag = (XmlTag) projectTag.addSubTag(newDependenciesTag, false);
            }

            // Проверка на дубликаты
            final XmlTag[] existingDependencies = dependenciesTag.findSubTags("dependency");
            for (final XmlTag dep : existingDependencies) {
                final XmlTag groupTag = dep.findFirstSubTag("groupId");
                final XmlTag artifactTag = dep.findFirstSubTag("artifactId");

                if (groupTag != null && artifactTag != null) {
                    if (groupId.equals(groupTag.getValue().getTrimmedText()) &&
                            artifactId.equals(artifactTag.getValue().getTrimmedText())) {
                        return; // Зависимость уже есть
                    }
                }
            }

            // Если версия передана — генерируем тег <version>, иначе оставляем пустым для BOM
            final String versionXml = (version != null && !version.isBlank())
                    ? String.format("    <version>%s</version>\n", version)
                    : "";

            final String dependencyXml = String.format("""
                    <dependency>
                        <groupId>%s</groupId>
                        <artifactId>%s</artifactId>
                    %s</dependency>
                    """, groupId, artifactId, versionXml);

            final XmlTag newDependencyTag = factory.createTagFromText(dependencyXml);
            dependenciesTag.addSubTag(newDependencyTag, false);
            CodeStyleManager.getInstance(project).reformat(dependenciesTag);
        });
    }

    public void ensureMapstructCompilerPlugin(final Project project) {
        final PsiDirectory psiDirectory = AbstractCreateRootFile.getRootDirectory(project);
        if (psiDirectory == null) return;

        final PsiFile psiPomFile = psiDirectory.findFile("pom.xml");
        if (!(psiPomFile instanceof XmlFile xmlPomFile)) return;

        WriteCommandAction.runWriteCommandAction(project, () -> {
            final XmlTag projectTag = xmlPomFile.getRootTag();
            if (projectTag == null) return;

            final String pomText = xmlPomFile.getText();
            if (pomText.contains("mapstruct-processor")) {
                return;
            }

            final XmlElementFactory factory = XmlElementFactory.getInstance(project);

            XmlTag buildTag = projectTag.findFirstSubTag("build");
            if (buildTag == null) {
                final XmlTag newBuildTag = factory.createTagFromText("<build>\n</build>");
                buildTag = (XmlTag) projectTag.addSubTag(newBuildTag, false);
            }

            XmlTag pluginsTag = buildTag.findFirstSubTag("plugins");
            if (pluginsTag == null) {
                final XmlTag newPluginsTag = factory.createTagFromText("<plugins>\n</plugins>");
                pluginsTag = (XmlTag) buildTag.addSubTag(newPluginsTag, false);
            }

            final String compilerPluginXml = """
                    <plugin>
                        <groupId>org.apache.maven.plugins</groupId>
                        <artifactId>maven-compiler-plugin</artifactId>
                        <version>3.13.0</version>
                        <configuration>
                            <annotationProcessorPaths>
                                <path>
                                    <groupId>org.projectlombok</groupId>
                                    <artifactId>lombok</artifactId>
                                    <version>${lombok.version}</version>
                                </path>
                                <path>
                                    <groupId>org.projectlombok</groupId>
                                    <artifactId>lombok-mapstruct-binding</artifactId>
                                    <version>0.2.0</version>
                                </path>
                                <path>
                                    <groupId>org.mapstruct</groupId>
                                    <artifactId>mapstruct-processor</artifactId>
                                    <version>1.6.3</version>
                                </path>
                            </annotationProcessorPaths>
                        </configuration>
                    </plugin>
                    """;

            final XmlTag newPluginTag = factory.createTagFromText(compilerPluginXml);
            pluginsTag.addSubTag(newPluginTag, false);
            CodeStyleManager.getInstance(project).reformat(buildTag);
        });
    }
}
