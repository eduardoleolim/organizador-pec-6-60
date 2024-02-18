package org.eduardoleolim.organizadorPec660.app.shared.utils

object ArgsUtils {
    fun databasePath(args: Array<String>): String? {
        return args.firstOrNull { it.startsWith("--database=") }?.replace("--database=", "")
    }
}
