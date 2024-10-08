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

package org.eduardoleolim.window

import androidx.compose.runtime.*
import com.jthemedetecor.OsThemeDetector
import java.util.function.Consumer
import javax.swing.SwingUtilities

@Composable
fun isSystemInDarkTheme(): Boolean {
    var isDarkTheme by remember { mutableStateOf(true) }

    DisposableEffect(Unit) {
        val osThemeDetector = OsThemeDetector.getDetector()
        isDarkTheme = osThemeDetector.isDark

        val consumer = Consumer<Boolean> {
            SwingUtilities.invokeLater {
                isDarkTheme = it
            }
        }

        osThemeDetector.registerListener(consumer)
        onDispose {
            osThemeDetector.removeListener(consumer)
        }
    }

    return isDarkTheme
}
