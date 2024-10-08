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
