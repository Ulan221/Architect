package org.product.catalog.service.architect;

import com.intellij.openapi.project.Project;

public class CreateDockerCompose extends AbstractCreateRootFile{
    @Override
    public String generateContent(final Project project) {
        final String dbName = project.getName().toLowerCase().replaceAll("[^a-z0-9]", "") + "_db";

        return """
                version: '3.8'

                services:
                  app:
                    build: .
                    ports:
                      - "8080:8080"
                    environment:
                      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/%s
                      - SPRING_DATASOURCE_USERNAME=postgres
                      - SPRING_DATASOURCE_PASSWORD=postgres
                    depends_on:
                      - postgres

                  postgres:
                    image: postgres:15-alpine
                    container_name: %s
                    environment:
                      - POSTGRES_DB=%s
                      - POSTGRES_USER=postgres
                      - POSTGRES_PASSWORD=postgres
                    ports:
                      - "5432:5432"
                    volumes:
                      - pgdata:/var/lib/postgresql/data

                volumes:
                  pgdata:
                """.formatted(dbName, dbName, dbName);
    }

    @Override
    public String getFileName() {
        return "docker-compose.yml";
    }
}
