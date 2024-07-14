package org.eduardoleolim.organizadorpec660.app.window

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jetbrains.JBR
import com.jetbrains.WindowDecorations.CustomTitleBar
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive

@Composable
internal fun DecoratedWindowScope.TitleBarOnWindows(
    defaultHeight: Dp = 40.dp,
    modifier: Modifier = Modifier,
    gradientStartColor: Color = Color.Unspecified,
    content: @Composable TitleBarScope.(DecoratedWindowState) -> Unit,
) {
    val titleBar = remember { JBR.windowDecorations!!.createCustomTitleBar()!! }
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5
    val foreground = LocalContentColor.current
    val foregroundColor = java.awt.Color(foreground.red, foreground.green, foreground.blue, 0.7f)
    val foregroundColorFocused = java.awt.Color(foreground.red, foreground.green, foreground.blue, 1f)
    val foregroundColorInactive = java.awt.Color(foreground.red, foreground.green, foreground.blue, 0.5f)

    TitleBarImpl(
        defaultHeight = defaultHeight,
        modifier = modifier.customTitleBarMouseEventHandler(titleBar),
        gradientStartColor = gradientStartColor,
        applyTitleBar = { height, _ ->
            titleBar.also {
                it.height = height.value
                it.putProperty("controls.dark", isDark)
                it.putProperty("controls.foreground.normal", foregroundColor)
                it.putProperty("controls.foreground.hovered", foregroundColorFocused)
                it.putProperty("controls.foreground.pressed", foregroundColorFocused)
                it.putProperty("controls.foreground.disabled", foregroundColorInactive)
                it.putProperty("controls.foreground.inactive", foregroundColorInactive)
            }

            JBR.windowDecorations!!.setCustomTitleBar(window, titleBar)
            PaddingValues(start = titleBar.leftInset.dp, end = titleBar.rightInset.dp)
        },
        content = content,
    )
}

internal fun Modifier.customTitleBarMouseEventHandler(titleBar: CustomTitleBar): Modifier =
    pointerInput(Unit) {
        val currentContext = currentCoroutineContext()
        awaitPointerEventScope {
            var inUserControl = false
            while (currentContext.isActive) {
                val event = awaitPointerEvent(PointerEventPass.Main)
                event.changes.forEach {
                    if (it.isConsumed.not() && inUserControl.not()) {
                        titleBar.forceHitTest(false)
                    } else {
                        if (event.type == PointerEventType.Press) {
                            inUserControl = true
                        }

                        if (inUserControl && event.type != PointerEventType.Press && it.isConsumed) {
                            inUserControl = false
                        }

                        titleBar.forceHitTest(true)
                    }
                }
            }
        }
    }
