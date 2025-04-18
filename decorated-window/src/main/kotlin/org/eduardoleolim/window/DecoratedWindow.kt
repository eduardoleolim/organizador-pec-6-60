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

package org.eduardoleolim.window

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
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
fun DecoratedWindow(
    onCloseRequest: () -> Unit,
    state: WindowState = rememberWindowState(),
    visible: Boolean = true,
    title: String = "Untitled",
    icon: Painter? = null,
    resizable: Boolean = true,
    enabled: Boolean = true,
    focusable: Boolean = true,
    alwaysOnTop: Boolean = false,
    onPreviewKeyEvent: (KeyEvent) -> Boolean = { false },
    onKeyEvent: (KeyEvent) -> Boolean = { false },
    content: @Composable DecoratedWindowScope.() -> Unit,
) {
    remember {
        if (JBR.isAvailable().not()) {
            error("DecoratedWindow can only be used on JetBrainsRuntime(JBR) platform")
        }
    }

    val isRunningInLinux = DesktopPlatform.Linux == DesktopPlatform.Current

    Window(
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
        var decoratedWindowState by remember { mutableStateOf(DecoratedWindowState.of(window)) }

        DisposableEffect(window) {
            val adapter = object : WindowAdapter(), ComponentListener {
                override fun windowActivated(e: WindowEvent?) {
                    decoratedWindowState = DecoratedWindowState.of(window)
                }

                override fun windowDeactivated(e: WindowEvent?) {
                    decoratedWindowState = DecoratedWindowState.of(window)
                }

                override fun windowIconified(e: WindowEvent?) {
                    decoratedWindowState = DecoratedWindowState.of(window)
                }

                override fun windowDeiconified(e: WindowEvent?) {
                    decoratedWindowState = DecoratedWindowState.of(window)
                }

                override fun windowStateChanged(e: WindowEvent) {
                    decoratedWindowState = DecoratedWindowState.of(window)
                }

                override fun componentResized(e: ComponentEvent?) {
                    decoratedWindowState = DecoratedWindowState.of(window)
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

        val undecoratedWindowBorder =
            if (isRunningInLinux && !decoratedWindowState.isMaximized) {
                Modifier.border(1.dp, MaterialTheme.colorScheme.primaryContainer, RectangleShape)
                    .padding(1.dp)
            } else {
                Modifier
            }

        CompositionLocalProvider(
            LocalTitleBarInfo provides TitleBarInfo(title, icon),
            LocalWindow provides window
        ) {
            Layout(
                content = {
                    val scope = object : DecoratedWindowScope {
                        override val state: DecoratedWindowState
                            get() = decoratedWindowState

                        override val window: ComposeWindow
                            get() = this@Window.window
                    }
                    scope.content()
                },
                modifier = undecoratedWindowBorder.trackWindowActivation(window),
                measurePolicy = DecoratedWindowMeasurePolicy,
            )
        }
    }
}

@Stable
interface DecoratedWindowScope : FrameWindowScope {
    override val window: ComposeWindow

    val state: DecoratedWindowState
}

private object DecoratedWindowMeasurePolicy : MeasurePolicy {
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
value class DecoratedWindowState(val state: ULong) {
    val isActive: Boolean
        get() = state and Active != 0UL

    val isFullscreen: Boolean
        get() = state and Fullscreen != 0UL

    val isMinimized: Boolean
        get() = state and Minimize != 0UL

    val isMaximized: Boolean
        get() = state and Maximize != 0UL

    fun copy(
        fullscreen: Boolean = isFullscreen,
        minimized: Boolean = isMinimized,
        maximized: Boolean = isMaximized,
        active: Boolean = isActive,
    ): DecoratedWindowState =
        of(
            fullscreen = fullscreen,
            minimized = minimized,
            maximized = maximized,
            active = active,
        )

    override fun toString(): String = "${javaClass.simpleName}(isFullscreen=$isFullscreen, isActive=$isActive)"

    companion object {
        val Active: ULong = 1UL shl 0
        val Fullscreen: ULong = 1UL shl 1
        val Minimize: ULong = 1UL shl 2
        val Maximize: ULong = 1UL shl 3

        fun of(
            fullscreen: Boolean = false,
            minimized: Boolean = false,
            maximized: Boolean = false,
            active: Boolean = true,
        ): DecoratedWindowState =
            DecoratedWindowState(
                (if (fullscreen) Fullscreen else 0UL) or
                    (if (minimized) Minimize else 0UL) or
                    (if (maximized) Maximize else 0UL) or
                    (if (active) Active else 0UL),
            )

        fun of(window: ComposeWindow): DecoratedWindowState =
            of(
                fullscreen = window.placement == WindowPlacement.Fullscreen,
                minimized = window.isMinimized,
                maximized = window.placement == WindowPlacement.Maximized,
                active = window.isActive,
            )
    }
}

data class TitleBarInfo(val title: String, val icon: Painter?)

val LocalTitleBarInfo = compositionLocalOf<TitleBarInfo> {
    error("LocalTitleBarInfo not provided, TitleBar must be used in DecoratedWindow")
}

val LocalWindow = compositionLocalOf<ComposeWindow> { error("window not provided") }
