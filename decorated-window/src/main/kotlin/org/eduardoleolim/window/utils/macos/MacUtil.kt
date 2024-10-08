/*
 * Copyright (C) 2024 Ángel Eduardo Martínez Leo Lim
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

package org.eduardoleolim.window.utils.macos

import org.eduardoleolim.window.utils.UnsafeAccessing
import org.eduardoleolim.window.utils.accessible
import java.awt.Component
import java.awt.Window
import java.lang.reflect.InvocationTargetException
import java.util.logging.Level
import java.util.logging.Logger
import javax.swing.SwingUtilities

internal object MacUtil {
    private val logger = Logger.getLogger(MacUtil::class.java.simpleName)

    init {
        try {
            UnsafeAccessing.assignAccessibility(
                UnsafeAccessing.desktopModule,
                listOf("sun.awt", "sun.lwawt", "sun.lwawt.macosx"),
            )
        } catch (
            @Suppress("TooGenericExceptionCaught") e: Exception,
        ) {
            logger.log(Level.WARNING, "Assign access for jdk.desktop failed.", e)
        }
    }

    fun getWindowFromJavaWindow(w: Window?): ID {
        if (w == null) {
            return ID.NIL
        }
        try {
            val cPlatformWindow = getPlatformWindow(w)
            if (cPlatformWindow != null) {
                val ptr = cPlatformWindow.javaClass.superclass.getDeclaredField("ptr")
                ptr.setAccessible(true)
                return ID(ptr.getLong(cPlatformWindow))
            }
        } catch (e: IllegalAccessException) {
            logger.log(Level.WARNING, "Fail to get cPlatformWindow from awt window.", e)
        } catch (e: NoSuchFieldException) {
            logger.log(Level.WARNING, "Fail to get cPlatformWindow from awt window.", e)
        }
        return ID.NIL
    }

    fun getPlatformWindow(w: Window): Any? {
        try {
            val awtAccessor = Class.forName("sun.awt.AWTAccessor")
            val componentAccessor = awtAccessor.getMethod("getComponentAccessor").invoke(null)
            val getPeer = componentAccessor.javaClass.getMethod("getPeer", Component::class.java).accessible()
            val peer = getPeer.invoke(componentAccessor, w)
            if (peer != null) {
                val cWindowPeerClass: Class<*> = peer.javaClass
                val getPlatformWindowMethod = cWindowPeerClass.getDeclaredMethod("getPlatformWindow")
                val cPlatformWindow = getPlatformWindowMethod.invoke(peer)
                if (cPlatformWindow != null) {
                    return cPlatformWindow
                }
            }
        } catch (e: NoSuchMethodException) {
            logger.log(Level.WARNING, "Fail to get cPlatformWindow from awt window.", e)
        } catch (e: IllegalAccessException) {
            logger.log(Level.WARNING, "Fail to get cPlatformWindow from awt window.", e)
        } catch (e: InvocationTargetException) {
            logger.log(Level.WARNING, "Fail to get cPlatformWindow from awt window.", e)
        } catch (e: ClassNotFoundException) {
            logger.log(Level.WARNING, "Fail to get cPlatformWindow from awt window.", e)
        }
        return null
    }

    fun updateColors(w: Window) {
        SwingUtilities.invokeLater {
            val window = getWindowFromJavaWindow(w)
            val delegate = Foundation.invoke(window, "delegate")
            if (Foundation.invoke(delegate, "respondsToSelector:", Foundation.createSelector("updateColors"))
                    .booleanValue()
            ) {
                Foundation.invoke(delegate, "updateColors")
            }
        }
    }

    fun updateFullScreenButtons(w: Window) {
        SwingUtilities.invokeLater {
            val selector = Foundation.createSelector("updateFullScreenButtons")
            val window = getWindowFromJavaWindow(w)
            val delegate = Foundation.invoke(window, "delegate")

            if (Foundation.invoke(delegate, "respondsToSelector:", selector).booleanValue()) {
                Foundation.invoke(delegate, "updateFullScreenButtons")
            }
        }
    }
}
