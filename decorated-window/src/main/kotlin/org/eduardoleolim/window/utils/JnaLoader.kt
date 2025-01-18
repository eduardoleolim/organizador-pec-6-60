/*
 * Copyright (C) 2025 Ángel Eduardo Martínez Leo Lim
 * This file is part of organizador-pec-6-60.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eduardoleolim.window.utils

import com.sun.jna.Native
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.system.measureTimeMillis

internal object JnaLoader {
    private var loaded: Boolean? = null
    private val logger = Logger.getLogger(JnaLoader::class.java.simpleName)

    @Synchronized
    fun load() {
        if (loaded == null) {
            loaded = false
            try {
                val time = measureTimeMillis { Native.POINTER_SIZE }
                logger.info("JNA library (${Native.POINTER_SIZE shl 3}-bit) loaded in $time ms")
                loaded = true
            } catch (@Suppress("TooGenericExceptionCaught") t: Throwable) {
                logger.log(
                    Level.WARNING,
                    "Unable to load JNA library(os=${System.getProperty("os.name")} ${System.getProperty("os.version")}, " +
                        "jna.boot.library.path=${System.getProperty("jna.boot.library.path")})",
                    t,
                )
            }
        }
    }

    @get:Synchronized
    val isLoaded: Boolean
        get() {
            if (loaded == null) {
                load()
            }
            return loaded ?: false
        }
}
