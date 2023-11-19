package org.eduardoleolim.organizadorpec660.app.main.window

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Maximize
import androidx.compose.material.icons.filled.Minimize
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowScope

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun WindowScope.TitleBar(
    composeWindow: ComposeWindow,
    onCloseRequest: () -> Unit
) {
    var placement by remember { mutableStateOf(composeWindow.placement) }
    var isCloseButtonHovered by remember { mutableStateOf(false) }

    if (placement == WindowPlacement.Fullscreen)
        return

    val onMaximizeRestoreRequest = {
        if (composeWindow.isResizable) {
            placement = if (placement == WindowPlacement.Maximized) {
                WindowPlacement.Floating
            } else {
                WindowPlacement.Maximized
            }
            composeWindow.placement = placement
        }
    }

    val onMinimizeRequest = {
        composeWindow.isMinimized = true
    }

    WindowDraggableArea(
        modifier = Modifier.fillMaxWidth()
            .height(40.dp)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { onMaximizeRestoreRequest() }
                )
            }
            .onDrag {
                if (placement == WindowPlacement.Maximized) {
                    placement = WindowPlacement.Floating
                    composeWindow.placement = placement
                }
            }
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = composeWindow.title,
                modifier = Modifier.padding(start = 10.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Row {
                val buttonModifier = Modifier.size(50.dp, 40.dp).padding(0.dp)
                val iconModifier = Modifier.size(20.dp)
                val maximizedIcon = if (placement == WindowPlacement.Maximized) {
                    Icons.Default.Restore
                } else {
                    Icons.Default.Maximize
                }
                val buttonColors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
                val closeButtonColors = if (isCloseButtonHovered) {
                    IconButtonDefaults.filledIconButtonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    )
                } else {
                    buttonColors
                }

                FilledIconButton(
                    onClick = onMinimizeRequest,
                    modifier = buttonModifier,
                    shape = RectangleShape,
                    colors = buttonColors
                ) {
                    Icon(
                        imageVector = Icons.Default.Minimize,
                        modifier = iconModifier,
                        contentDescription = "Minimize"
                    )
                }

                if (composeWindow.isResizable || placement != WindowPlacement.Floating) {
                    FilledIconButton(
                        onClick = onMaximizeRestoreRequest,
                        modifier = buttonModifier,
                        shape = RectangleShape,
                        colors = buttonColors
                    ) {
                        Icon(
                            imageVector = maximizedIcon,
                            modifier = iconModifier,
                            contentDescription = "Maximize/Restore"
                        )
                    }
                }

                FilledIconButton(
                    onClick = onCloseRequest,
                    modifier = buttonModifier.onPointerEvent(PointerEventType.Enter) {
                        isCloseButtonHovered = true
                    }.onPointerEvent(PointerEventType.Exit) {
                        isCloseButtonHovered = false
                    },
                    shape = RectangleShape,
                    colors = closeButtonColors
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        modifier = iconModifier,
                        contentDescription = "Close"
                    )
                }
            }
        }
    }
}
