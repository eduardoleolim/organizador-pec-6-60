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

package org.eduardoleolim.window.icons

import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

private var _floating: ImageVector? = null

val CustomIcons.Floating: ImageVector
    get() {
        if (_floating != null) {
            return _floating!!
        }
        _floating = ImageVector.Builder(
            name = "CustomIcons.Floating",
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
                moveTo(6.54545f, 0f)
                verticalLineTo(6.54545f)
                horizontalLineTo(0f)
                verticalLineTo(24f)
                horizontalLineTo(17.4545f)
                verticalLineTo(17.4545f)
                horizontalLineTo(24f)
                verticalLineTo(0f)
                horizontalLineTo(6.54545f)
                close()
                moveTo(21.8182f, 2.18182f)
                horizontalLineTo(8.72727f)
                verticalLineTo(6.54545f)
                horizontalLineTo(17.4545f)
                verticalLineTo(15.2727f)
                horizontalLineTo(21.8182f)
                verticalLineTo(2.18182f)
                close()
                moveTo(15.2727f, 15.2727f)
                verticalLineTo(21.8182f)
                horizontalLineTo(2.18182f)
                verticalLineTo(8.72727f)
                horizontalLineTo(8.72727f)
                horizontalLineTo(15.2727f)
                verticalLineTo(15.2727f)
                close()
            }
        }.build()
        return _floating!!
    }
