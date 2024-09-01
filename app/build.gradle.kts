import org.gradle.internal.os.OperatingSystem
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
}

dependencies {
    implementation(project(":core"))
    implementation(compose.desktop.currentOs) {
        exclude("org.jetbrains.compose.material")
    }
    implementation(compose.components.resources)
    implementation(compose.materialIconsExtended)
    implementation(compose.material3)
    implementation(libs.jna.core)
    implementation(libs.jna.platform)
    implementation(libs.kotlinx.coroutines.swing)
    implementation(libs.voyager.navigator)
    implementation(libs.voyager.screenmodel)
    implementation(libs.voyager.transitions)
    implementation(libs.material3.datatable)
    implementation(libs.pdfbox) {
        exclude("org.junit.jupiter")
    }
    implementation(libs.jSystemThemeDetector) {
        exclude("org.slf4j")
        exclude("net.java.dev.jna")
    }
    implementation(libs.properlty)
}

compose.desktop {
    application {
        mainClass = "MainKt"
        jvmArgs("-Dfile.encoding=UTF-8")

        if (OperatingSystem.current().isMacOsX) {
            jvmArgs("-Dskiko.renderApi=METAL")
        } else {
            jvmArgs("-Dskiko.renderApi=OPENGL")
        }

        nativeDistributions {
            packageName = "Organizador PEC-6-60"
            description = "Organizador de formatos PEC-6-60"
            copyright = "Copyright © 2024 Angel Eduardo Martinez Leo Lim. All rights reserved."
            vendor = "Angel Eduardo Martinez Leo Lim"
            licenseFile.set(file("../LICENSE.txt"))

            includeAllModules = true

            appResourcesRootDir.set(project.layout.projectDirectory.dir("resources"))

            windows {
                iconFile.set(file("icons/icon.ico"))
                dirChooser = true
                shortcut = true
                menuGroup = "Inegij"
            }

            linux {
                iconFile.set(file("icons/icon.png"))
                shortcut = true
                menuGroup = "Inegi"
                debMaintainer = "eduardoleolim@hotmail.com"
                appCategory = "Office"
            }

            macOS {
                iconFile.set(file("icons/icon.icns"))
                appCategory = "public.app-category.productivity"
            }

            targetFormats(TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Dmg)
        }

        buildTypes.release.proguard {
            configurationFiles.from(file("compose-desktop.pro"))
        }
    }
}
