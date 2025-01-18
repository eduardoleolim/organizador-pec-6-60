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

package org.eduardoleolim.window.icons

import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

private var _maximize: ImageVector? = null

val CustomIcons.Maximize: ImageVector
    get() {
        if (_maximize != null) {
            return _maximize!!
        }
        _maximize = ImageVector.Builder(
            name = "CustomIcons.Maximize",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFFFFFFF)),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.EvenOdd
            ) {
                moveTo(21.8182f, 2.18182f)
                horizontalLineTo(2.18182f)
                verticalLineTo(21.8182f)
                horizontalLineTo(21.8182f)
                verticalLineTo(2.18182f)
                close()
                moveTo(0f, 0f)
                verticalLineTo(24f)
                horizontalLineTo(24f)
                verticalLineTo(0f)
                horizontalLineTo(0f)
                close()
            }
        }.build()
        return _maximize!!
    }
