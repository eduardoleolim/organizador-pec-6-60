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

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jetbrains.JBR
import org.eduardoleolim.window.icons.*
import java.awt.Frame
import java.awt.event.MouseEvent

@Composable
internal fun DecoratedWindowScope.TitleBarOnLinux(
    minHeight: Dp = 40.dp,
    modifier: Modifier = Modifier,
    gradientStartColor: Color = Color.Unspecified,
    onCloseRequest: () -> Unit,
    content: @Composable() (TitleBarScope.(DecoratedWindowState) -> Unit),
) {
    fun toggleMaximize() {
        if (state.isMaximized) {
            window.extendedState = Frame.NORMAL
        } else {
            window.extendedState = Frame.MAXIMIZED_BOTH
        }
    }

    TitleBarImpl(
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures(
                onDoubleTap = {
                    if (window.isResizable) {
                        toggleMaximize()
                    }
                },
                onPress = {
                    JBR.getWindowMove().startMovingTogetherWithMouse(window, MouseEvent.BUTTON1)
                }
            )
        },
        gradientStartColor = gradientStartColor,
        applyTitleBar = { _, _ -> PaddingValues(0.dp) },
        content = { state ->
            content(state)

            Row(
                modifier = Modifier.padding(horizontal = 8.dp).align(Alignment.End),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ControlButton(
                    onClick = { window.extendedState = Frame.ICONIFIED },
                    icon = CustomIcons.Minimize,
                    description = "Minimize"
                )

                if (window.isResizable) {
                    ControlButton(
                        onClick = {
                            if (window.isResizable) {
                                toggleMaximize()
                            }
                        },
                        icon = if (state.isMaximized) CustomIcons.Floating else CustomIcons.Maximize,
                        description = "Maximize"
                    )
                }

                ControlButton(
                    onClick = onCloseRequest,
                    icon = CustomIcons.Close,
                    description = "Close"
                )
            }
        },
        minHeight = minHeight

    )
}

@Composable
internal fun DecoratedDialogWindowScope.TitleBarOnLinux(
    modifier: Modifier = Modifier,
    gradientStartColor: Color = Color.Unspecified,
    onCloseRequest: () -> Unit,
    content: @Composable (TitleBarScope.(DecoratedDialogWindowState) -> Unit),
) {
    TitleBarImpl(
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    JBR.getWindowMove().startMovingTogetherWithMouse(window, MouseEvent.BUTTON1)
                }
            )
        },
        gradientStartColor = gradientStartColor,
        applyTitleBar = { _, _ -> PaddingValues(0.dp) },
        content = { state ->
            content(state)

            Row(
                modifier = Modifier.padding(horizontal = 8.dp).align(Alignment.End),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ControlButton(
                    onClick = onCloseRequest,
                    icon = CustomIcons.Close,
                    description = "Close"
                )
            }
        },
        minHeight = 30.dp
    )
}

@Composable
private fun ControlButton(
    enabled: Boolean = true,
    onClick: () -> Unit,
    icon: ImageVector,
    description: String
) {
    val background = LocalContentColor.current

    IconButton(
        enabled = enabled,
        onClick = onClick,
        modifier = Modifier.size(22.dp).focusable(false),
        colors = IconButtonDefaults.iconButtonColors().copy(containerColor = background.copy(0.1f))
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            modifier = Modifier.size(8.dp)
        )
    }
}
