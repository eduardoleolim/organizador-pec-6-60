package org.eduardoleolim.organizadorpec660.app.utils

class ArgsUtils {
    companion object {
        fun getDatabasePath(args: Array<String>): String? {
            args.toList().forEach {
                if (it.startsWith("--database=")) {
                    return it.replace("--database=", "")
                }
            }
            return null
        }
    }
}
