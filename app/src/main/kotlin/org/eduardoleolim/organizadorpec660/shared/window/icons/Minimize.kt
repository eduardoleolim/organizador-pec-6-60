package org.eduardoleolim.organizadorpec660.shared.window.icons

import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

private var _minimize: ImageVector? = null

val CustomIcons.Minimize: ImageVector
    get() {
        if (_minimize != null) {
            return _minimize!!
        }
        _minimize = ImageVector.Builder(
            name = "CustomIcons.Minimize",
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
                moveTo(0f, 10.9091f)
                horizontalLineTo(24f)
                verticalLineTo(13.0909f)
                horizontalLineTo(0f)
                verticalLineTo(10.9091f)
                close()
            }
        }.build()
        return _minimize!!
    }
