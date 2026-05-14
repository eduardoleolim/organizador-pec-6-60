# AGENTS.md

## Build Commands

- `./gradlew assemble` - Build all modules
- `./gradlew test` - Run tests (JUnit 5 in core module)
- `./gradlew run` - Run desktop app in development mode
- `./gradlew packageReleaseDistributionForCurrentOS -Penv=production` - Package app for current OS

## Project Structure

Three-module Gradle Kotlin Multiplatform project:

- **`app/`** - Compose Desktop UI, main entry point (`MainKt`), application packaging
- **`core/`** - Business logic, SQLite via Ktorm, domain models, tests
- **`decorated-window/`** - Custom window decorations (title bar, window controls), platform-specific JNA/JBR integration

## Important Notes

- Java 21 required. CI uses JetBrains distribution `21.0.6`
- When editing `decorated-window/`, the local JAR at `decorated-window/libs/jbr-api-1.0.2.jar` is a prebuilt dependency - do not delete
- Linux builds require `dpkg-deb` for post-processing DEB packages (adds pkexec policy)
- Non-production builds use debug directories: `./debug/`, `./debug/logs/`, `./debug/config/`
- Version defined in `gradle.properties`, not in build files

## Running a Single Test

```bash
./gradlew :core:test --tests "FederalEntityCreatorTest"
```

## CI Workflow

- Runs on push/PR to `main` (excludes README, LICENSE, CHANGELOG, IDE configs)
- Matrix: Windows, Ubuntu, macOS
- Test → Package (if no release exists) → Release