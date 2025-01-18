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

import sun.misc.Unsafe
import java.lang.reflect.AccessibleObject

internal object UnsafeAccessing {
    private val unsafe: Any? by lazy {
        try {
            val theUnsafe = Unsafe::class.java.getDeclaredField("theUnsafe")
            theUnsafe.isAccessible = true
            theUnsafe.get(null) as Unsafe
        } catch (e: Throwable) {
            null
        }
    }

    val desktopModule by lazy {
        ModuleLayer.boot().findModule("java.desktop").get()
    }

    val ownerModule by lazy {
        this.javaClass.module
    }

    private val isAccessibleFieldOffset: Long? by lazy {
        try {
            (unsafe as? Unsafe)?.objectFieldOffset(Parent::class.java.getDeclaredField("first"))
        } catch (e: Throwable) {
            null
        }
    }

    private val implAddOpens by lazy {
        try {
            Module::class.java.getDeclaredMethod(
                "implAddOpens", String::class.java, Module::class.java
            ).accessible()
        } catch (e: Throwable) {
            null
        }
    }

    fun assignAccessibility(obj: AccessibleObject) {
        try {
            val theUnsafe = unsafe as? Unsafe ?: return
            val offset = isAccessibleFieldOffset ?: return
            theUnsafe.putBooleanVolatile(obj, offset, true)
        } catch (e: Throwable) {
            // ignore
        }
    }

    fun assignAccessibility(module: Module, packages: List<String>) {
        try {
            packages.forEach {
                implAddOpens?.invoke(module, it, ownerModule)
            }
        } catch (e: Throwable) {
            // ignore
        }
    }

    private class Parent {
        var first = false

        @Volatile
        var second: Any? = null
    }
}

internal fun <T : AccessibleObject> T.accessible(): T {
    return apply {
        UnsafeAccessing.assignAccessibility(this)
    }
}
