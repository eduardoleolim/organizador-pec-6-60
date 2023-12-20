package org.eduardoleolim.organizadorpec660.app.shared.utils

object ArgsParser {
    fun getDatabasePath(args: Array<String>): String? {
        return args.firstOrNull { it.startsWith("--database=") }?.replace("--database=", "")
    }
}
