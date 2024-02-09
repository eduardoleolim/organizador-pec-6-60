import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    id("org.eduardoleolim.organizadorpec660.kotlin-application-conventions")
    id("org.jetbrains.compose").version("1.5.12")
}

tasks.named("compileKotlin", KotlinCompilationTask::class) {
    compilerOptions {
        freeCompilerArgs.addAll("-Xcontext-receivers=true")
    }
}

dependencies {
    implementation(project(":core"))
    implementation(compose.desktop.currentOs) {
        exclude("org.jetbrains.compose.material")
    }
    implementation(compose.materialIconsExtended)
    implementation(compose.material3)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:${properties["coroutines.version"]}")
    implementation("cafe.adriel.voyager:voyager-navigator:${properties["voyager.version"]}")
    implementation("cafe.adriel.voyager:voyager-screenmodel:${properties["voyager.version"]}")
    implementation("com.seanproctor:data-table-material3:0.5.1")
    implementation("com.github.Dansoftowner:jSystemThemeDetector:${properties["themeDetector.version"]}") {
        exclude("org.slf4j")
    }
}

compose.desktop {
    application {
        mainClass = "org.eduardoleolim.organizadorpec660.app.MainKt"

        val taskNames = project.gradle.startParameter.taskNames
        val runTasks = listOf("run", "${project.name}:run", ":${project.name}:run")

        if (runTasks.any { it in taskNames }) {
            args("--database=../data/organizador-pec-6-60.db")
        } else {
            args("--database=./data/organizador-pec-6-60.db")
        }

        nativeDistributions {
            packageName = "Organizador PEC-6-60"
            description = "Organizador de formatos PEC-6-60"
            copyright = "Copyright © 2023 Angel Eduardo Martinez Leo Lim. All rights reserved."

            includeAllModules = true

            windows {
                iconFile.set(file("src/main/resources/assets/icon.ico"))
            }

            linux {
                iconFile.set(file("src/main/resources/assets/icon.png"))
            }

            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
        }
    }
}
