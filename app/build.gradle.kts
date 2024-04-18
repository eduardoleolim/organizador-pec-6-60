import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.eduardoleolim.organizadorpec660.kotlin-application-conventions")
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.addAll("-Xcontext-receivers")
    }
}

dependencies {
    implementation(project(":core"))
    implementation(compose.desktop.currentOs) {
        exclude("org.jetbrains.compose.material")
    }
    implementation(compose.components.resources)
    implementation(compose.materialIconsExtended)
    implementation(compose.material3)
    implementation(libs.kotlinx.coroutines.swing)
    implementation(libs.voyager.navigator)
    implementation(libs.voyager.screenmodel)
    implementation(libs.material3.datatable)
    implementation(libs.pdfbox) {
        exclude("org.junit.jupiter")
    }
    implementation(libs.jSystemThemeDetector) {
        exclude("org.slf4j")
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        jvmArgs("-Dfile.encoding=UTF-8")

        args("--database=./data/organizador-pec-6-60.db")

        nativeDistributions {
            packageName = "Organizador PEC-6-60"
            description = "Organizador de formatos PEC-6-60"
            copyright = "Copyright © 2023 Angel Eduardo Martinez Leo Lim. All rights reserved."
            vendor = "Angel Eduardo Martinez Leo Lim"

            includeAllModules = true

            appResourcesRootDir.set(project.layout.projectDirectory.dir("resources"))

            windows {
                iconFile.set(file("src/main/composeResources/drawable/icon.ico"))
                dirChooser = true
                shortcut = true
            }

            linux {
                iconFile.set(file("src/main/composeResources/drawable/logo.png"))
            }

            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
        }
    }
}
