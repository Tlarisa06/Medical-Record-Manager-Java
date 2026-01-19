# Medical-Record-Manager-Java
Modular Java application (Layered Architecture) featuring polymorphic persistence: SQL, Binary, Text, and RAM. Includes a dual interface (JavaFX & CLI) configurable via a settings.properties file. The project utilizes Generic Repositories, Java Streams for reporting, Unit Testing (JUnit 5 with >90% code coverage), and Java Faker for database population.

## Architecture and Modules
The project is organized into four main layers:
1. **Domain**: Contains entities and abstract classes. All objects are uniquely identifiable through an automatically generated and persistent ID system.
2. **Repository**: Generic implementation for data storage. Supports multiple persistence mechanisms through polymorphism.
3. **Service**: Contains the business logic and data processing using Java 8 Streams for report generation.
4. **User Interface**: Dual interface support (Command Line Interface and JavaFX Graphical User Interface).

## Technical Features
- **Multi-Source Persistence**: The system allows data storage in Text, Binary (Java Serialization), Memory (RAM), or SQL database formats.
- **External Configuration**: The repository type (SQL, Binary, Text) and the display mode (CLI, JavaFX) are managed through the `settings.properties` file, requiring no source code modifications.
- **Generics**: Extensive use of generic interfaces and classes for CRUD operations, allowing easy extension with new entities.
- **Identification System**: An ID generator that persists its state in a text file to ensure ID uniqueness across application restarts.
- **Validation and Exceptions**: Custom exception hierarchy (RepositoryException, DuplicateIDException, etc.) for robust handling of input or system errors.

## Technologies Used
- Java 17+
- JavaFX for the Graphical User Interface (GUI)
- JDBC for SQL database integration
- JUnit 5 for Unit Testing (Code coverage > 90%)
- Java Faker for generating pseudo-random test data

## Configuration Instructions
The application uses a properties file to control execution. Example configuration in `settings.properties`:

```properties
Repository = database
Patients = patients.bin
Appointments = appointments.bin
Display = javafx
