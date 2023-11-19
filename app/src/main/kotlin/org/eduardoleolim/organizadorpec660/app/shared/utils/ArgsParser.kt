package org.eduardoleolim.organizadorpec660.app.shared.utils

object ArgsParser {
    fun getDatabasePath(args: Array<String>): String? {
        args.toList().forEach {
            if (it.startsWith("--database=")) {
                return it.replace("--database=", "")
            }
        }
        return null
    }
}
