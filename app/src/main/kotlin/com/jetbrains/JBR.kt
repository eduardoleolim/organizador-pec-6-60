/*
 * Copyright 2000-2023 JetBrains s.r.o.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package com.jetbrains

import java.lang.invoke.MethodHandles
import java.lang.reflect.InvocationTargetException

/**
 * This class is an entry point into JBR API.
 * JBR API is a collection of services, classes, interfaces, etc.,
 * which require tight interaction with JRE and therefore are implemented inside JBR.
 * <div>JBR API consists of two parts:</div>
 *
 *  * Client side - `jetbrains.api` module, mostly containing interfaces
 *  * JBR side - actual implementation code inside JBR
 *
 * Client and JBR side are linked dynamically at runtime and do not have to be of the same version.
 * In some cases (e.g. running on different JRE or old JBR) system will not be able to find
 * implementation for some services, so you'll need a fallback behavior for that case.
 * <h2>Simple usage example:</h2>
 * <blockquote><pre>`if (JBR.isSomeServiceSupported()) {
 * JBR.getSomeService().doSomething();
 * } else {
 * planB();
 * }
`</pre></blockquote> *
 *
 * @implNote JBR API is initialized on first access to this class (in static initializer).
 * Actual implementation is linked on demand, when corresponding service is requested by client.
 */
object JBR {
    private val api: ServiceApi?
    private val bootstrapException: Exception?

    init {
        var a: ServiceApi? = null
        var exception: Exception? = null
        try {
            a = Class.forName("com.jetbrains.bootstrap.JBRApiBootstrap")
                .getMethod("bootstrap", MethodHandles.Lookup::class.java)
                .invoke(null, MethodHandles.lookup()) as ServiceApi
        } catch (e: InvocationTargetException) {
            val t = e.cause

            if (t is Error) {
                throw t
            } else {
                throw Error(t)
            }
        } catch (e: IllegalAccessException) {
            exception = e
        } catch (e: NoSuchMethodException) {
            exception = e
        } catch (e: ClassNotFoundException) {
            exception = e
        }
        api = a
        bootstrapException = exception
    }

    private fun <T> getService(interFace: Class<T>, fallback: FallbackSupplier<T>?): T? {
        val service = getService(interFace)
        return try {
            service ?: fallback?.get()
        } catch (ignore: Throwable) {
            null
        }
    }

    fun <T> getService(interFace: Class<T>?): T? {
        return api?.getService(interFace)
    }

    /**
     * @return true when running on JBR which implements JBR API
     */
    val isAvailable: Boolean
        get() = api != null

    /**
     * @return JBR API version in form `JBR.MAJOR.MINOR.PATCH`
     * @implNote This is an API version, which comes with client application,
     * it has nothing to do with JRE it runs on.
     */
    val apiVersion: String
        get() = "17.0.8.1b1070.2.1.9.0"

    /**
     * @return true if current runtime has implementation for all methods in [DesktopActions]
     * and its dependencies (can fully implement given service).
     * @see [desktopActions]
     */
    val isDesktopActionsSupported: Boolean
        get() = DesktopActionsHolder.INSTANCE != null

    /**
     * @return full implementation of [DesktopActions] service if any, or `null` otherwise
     */
    val desktopActions: DesktopActions?
        get() = DesktopActionsHolder.INSTANCE

    /**
     * @return true if current runtime has implementation for all methods in [RoundedCornersManager]
     * and its dependencies (can fully implement given service).
     * @see [roundedCornersManager]
     */
    val isRoundedCornersManagerSupported: Boolean
        get() = RoundedCornersManagerHolder.INSTANCE != null

    /**
     * This manager allows decorate awt Window with rounded corners.
     * Appearance depends from operating system.
     *
     * @return full implementation of [RoundedCornersManager] service if any, or `null` otherwise
     */
    val roundedCornersManager: RoundedCornersManager?
        get() = RoundedCornersManagerHolder.INSTANCE

    /**
     * @return true if current runtime has implementation for all methods in [WindowDecorations]
     * and its dependencies (can fully implement given service).
     * @see [windowDecorations]
     */
    val isWindowDecorationsSupported: Boolean
        get() = WindowDecorationsHolder.INSTANCE != null

    /**
     * Window decorations consist of title bar, window controls and border.
     *
     * @return full implementation of [WindowDecorations] service if any, or `null` otherwise
     * @see [WindowDecorations.CustomTitleBar]
     */
    val windowDecorations: WindowDecorations?
        get() = WindowDecorationsHolder.INSTANCE

    /**
     * @return true if current runtime has implementation for all methods in [WindowMove]
     * and its dependencies (can fully implement given service).
     * @see [windowMove]
     */
    val isWindowMoveSupported: Boolean
        get() = WindowMoveHolder.INSTANCE != null

    /**
     * @return full implementation of [WindowMove] service if any, or `null` otherwise
     */
    val windowMove: WindowMove?
        get() = WindowMoveHolder.INSTANCE

    /**
     * Internal API interface, contains most basic methods for communication between client and JBR.
     */
    private interface ServiceApi {
        fun <T> getService(interFace: Class<T>?): T
    }

    private fun interface FallbackSupplier<T> {
        @Throws(Throwable::class)
        fun get(): T
    }

    // ========================== Generated metadata ==========================
    /**
     * Generated client-side metadata, needed by JBR when linking the implementation.
     */
    private object Metadata {
        private val KNOWN_SERVICES = arrayOf(
            "com.jetbrains.ExtendedGlyphCache",
            "com.jetbrains.DesktopActions",
            "com.jetbrains.CustomWindowDecoration",
            "com.jetbrains.ProjectorUtils",
            "com.jetbrains.FontExtensions",
            "com.jetbrains.RoundedCornersManager",
            "com.jetbrains.GraphicsUtils",
            "com.jetbrains.WindowDecorations",
            "com.jetbrains.JBRFileDialogService",
            "com.jetbrains.AccessibleAnnouncer",
            "com.jetbrains.JBR\$ServiceApi",
            "com.jetbrains.Jstack",
            "com.jetbrains.WindowMove"
        )
        private val KNOWN_PROXIES =
            arrayOf("com.jetbrains.JBRFileDialog", "com.jetbrains.WindowDecorations\$CustomTitleBar")
    }

    // ======================= Generated static methods =======================
    private object DesktopActionsHolder {
        val INSTANCE: DesktopActions? = getService(DesktopActions::class.java, null)
    }

    private object RoundedCornersManagerHolder {
        val INSTANCE: RoundedCornersManager? = getService(RoundedCornersManager::class.java, null)
    }

    private object WindowDecorationsHolder {
        val INSTANCE: WindowDecorations? = getService(WindowDecorations::class.java, null)
    }

    private object WindowMoveHolder {
        val INSTANCE: WindowMove? = getService(WindowMove::class.java, null)
    }
}
