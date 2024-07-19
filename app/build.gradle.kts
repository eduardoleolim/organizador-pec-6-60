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
    implementation(libs.kotlin.csv.jvm)
    implementation(libs.pdfbox) {
        exclude("org.junit.jupiter")
    }
    implementation(libs.jSystemThemeDetector) {
        exclude("org.slf4j")
    }
    implementation(libs.properlty)
}

compose.desktop {
    application {
        mainClass = "MainKt"
        jvmArgs("-Dfile.encoding=UTF-8")

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
                menuGroup = "Inegi"
            }

            linux {
                iconFile.set(file("src/main/composeResources/drawable/logo.png"))
                shortcut = true
                menuGroup = "Inegi"
            }

            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
        }

        buildTypes.release.proguard {
            configurationFiles.from(file("compose-desktop.pro"))
        }
    }
}

println(JavaVersion.current())
