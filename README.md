# Architect - IntelliJ IDEA Plugin 🚀

**Architect** is a developer productivity plugin designed to eliminate repetitive boilerplate code in Spring Boot applications. With a single click, it generates domain DTOs, MapStruct mappers, Liquibase database migrations, and auto-configures Maven dependencies.

## ✨ Features

- **Automated DTO & Mapper Creation:** Instantly builds Data Transfer Objects and MapStruct interfaces for your entities.
- **Liquibase Integration:** Generates changeset XML files aligned with project entity definitions.
- **Dependency Management:** Automatically injects required Spring Boot, MapStruct, Lombok, and Liquibase Maven dependencies.
- **Compiler Plugin Injector:** Configures `maven-compiler-plugin` annotation processors (`lombok-mapstruct-binding`).

## 🛠️ Installation

1. Open **IntelliJ IDEA**.
2. Go to `Settings` -> `Plugins` -> `Marketplace`.
3. Search for **Architect**.
4. Click **Install** and restart the IDE.

## 🚀 Usage

1. Right-click on an Entity class or inside the editor.
2. Select **Architect** -> **Generate CRUD 🚀**.
3. Let the plugin build your infrastructure files automatically.

## 📄 License

Distributed under the Apache 2.0 License.
