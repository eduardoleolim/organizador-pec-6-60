package org.eduardoleolim.organizadorpec660.app.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.icons.Icons.Default
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainWindow(
    title: String = "App",
    onCloseRequest: () -> Unit,
    minHeight: Int? = null,
    minWidth: Int? = null,
    content: (@Composable () -> Unit)? = null
) {
    var windowState by remember { mutableStateOf(WindowState()) }
    var surfaceModifier by remember { mutableStateOf(Modifier.fillMaxSize()) }
    var surfaceRoundedCornerShape by remember { mutableStateOf(RoundedCornerShape(10.dp)) }
    val DOUBLE_CLICK_DELAY = 300L
    var lastClickTime by remember { mutableStateOf(0L) }
    var resizable by remember { mutableStateOf(true) }

    Window(
        onCloseRequest = onCloseRequest,
        state = windowState,
        title = title,
        resizable = resizable,
        undecorated = true,
        transparent = true
    ) {
        window.minimumSize = window.minimumSize.apply {
            minHeight?.let { height = it }
            minWidth?.let { width = it }
        }

        val onMaximizeRestoreRequest = {
            windowState = if (windowState.placement == WindowPlacement.Maximized) {
                resizable = true
                WindowState(placement = WindowPlacement.Floating)
            } else {
                resizable = false
                WindowState(placement = WindowPlacement.Maximized)
            }
        }

        val onMinimizeRequest = {
            window.isMinimized = true
        }

        if (windowState.placement == WindowPlacement.Maximized) {
            surfaceModifier = Modifier.fillMaxSize()
            surfaceRoundedCornerShape = RoundedCornerShape(0.dp)
        } else {
            surfaceModifier = Modifier.fillMaxSize().padding(5.dp).shadow(3.dp, RoundedCornerShape(10.dp))
            surfaceRoundedCornerShape = RoundedCornerShape(10.dp)
        }

        Surface(
            modifier = surfaceModifier,
            color = Color(255, 255, 255),
            shape = surfaceRoundedCornerShape
        ) {
            MaterialTheme {
                Column {
                    WindowDraggableArea(
                        modifier = Modifier.fillMaxWidth()
                            .height(40.dp)
                            .background(Color(0, 119, 200))
                            .pointerInput(Unit) {
                                detectTapGestures {
                                    val currentTime = System.currentTimeMillis()
                                    if (currentTime - lastClickTime < DOUBLE_CLICK_DELAY) {
                                        onMaximizeRestoreRequest()
                                    }
                                    lastClickTime = currentTime
                                }
                            }.onDrag {
                                if (windowState.placement == WindowPlacement.Maximized) {
                                    onMaximizeRestoreRequest()
                                }
                            }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = title,
                                modifier = Modifier.padding(start = 10.dp),
                                color = Color.White
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(end = 10.dp)
                            ) {
                                IconButton(
                                    onClick = onMinimizeRequest
                                ) {
                                    Icon(
                                        imageVector = Default.Add,
                                        contentDescription = "Minimize",
                                        tint = Color.White
                                    )
                                }

                                IconButton(
                                    onClick = onMaximizeRestoreRequest
                                ) {
                                    val icon = if (windowState.placement == WindowPlacement.Maximized) {
                                        Default.AccountBox
                                    } else {
                                        Default.Close
                                    }
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = "Maximize/Restore",
                                        tint = Color.White
                                    )
                                }

                                IconButton(
                                    onClick = { onCloseRequest() }
                                ) {
                                    Icon(
                                        imageVector = Default.Close,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }
                    content?.invoke()
                }
            }
        }
    }
}
