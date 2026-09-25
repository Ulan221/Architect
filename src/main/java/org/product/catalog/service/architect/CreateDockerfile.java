package org.product.catalog.service.architect;

import com.intellij.openapi.project.Project;

public class CreateDockerfile extends AbstractCreateRootFile{

    @Override
    public String generateContent(final Project project) {
        return """
                # Stage 1: Build stage
                FROM eclipse-temurin:17-jdk-alpine AS build
                WORKDIR /app
                COPY . .
                RUN ./mvnw clean package -DskipTests || mvn clean package -DskipTests

                # Stage 2: Runtime stage
                FROM eclipse-temurin:17-jre-alpine
                WORKDIR /app
                COPY --from=build /app/target/*.jar app.jar
                EXPOSE 8080
                ENTRYPOINT ["java", "-jar", "app.jar"]
                """;
    }

    @Override
    public String getFileName() {
        return "Dockerfile";
    }
}
