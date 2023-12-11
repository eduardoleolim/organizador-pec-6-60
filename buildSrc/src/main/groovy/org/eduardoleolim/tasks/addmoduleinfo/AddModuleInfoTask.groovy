package org.eduardoleolim.tasks.addmoduleinfo

import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

import java.nio.file.Files
import java.nio.file.StandardCopyOption

abstract class AddModuleInfoTask extends DefaultTask {
    @Input
    abstract Property<String> getModuleName()

    @Input
    abstract Property<String> getMultiRelease()

    @Input
    abstract Property<String> getJarPath()

    @Input
    abstract Property<FileCollection> getClasspath()

    @Input
    abstract Property<Boolean> getReplace()

    AddModuleInfoTask() {
        group = 'custom'
        description = 'Add module-info.java to jar file'

        moduleName.convention('org.module.name')
        jarPath.convention(project.tasks.jar.archiveFile.get().toString())
        classpath.convention(project.files(project.file(".")))
        multiRelease.convention(null)
        replace.convention(true)
    }

    @TaskAction
    def addModuleInfo() {
        try {
            this.getTemporaryDir().mkdirs()

            def originalJarFile = new File(jarPath.get())
            def tempJarFile = new File(this.getTemporaryDir(), originalJarFile.name)
            Files.copy(originalJarFile.toPath(), tempJarFile.toPath(), StandardCopyOption.REPLACE_EXISTING)

            def modulepath = classpath.get().join(File.pathSeparator)

            if (this.replace.get()) {
                removeModuleInfo(tempJarFile)
            }

            def moduleInfoFile = createModuleInfo(moduleName.get(), tempJarFile, modulepath)
            def moduleClassFile = compileModuleInfo(moduleName.get(), moduleInfoFile, tempJarFile, modulepath)
            addModuleInfoToJar(moduleClassFile, tempJarFile)

            Files.copy(tempJarFile.toPath(), originalJarFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
            this.getTemporaryDir().deleteDir()
        } catch (Exception e) {
            throw new GradleException("Error adding module-info.java to jar file", e)
        }
    }

    def removeModuleInfo(File jarFile) {
        def command = new ArrayList<String>(['jar', '-xf', jarFile.absolutePath].toList())

        executeCommandLine(command, jarFile.parentFile)

        jarFile.delete()

        def moduleInfoFile = new File(jarFile.parentFile, "module-info.class")
        if (moduleInfoFile.exists()) {
            moduleInfoFile.delete()
        }

        def createJarCommand = new ArrayList<String>(['jar', '-cf', jarFile.absolutePath, '.'].toList())

        executeCommandLine(createJarCommand, jarFile.parentFile)
    }

    def createModuleInfo(String moduleName, File jarFile, String modulepath) {
        def directory = jarFile.parentFile
        def multiRelease = this.multiRelease.get()

        def listCommand = new ArrayList<String>(['jdeps', '--module-path', modulepath, '--generate-module-info', directory.absolutePath, jarFile.absolutePath].toList())
        if (multiRelease != null) {
            listCommand.add(1, '--multi-release')
            listCommand.add(2, multiRelease)
        }

        executeCommandLine(listCommand)

        def moduleInfoPath = "${directory.absolutePath}/module-info.java"
        if (multiRelease != null) {
            def jarFileName = jarFile.name.replace(".jar", "")
            moduleInfoPath = "${directory.absolutePath}/${jarFileName}/versions/${this.multiRelease.get()}/module-info.java"
        }

        def moduleInfoFile = new File(moduleInfoPath)

        if (!moduleInfoFile.exists()) {
            throw new GradleException("Module-info.java not found")
        }

        def content = moduleInfoFile.text
        content = content.replaceFirst("module\\s+[^\\s]+\\s+\\{", "open module $moduleName {")
        moduleInfoFile.write content

        return moduleInfoFile
    }

    def compileModuleInfo(String moduleName, File moduleInfo, File jarFile, String modulepath) {
        def directory = jarFile.parentFile

        def listCommand = new ArrayList<String>(['javac', '-d', directory.absolutePath, '--module-path', modulepath, '--patch-module', moduleName + "=" + jarFile.absolutePath, moduleInfo.absolutePath].toList())

        executeCommandLine(listCommand)

        moduleInfo.delete()

        def moduleInfoClass = project.file("${directory.absolutePath}/module-info.class")

        if (!moduleInfoClass.exists()) {
            throw new GradleException("Module-info.class not found")
        }

        return moduleInfoClass
    }

    def addModuleInfoToJar(File moduleInfoClass, File jar) {
        def directory = jar.parentFile

        def listCommand = new ArrayList<String>(['jar', '-uf', jar.absolutePath, "-C", directory.absolutePath, moduleInfoClass.name].toList())

        executeCommandLine(listCommand)

        moduleInfoClass.delete()
    }

    def executeCommandLine(List<String> command, File directory = project.projectDir) {
        if (command == null || command.isEmpty()) {
            throw new GradleException("Command is null or empty")
        }

        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            if (command.get(0) != 'cmd') {
                command.add(0, 'cmd')
                command.add(1, '/c')
            }
        }

        def processBuilder = new ProcessBuilder(command)
        processBuilder.directory(directory)
        def process = processBuilder.start()
        def exitCode = process.waitFor()

        def output = new StringBuilder()
        def error = new StringBuilder()

        process.inputStream.eachLine { output.append(it) }
        process.errorStream.eachLine { error.append(it) }

        if (exitCode != 0) {
            def message = "Error executing command: ${command} \n ${error}"

            if (output != null && !output.isEmpty()) {
                message = output.toString()
            }

            throw new GradleException(message)
        }
    }
}
