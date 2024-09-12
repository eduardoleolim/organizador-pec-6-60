package org.eduardoleolim.organizadorpec660.shared.window.icons

import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

private var _close: ImageVector? = null

val CustomIcons.Close: ImageVector
    get() {
        if (_close != null) {
            return _close!!
        }
        _close = ImageVector.Builder(
            name = "CustomIcons.Close",
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
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(0f, 22f)
                lineTo(22f, 0f)
                lineTo(24f, 2f)
                lineTo(2f, 24f)
                lineTo(0f, 22f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(22f, 24f)
                lineTo(0f, 2f)
                lineTo(2f, 0f)
                lineTo(24f, 22f)
                lineTo(22f, 24f)
                close()
            }
        }.build()
        return _close!!
    }
