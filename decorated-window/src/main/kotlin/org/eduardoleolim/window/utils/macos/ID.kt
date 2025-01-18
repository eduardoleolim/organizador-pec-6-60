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

import com.sun.jna.NativeLong

/**
 * Could be an address in memory (if pointer to a class or method) or a
 * value (like 0 or 1)
 */
@Suppress("OVERRIDE_DEPRECATION") // Copied code
internal class ID : NativeLong {
    constructor()
    constructor(peer: Long) : super(peer)

    fun booleanValue(): Boolean = toInt() != 0

    override fun toByte(): Byte = toInt().toByte()

    override fun toChar(): Char = toInt().toChar()

    override fun toShort(): Short = toInt().toShort()

    @Suppress("RedundantOverride") // Without this, we get a SOE
    override fun toInt(): Int = super.toInt()

    companion object {
        @JvmField
        val NIL = ID(0L)
    }
}
