# AGENTS.md - AI Coding Agent Guidelines

## Project Overview

**PFLogistica** is a Java Maven project (logistics platform foundation) currently in early-stage development. The architecture follows a classic MVC pattern with planned separation between model, controller, and view controller layers.

- **Build System**: Maven (Java 25)
- **Project Structure**: MVC-inspired with `/model`, `/controller`, `/viewcontroller` packages
- **Current State**: Skeleton phase with incomplete model classes and empty package directories

## Key Architecture Patterns

### Package Structure & Responsibilities

```
org.example/
├── Main.java              # Entry point; uses IO utility for printing
├── model/                 # Domain entities (incomplete)
│   ├── Persona.java      # Abstract base class (WIP - incomplete attributes)
│   └── Usuario.java      # User entity extending/implementing Persona concept
├── controller/            # Business logic layer (empty - ready for implementation)
└── viewcontroller/        # UI/View management layer (empty - ready for implementation)
```

**Critical Pattern**: The project uses an `IO` utility class (referenced in Main.java) that's likely custom-built or imported. When adding new logging/output functionality, use `IO.println()` rather than `System.out.println()`.

### Model Layer (org.example.model)

- **Persona.java**: Abstract base class for all person-like entities
  - Incomplete: Missing attribute declarations and methods
  - Design pattern: Template for inheritance hierarchy (Usuario likely extends this)
  - Convention: Keep domain objects simple; business logic goes to controllers
  
- **Usuario.java**: Currently empty; expected to represent users in the logistics system
  - Should extend or reference Persona
  - Will require attributes (username, password, roles, etc.) once requirements are clarified

### Empty Directories - Future Growth Areas

- **controller/**: Will contain business logic, request handlers, and workflow orchestration
  - Expected: User management, order processing, logistics coordination
- **viewcontroller/**: Planned for UI integration or API response formatting
  - May indicate a future Swing/JavaFX desktop GUI or REST API layer

## Development Workflow & Build Commands

### Maven Build

```bash
# Clean and compile
mvn clean compile

# Run tests (currently empty)
mvn test

# Package JAR
mvn clean package

# Run the application
mvn exec:java -Dexec.mainClass="org.example.Main"
```

### IDE Quick Actions (IntelliJ IDEA)

The codebase is configured for IntelliJ IDEA with reference to keyboard shortcuts:
- Run code: `Ctrl+Shift+F10` (referenced in Main.java comments)
- Show intentions: `Alt+Enter` (for quick fixes)
- Toggle breakpoint: `Ctrl+F8` (debugging is set up)

## Important Conventions & Non-Standard Practices

### 1. IO Utility Class Usage
- **Pattern**: Use `IO.println()` for all console output instead of `System.out.println()`
- **Rationale**: Centralizes logging; likely to support future file/GUI output redirection
- **Location**: Must be in classpath; verify import in new files

### 2. Java 25 Language Features
- Project targets Java 25 (modern JVM features available)
- Consider using records, sealed classes, pattern matching when defining new model entities
- Compiler enforces UTF-8 encoding: ensure all source files use UTF-8

### 3. Package Naming Convention
- Standard: `org.example.*` (domain-driven starting point)
- Future: Likely to evolve to `org.logistica.*` or similar as project matures

### 4. No External Dependencies Currently
- pom.xml has NO dependencies declared (only compiler properties)
- **When adding libraries**: Update pom.xml `<dependencies>` section and document in this file
- Common needs likely: JUnit for testing, potentially Spring/Quarkus for web layer later

## Critical Developer Workflows

### Adding a New Model Class

1. Create class in `org.example.model` package
2. Follow naming: `EntityName.java` (capitalized nouns)
3. Reference Persona as potential parent class if entity represents a person
4. Include package declaration and appropriate class structure
5. Example: `Paquete.java` for package/shipment entities

### Adding Business Logic (Controller)

1. Create in `org.example.controller` package
2. Naming: `{Entity}Controller.java` or `{Entity}Service.java`
3. Controllers should:
   - Accept model objects
   - Perform validation/business rules
   - Use `IO.println()` for logging
   - Return result objects to viewcontroller layer
4. Avoid mixing UI concerns with business logic

### Debugging & Testing

- Main.java includes a breakpoint example for learning
- Test classes go in `src/test/java/org/example/*` (mirrors main structure)
- Run individual tests: `mvn test -Dtest=ClassName`
- Debug: Right-click on main method and select "Debug" in IDE

## Integration Points & External Communication

### Current State
- **No external integrations yet** (no database, API clients, or messaging configured)
- **Data persistence**: To be implemented (likely JDBC or ORM framework)
- **API layer**: viewcontroller likely becomes REST endpoint container

### Future Considerations

When integrating external systems:
- Add dependency to `pom.xml`
- Create dedicated layer/package for external client code
- Keep domain models (org.example.model) independent of external system specifics
- Use adapter pattern for complex integrations

## Files to Reference When Implementing Features

| Feature | Reference Files |
|---------|-----------------|
| New entity type | Persona.java, Usuario.java |
| Business logic | Main.java (for IO usage) |
| Build/compile issues | pom.xml (versions, compiler config) |
| Testing structure | src/test/java/ (currently empty) |
| Entry point flow | Main.java (shows basic pattern) |

## Common Pitfalls to Avoid

1. **Using System.out instead of IO**: Always use `IO.println()` for consistency
2. **Mixing layers**: Don't put UI code in models or business logic in controllers meant for routing
3. **Missing package declarations**: Every .java file must have explicit `package org.example.*` declaration
4. **Ignoring Java 25 capabilities**: Take advantage of modern features for cleaner code
5. **Not updating pom.xml for new dependencies**: Always add explicit dependency declarations

## Quick Reference for Common Tasks

- **Find all references to a class**: `Ctrl+Alt+F7` in IntelliJ
- **Refactor class name**: Right-click class → Refactor → Rename
- **Generate getters/setters**: Place caret in class → Alt+Insert → Getter/Setter
- **Add TODO comment**: Type `//TODO` - IntelliJ tracks these in Tool Window

