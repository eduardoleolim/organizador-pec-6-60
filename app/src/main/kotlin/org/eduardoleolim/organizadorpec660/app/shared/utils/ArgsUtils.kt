package org.eduardoleolim.organizadorpec660.app.shared.utils

object ArgsUtils {
    fun databasePath(args: Array<String>): String? {
        return args.firstOrNull { it.startsWith("--database=") }?.replace("--database=", "")
    }

    fun instrumentsPath(args: Array<String>): String? {
        return args.firstOrNull { it.startsWith("--instruments=") }?.replace("--instruments=", "")
    }
}
