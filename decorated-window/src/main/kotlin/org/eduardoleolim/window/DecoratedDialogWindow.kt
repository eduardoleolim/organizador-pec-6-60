package org.eduardoleolim.window

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeDialog
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.layout.*
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import androidx.compose.ui.window.*
import com.jetbrains.JBR
import org.eduardoleolim.window.modifier.trackWindowActivation
import java.awt.event.ComponentEvent
import java.awt.event.ComponentListener
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DecoratedDialogWindow(
    onCloseRequest: () -> Unit,
    state: DialogState = rememberDialogState(),
    visible: Boolean = true,
    title: String = "Untitled",
    icon: Painter? = null,
    resizable: Boolean = true,
    enabled: Boolean = true,
    focusable: Boolean = true,
    alwaysOnTop: Boolean = false,
    onPreviewKeyEvent: (KeyEvent) -> Boolean = { false },
    onKeyEvent: (KeyEvent) -> Boolean = { false },
    content: @Composable (DecoratedDialogWindowScope.() -> Unit)
) {
    remember {
        if (JBR.isAvailable().not()) {
            error("DecoratedDialogWindow can only be used on JetBrainsRuntime(JBR) platform")
        }
    }

    val isRunningInLinux = DesktopPlatform.Linux == DesktopPlatform.Current

    DialogWindow(
        onCloseRequest = onCloseRequest,
        state = state,
        visible = visible,
        title = title,
        icon = icon,
        decoration = if (isRunningInLinux) WindowDecoration.Undecorated() else WindowDecoration.SystemDefault,
        transparent = false,
        resizable = resizable,
        enabled = enabled,
        focusable = focusable,
        alwaysOnTop = alwaysOnTop,
        onPreviewKeyEvent = onPreviewKeyEvent,
        onKeyEvent = onKeyEvent
    ) {
        var decoratedDialogWindowState by remember { mutableStateOf(DecoratedDialogWindowState.of(window)) }

        DisposableEffect(window) {
            val adapter = object : WindowAdapter(), ComponentListener {
                override fun windowActivated(e: WindowEvent?) {
                    decoratedDialogWindowState = DecoratedDialogWindowState.of(window)
                }

                override fun windowDeactivated(e: WindowEvent?) {
                    decoratedDialogWindowState = DecoratedDialogWindowState.of(window)
                }

                override fun windowIconified(e: WindowEvent?) {
                    decoratedDialogWindowState = DecoratedDialogWindowState.of(window)
                }

                override fun windowDeiconified(e: WindowEvent?) {
                    decoratedDialogWindowState = DecoratedDialogWindowState.of(window)
                }

                override fun windowStateChanged(e: WindowEvent) {
                    decoratedDialogWindowState = DecoratedDialogWindowState.of(window)
                }

                override fun componentResized(e: ComponentEvent?) {
                    decoratedDialogWindowState = DecoratedDialogWindowState.of(window)
                }

                override fun componentMoved(e: ComponentEvent?) {
                    // Empty
                }

                override fun componentShown(e: ComponentEvent?) {
                    // Empty
                }

                override fun componentHidden(e: ComponentEvent?) {
                    // Empty
                }
            }

            window.addWindowListener(adapter)
            window.addWindowStateListener(adapter)
            window.addComponentListener(adapter)

            onDispose {
                window.removeWindowListener(adapter)
                window.removeWindowStateListener(adapter)
                window.removeComponentListener(adapter)
            }
        }

        CompositionLocalProvider(
            LocalTitleBarInfo provides TitleBarInfo(title, icon),
            LocalDialogWindow provides window
        ) {
            Layout(
                content = {
                    val scope = object : DecoratedDialogWindowScope {
                        override val state: DecoratedDialogWindowState
                            get() = decoratedDialogWindowState

                        override val window: ComposeDialog
                            get() = this@DialogWindow.window
                    }
                    scope.content()
                },
                modifier = Modifier
                    .border(1.dp, MaterialTheme.colorScheme.primaryContainer, RectangleShape)
                    .padding(1.dp)
                    .trackWindowActivation(window),
                measurePolicy = DecoratedDialogWindowMeasurePolicy,
            )
        }
    }
}

interface DecoratedDialogWindowScope : DialogWindowScope {
    override val window: ComposeDialog

    val state: DecoratedDialogWindowState
}

private object DecoratedDialogWindowMeasurePolicy : MeasurePolicy {
    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints,
    ): MeasureResult {
        if (measurables.isEmpty()) {
            return layout(width = constraints.minWidth, height = constraints.minHeight) {}
        }

        val titleBars = measurables.filter { it.layoutId == TITLE_BAR_LAYOUT_ID }
        if (titleBars.size > 1) {
            error("Window just can have only one title bar")
        }
        val titleBar = titleBars.firstOrNull()
        val titleBarBorder = measurables.firstOrNull { it.layoutId == TITLE_BAR_BORDER_LAYOUT_ID }

        val contentConstraints = constraints.copy(minWidth = 0, minHeight = 0)

        val titleBarPlaceable = titleBar?.measure(contentConstraints)
        val titleBarHeight = titleBarPlaceable?.height ?: 0

        val titleBarBorderPlaceable = titleBarBorder?.measure(contentConstraints)
        val titleBarBorderHeight = titleBarBorderPlaceable?.height ?: 0

        val measuredPlaceable = mutableListOf<Placeable>()

        for (it in measurables) {
            if (it.layoutId.toString().startsWith(TITLE_BAR_COMPONENT_LAYOUT_ID_PREFIX)) continue
            val offsetConstraints = contentConstraints.offset(vertical = -titleBarHeight - titleBarBorderHeight)
            val placeable = it.measure(offsetConstraints)
            measuredPlaceable += placeable
        }

        return layout(constraints.maxWidth, constraints.maxHeight) {
            titleBarPlaceable?.placeRelative(0, 0)
            titleBarBorderPlaceable?.placeRelative(0, titleBarHeight)

            measuredPlaceable.forEach { it.placeRelative(0, titleBarHeight + titleBarBorderHeight) }
        }
    }
}


@Immutable
@JvmInline
value class DecoratedDialogWindowState(val state: ULong) {
    val isActive: Boolean
        get() = state and Active != 0UL

    fun copy(active: Boolean = isActive): DecoratedDialogWindowState = of(active)

    override fun toString(): String = "${javaClass.simpleName}(isActive=$isActive)"

    companion object {
        val Active: ULong = 1UL shl 0

        fun of(active: Boolean = true): DecoratedDialogWindowState =
            DecoratedDialogWindowState(if (active) Active else 0UL)

        fun of(window: ComposeDialog): DecoratedDialogWindowState = of(window.isActive)
    }
}

internal val LocalDialogWindow = compositionLocalOf<ComposeDialog> { error("dialog window not provided") }
