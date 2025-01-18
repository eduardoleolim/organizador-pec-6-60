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

val env: String? by project

dependencies {
    implementation(project(":core"))
    implementation(project(":decorated-window"))
    implementation(compose.desktop.currentOs) {
        exclude("org.jetbrains.compose.material")
    }
    implementation(compose.components.resources)
    implementation(compose.materialIconsExtended)
    implementation(compose.material3)
    implementation(libs.material3.windowSizeClass)
    implementation(libs.kotlinx.coroutines.swing)
    // implementation("org.jetbrains.androidx.navigation:navigation-compose:2.8.0-alpha10")
    // implementation("org.jetbrains.compose.material3.adaptive:adaptive:1.0.0-alpha03")
    // implementation("org.jetbrains.compose.material3.adaptive:adaptive-layout:1.0.0-alpha03")
    // implementation("org.jetbrains.compose.material3.adaptive:adaptive-navigation:1.0.0-alpha03")
    // implementation("org.jetbrains.compose.material3:material3-adaptive-navigation-suite:1.7.0-beta02")
    implementation(libs.material3.datatable)
    implementation(libs.voyager.navigator)
    implementation(libs.voyager.screenmodel)
    implementation(libs.voyager.transitions)
    implementation(libs.pdfbox)
    implementation(libs.properlty)
    implementation(libs.appdirs)
}

compose.resources {
    packageOfResClass = "org.eduardoleolim.organizadorpec660.shared.resources"
}

compose.desktop {
    application {
        val skikoApi = if (OperatingSystem.current().isMacOsX) "METAL" else "OPENGL"

        mainClass = "MainKt"
        jvmArgs(
            "-Dskiko.renderApi=$skikoApi",
            "-Dfile.encoding=UTF-8",
            "-Dapp.name=${rootProject.name}",
            "-Dapp.version=${rootProject.version}"
        )

        if (env != "production") {
            jvmArgs("-Dapp.data.dir=./debug", "-Dapp.logs.dir=./debug/logs", "-Dapp.config.dir=./debug/config")
        }

        nativeDistributions {
            packageName = if (OperatingSystem.current().isWindows) "OrganizadorPEC660" else "organizador-pec-6-60"
            description = "Organizador de formatos PEC-6-60"
            copyright = "Copyright © 2024-2025 Angel Eduardo Martinez Leo Lim. Licensed under the GNU GPL v3."
            vendor = "Angel Eduardo Martinez Leo Lim"
            licenseFile.set(file("../LICENSE.txt"))

            includeAllModules = true
            appResourcesRootDir.set(project.layout.projectDirectory.dir("resources"))

            windows {
                upgradeUuid = "e59c1d59-bf07-4a62-9a4e-646de0a1324e"
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
            version.set("7.5.0")
            configurationFiles.from(file("compose-desktop.pro"))
        }
    }
}

afterEvaluate {
    fun updateDebPackage(
        packageName: String,
        targetFormat: TargetFormat,
        tempDir: File,
        helpersDir: File,
        installerDir: File
    ) {
        val policy = helpersDir.resolve("./org.eduardoleolim.organizador-pec-6-60.policy").normalize()
        val postInstScript = helpersDir.resolve("./DEBIAN/postinst").normalize()
        val preRmScript = helpersDir.resolve("./DEBIAN/prerm").normalize()
        val newPolicy = tempDir.resolve("./usr/local/share/${packageName}/${policy.name}").normalize()
        val newPostInstScript = tempDir.resolve("./DEBIAN/postinst").normalize()
        val newPreRmScript = tempDir.resolve("./DEBIAN/prerm").normalize()
        val desktopDir = tempDir.resolve("./opt/${packageName}/lib").normalize()
        val modifiedInstaller = tempDir.resolve("./${packageName}_modified${targetFormat.fileExt}").normalize()

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
                    it.startsWith("Exec=") -> "Exec=pkexec /opt/${packageName}/bin/${packageName}"
                    it.startsWith("Name=") -> "Name=Organizador PEC-6-60"
                    else -> it
                }
            }
            writeText(desktopContent)
            renameTo(desktopFile.parentFile.resolve("org.eduardoleolim.${packageName}.desktop"))
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
    }

    tasks.named<AbstractJPackageTask>("packageDeb") {
        val tempDir = project.layout.buildDirectory.dir("./compose/tmp/$name/deb-mod").get().asFile
        val helpersDir = project.layout.projectDirectory.dir("helpers/linux").asFile
        val installerDir = destinationDir.get().asFile

        doLast {
            updateDebPackage(packageName.get(), targetFormat, tempDir, helpersDir, installerDir)
            logger.lifecycle("The distribution was modified with a custom launcher script")
        }
    }

    tasks.named<AbstractJPackageTask>("packageReleaseDeb") {
        val tempDir = project.layout.buildDirectory.dir("./compose/tmp/$name/deb-mod").get().asFile
        val helpersDir = project.layout.projectDirectory.dir("helpers/linux").asFile
        val installerDir = destinationDir.get().asFile

        doLast {
            updateDebPackage(packageName.get(), targetFormat, tempDir, helpersDir, installerDir)
            logger.lifecycle("The distribution was modified with a custom launcher script")
        }
    }
}
