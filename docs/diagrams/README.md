# Database Schema Diagrams

This directory contains the Entity-Relationship Diagram (ERD) for the Bible API database schema.

## Files

- `database_schema.puml`: PlantUML file defining the database schema in UML notation.

## Generating Diagrams

To generate the PNG image from the PlantUML file, you can use one of the following methods:

### Online PlantUML Server

1. Visit [PlantUML Server](http://www.plantuml.com/plantuml/uml/)
2. Copy the content of the `database_schema.puml` file and paste it into the editor
3. The diagram will be generated automatically

### Command Line (requires Java and PlantUML jar)

1. Download the PlantUML jar from [PlantUML website](http://plantuml.com/download)
2. Run the following command:
   ```
   java -jar plantuml.jar database_schema.puml
   ```
3. This will generate a `database_schema.png` file in the same directory

### IDE Plugins

Many IDEs have PlantUML plugins that can render the diagrams directly:

- IntelliJ IDEA: [PlantUML integration](https://plugins.jetbrains.com/plugin/7017-plantuml-integration)
- Visual Studio Code: [PlantUML extension](https://marketplace.visualstudio.com/items?itemName=jebbs.plantuml)
- Eclipse: [PlantUML plugin](https://github.com/hallvard/plantuml)

## Database Schema Overview

The diagram shows the following entities and their relationships:

### User Management
- **Users**: Main user entity with personal information
- **Role**: User roles (ADMIN, LEADER, MEMBER)
- **UserImage**: Profile images for users

### Group Management
- **Group**: Bible study groups
- **GroupType**: Types of groups (VIRTUAL, IN_PERSON)

### Relationships
- Users can have multiple roles
- Users can lead multiple groups
- Users can be members of multiple groups
- Each group has one leader
- Each user can have one profile image