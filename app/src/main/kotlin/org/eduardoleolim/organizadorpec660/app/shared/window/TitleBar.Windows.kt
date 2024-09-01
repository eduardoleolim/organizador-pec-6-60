package org.eduardoleolim.organizadorpec660.app.shared.window

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
import org.eduardoleolim.organizadorpec660.app.shared.utils.toAwt

@Composable
internal fun DecoratedWindowScope.TitleBarOnWindows(
    minHeight: Dp = 40.dp,
    modifier: Modifier = Modifier,
    gradientStartColor: Color = Color.Unspecified,
    content: @Composable TitleBarScope.(DecoratedWindowState) -> Unit,
) {
    val titleBar = remember { JBR.windowDecorations!!.createCustomTitleBar()!! }
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5
    val foreground = LocalContentColor.current

    TitleBarImpl(
        minHeight = minHeight,
        modifier = modifier.customTitleBarMouseEventHandler(titleBar),
        gradientStartColor = gradientStartColor,
        applyTitleBar = { newHeight, _ ->
            titleBar.apply {
                height = newHeight.value
                putProperty("controls.dark", isDark)
                putProperty("controls.foreground.normal", foreground.toAwt(alpha = 0.7f))
                putProperty("controls.foreground.hovered", foreground.toAwt(alpha = 1f))
                putProperty("controls.foreground.pressed", foreground.toAwt(alpha = 1f))
                putProperty("controls.foreground.disabled", foreground.toAwt(alpha = 0.5f))
                putProperty("controls.foreground.inactive", foreground.toAwt(alpha = 0.5f))
            }

            JBR.windowDecorations!!.setCustomTitleBar(window, titleBar)
            PaddingValues(start = titleBar.leftInset.dp, end = titleBar.rightInset.dp)
        },
        content = content,
    )
}

@Composable
internal fun DecoratedDialogWindowScope.TitleBarOnWindows(
    modifier: Modifier = Modifier,
    gradientStartColor: Color = Color.Unspecified,
    content: @Composable TitleBarScope.(DecoratedDialogWindowState) -> Unit,
) {
    val titleBar = remember { JBR.windowDecorations!!.createCustomTitleBar()!! }
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5
    val foreground = LocalContentColor.current

    TitleBarImpl(
        minHeight = 30.dp,
        modifier = modifier.customTitleBarMouseEventHandler(titleBar),
        gradientStartColor = gradientStartColor,
        applyTitleBar = { newHeight, _ ->
            titleBar.apply {
                height = newHeight.value
                putProperty("controls.dark", isDark)
                putProperty("controls.foreground.normal", foreground.toAwt(alpha = 0.7f))
                putProperty("controls.foreground.hovered", foreground.toAwt(alpha = 1f))
                putProperty("controls.foreground.pressed", foreground.toAwt(alpha = 1f))
                putProperty("controls.foreground.disabled", foreground.toAwt(alpha = 0.5f))
                putProperty("controls.foreground.inactive", foreground.toAwt(alpha = 0.5f))
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
