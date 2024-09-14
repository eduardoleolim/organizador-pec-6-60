import org.gradle.internal.os.OperatingSystem
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.compose.desktop.application.tasks.AbstractJPackageTask
import java.nio.file.Files
import java.nio.file.StandardCopyOption

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
    implementation(libs.appdirs)
}

compose.resources {
    packageOfResClass = "org.eduardoleolim.organizadorpec660.shared.resources"
}

compose.desktop {
    application {
        mainClass = "MainKt"
        jvmArgs("-Dfile.encoding=UTF-8", "-Dapp.name=${rootProject.name}", "-Dapp.version=${rootProject.version}")

        if (OperatingSystem.current().isMacOsX) {
            jvmArgs("-Dskiko.renderApi=METAL")
        } else {
            jvmArgs("-Dskiko.renderApi=OPENGL")
        }

        nativeDistributions {
            packageName = rootProject.name
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
                menuGroup = "Inegi"
            }

            linux {
                iconFile.set(file("icons/icon.png"))
                appRelease = "1"
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

afterEvaluate {
    tasks.named<AbstractJPackageTask>("packageDeb") {
        val tempDir = project.layout.buildDirectory.dir("./compose/tmp/$name/deb-mod").get().asFile
        val helpersDir = project.layout.projectDirectory.dir("helpers/linux").asFile
        val installerDir = destinationDir.get().asFile

        val policy = helpersDir.resolve("./org.eduardoleolim.organizador-pec-6-60.policy").normalize()
        val postInstScript = helpersDir.resolve("./DEBIAN/postinst").normalize()
        val preRmScript = helpersDir.resolve("./DEBIAN/prerm").normalize()
        val newPolicy = tempDir.resolve("./usr/local/share/${packageName.get()}/${policy.name}").normalize()
        val newPostInstScript = tempDir.resolve("./DEBIAN/postinst").normalize()
        val newPreRmScript = tempDir.resolve("./DEBIAN/prerm").normalize()
        val desktopDir = tempDir.resolve("./opt/${packageName.get()}/lib").normalize()
        val modifiedInstaller = tempDir.resolve("./${packageName.get()}_modified${targetFormat.fileExt}").normalize()

        doLast {
            tempDir.mkdirs()
            val debInstaller: File = installerDir.walk().first { it.isFile && it.name.endsWith(targetFormat.fileExt) }

            val unpackProcess = ProcessBuilder("dpkg-deb", "-R", debInstaller.absolutePath, tempDir.absolutePath)
                .inheritIO()
                .start()
            unpackProcess.waitFor()

            val desktopFile = desktopDir.walk().first { it.isFile && it.name.endsWith(".desktop") }

            desktopFile.apply {
                val desktopContent = readLines().joinToString("\n") {
                    when {
                        it.startsWith("Exec=") -> "Exec=pkexec /opt/${packageName.get()}/bin/${packageName.get()}"
                        it.startsWith("Name=") -> "Name=Organizador PEC-6-60"
                        else -> it
                    }
                }
                writeText(desktopContent)
                renameTo(desktopFile.parentFile.resolve("org.eduardoleolim.${packageName.get()}.desktop"))
            }

            newPolicy.parentFile.mkdirs()
            Files.copy(
                policy.toPath(),
                newPolicy.toPath(),
                StandardCopyOption.COPY_ATTRIBUTES,
                StandardCopyOption.REPLACE_EXISTING
            )
            Files.copy(
                postInstScript.toPath(),
                newPostInstScript.toPath(),
                StandardCopyOption.COPY_ATTRIBUTES,
                StandardCopyOption.REPLACE_EXISTING
            )
            Files.copy(
                preRmScript.toPath(),
                newPreRmScript.toPath(),
                StandardCopyOption.COPY_ATTRIBUTES,
                StandardCopyOption.REPLACE_EXISTING
            )

            val repackProcess =
                ProcessBuilder("dpkg-deb", "--build", tempDir.absolutePath, modifiedInstaller.absolutePath)
                    .inheritIO()
                    .start()
            repackProcess.waitFor()

            modifiedInstaller.copyTo(debInstaller, true)

            tempDir.deleteRecursively()

            logger.lifecycle("The distribution was modified with a custom launcher script")
        }
    }
}
