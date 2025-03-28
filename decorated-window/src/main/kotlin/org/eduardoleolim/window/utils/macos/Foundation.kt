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

package org.eduardoleolim.window.utils.macos

import com.sun.jna.Function
import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.Pointer
import org.eduardoleolim.window.utils.JnaLoader
import java.lang.reflect.Proxy
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

internal object Foundation {
    private val logger = Logger.getLogger(Foundation::class.java.simpleName)

    init {
        if (!JnaLoader.isLoaded) {
            logger.log(Level.WARNING, "JNA is not loaded")
        }
    }

    private val myFoundationLibrary: FoundationLibrary? by lazy {
        try {
            Native.load(
                "Foundation",
                FoundationLibrary::class.java,
                Collections.singletonMap("jna.encoding", "UTF8"),
            )
        } catch (_: Throwable) {
            null
        }
    }

    private val myObjcMsgSend: Function? by lazy {
        try {
            (Proxy.getInvocationHandler(myFoundationLibrary) as Library.Handler).nativeLibrary.getFunction("objc_msgSend")
        } catch (_: Throwable) {
            null
        }
    }

    /**
     * Get the ID of the NSClass with className
     */
    fun getObjcClass(className: String?): ID? = myFoundationLibrary?.objc_getClass(className)

    fun getProtocol(name: String?): ID? = myFoundationLibrary?.objc_getProtocol(name)

    fun createSelector(s: String?): Pointer? = myFoundationLibrary?.sel_registerName(s)

    private fun prepInvoke(
        id: ID?,
        selector: Pointer?,
        args: Array<out Any?>,
    ): Array<Any?> {
        val invokArgs = arrayOfNulls<Any>(args.size + 2)
        invokArgs[0] = id
        invokArgs[1] = selector
        System.arraycopy(args, 0, invokArgs, 2, args.size)
        return invokArgs
    }

    // objc_msgSend is called with the calling convention of the target method
    // on x86_64 this does not make a difference, but arm64 uses a different calling convention for varargs
    // it is therefore important to not call objc_msgSend as a vararg function
    operator fun invoke(
        id: ID?,
        selector: Pointer?,
        vararg args: Any?,
    ): ID = ID(myObjcMsgSend?.invokeLong(prepInvoke(id, selector, args)) ?: 0)

    /**
     * Invokes the given vararg selector.
     * Expects `NSArray arrayWithObjects:(id), ...` like signature, i.e. exactly one fixed argument, followed by varargs.
     */
    fun invokeVarArg(
        id: ID?,
        selector: Pointer?,
        vararg args: Any?,
    ): ID {
        // c functions and objc methods have at least 1 fixed argument, we therefore need to separate out the first argument
        return myFoundationLibrary?.objc_msgSend(
            id,
            selector,
            args[0],
            *Arrays.copyOfRange(args, 1, args.size),
        ) ?: ID.NIL
    }

    operator fun invoke(
        cls: String?,
        selector: String?,
        vararg args: Any?,
    ): ID = invoke(getObjcClass(cls), createSelector(selector), *args)

    fun invokeVarArg(
        cls: String?,
        selector: String?,
        vararg args: Any?,
    ): ID = invokeVarArg(getObjcClass(cls), createSelector(selector), *args)

    fun safeInvoke(
        stringCls: String?,
        stringSelector: String?,
        vararg args: Any?,
    ): ID {
        val cls = getObjcClass(stringCls)
        val selector = createSelector(stringSelector)
        if (!invoke(cls, "respondsToSelector:", selector).booleanValue()) {
            error("Missing selector $stringSelector for $stringCls")
        }
        return invoke(cls, selector, *args)
    }

    operator fun invoke(
        id: ID?,
        selector: String?,
        vararg args: Any?,
    ): ID = invoke(id, createSelector(selector), *args)
}
