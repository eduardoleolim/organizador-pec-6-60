package org.eduardoleolim.organizadorpec660.app.shared.window

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeDialog
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.DialogWindowScope
import androidx.compose.ui.window.rememberDialogState
import org.eduardoleolim.organizadorpec660.app.shared.window.utils.CustomWindowDecorationAccessing

class DialogWindowController {
    var title: String? by mutableStateOf(null)
    var icon: Painter? by mutableStateOf(null)
    var onIconClick: (() -> Unit)? by mutableStateOf(null)
}

val LocalDialogWindow = compositionLocalOf<ComposeDialog> { error("window not provided") }
private val LocalDialogWindowController =
    compositionLocalOf<DialogWindowController> { error("window controller not provided") }
private val LocalDialogWindowState = compositionLocalOf<DialogState> { error("window controller not provided") }

@Composable
fun DialogWindowTitle(title: String) {
    val c = LocalDialogWindowController.current
    LaunchedEffect(title) {
        c.title = title
    }
    DisposableEffect(Unit) {
        onDispose {
            c.title = null
        }
    }
}

@Composable
fun DialogWindowIcon(icon: Painter, onClick: () -> Unit) {
    val current = LocalDialogWindowController.current
    DisposableEffect(icon) {
        current.let {
            it.icon = icon
            it.onIconClick = onClick

            onDispose {
                it.icon = null
                it.onIconClick = null
            }
        }
    }
}

@Composable
private fun DialogWindowScope.FrameContent(
    title: String,
    windowIcon: Painter? = null,
    onRequestClose: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
            .background(MaterialTheme.colorScheme.primaryContainer),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(Modifier.width(16.dp))
        windowIcon?.let {
            val onIconClick = LocalDialogWindowController.current.onIconClick

            Image(
                painter = it,
                contentDescription = "DialogWindow Icon",
                modifier = Modifier
                    .dialogWindowFrameItem("icon-window", HitSpots.OTHER_HIT_SPOT)
                    .size(16.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            if (onIconClick != null) {
                                onIconClick()
                            }
                        }
                    )
            )
        }

        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.onBackground.copy(0.75f)
        ) {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 14.sp,
                modifier = Modifier.dialogWindowFrameItem("title", HitSpots.DRAGGABLE_AREA),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Spacer(Modifier.weight(1.0f))

        DialogWindowsActionButtons(
            onRequestClose = onRequestClose,
        )
    }
}

@Composable
fun DialogWindowScope.SnapDraggableToolbar(
    title: String,
    windowIcon: Painter? = null,
    onRequestClose: () -> Unit,
) {
    ProvideWindowSpotContainer {
        if (CustomWindowDecorationAccessing.isSupported) {
            FrameContent(title, windowIcon, onRequestClose)
        } else {
            WindowDraggableArea {
                FrameContent(title, windowIcon, onRequestClose)
            }
        }
    }
}

@Composable
private fun DialogWindowScope.CustomDialogWindowFrame(
    onRequestClose: () -> Unit,
    title: String,
    windowIcon: Painter? = null,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalContentColor provides MaterialTheme.colorScheme.onBackground,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            SnapDraggableToolbar(
                title = title,
                windowIcon = windowIcon,
                onRequestClose = onRequestClose
            )
            content()
        }
    }
}

@Composable
fun CustomDialogWindow(
    onCloseRequest: () -> Unit,
    state: DialogState = rememberDialogState(),
    visible: Boolean = true,
    defaultTitle: String = "Untitled",
    defaultIcon: Painter? = null,
    resizable: Boolean = true,
    enabled: Boolean = true,
    focusable: Boolean = true,
    alwaysOnTop: Boolean = false,
    onPreviewKeyEvent: ((KeyEvent) -> Boolean) = { false },
    onKeyEvent: ((KeyEvent) -> Boolean) = { false },
    content: @Composable DialogWindowScope.() -> Unit
) {
    val windowController = remember { DialogWindowController() }

    val transparent: Boolean
    val undecorated: Boolean
    val isAeroSnapSupported = CustomWindowDecorationAccessing.isSupported

    if (isAeroSnapSupported) {
        transparent = false
        undecorated = false
    } else {
        transparent = true
        undecorated = true
    }

    DialogWindow(
        state = state,
        enabled = enabled,
        visible = visible,
        resizable = resizable,
        focusable = focusable,
        alwaysOnTop = alwaysOnTop,
        onPreviewKeyEvent = onPreviewKeyEvent,
        onKeyEvent = onKeyEvent,
        transparent = transparent,
        undecorated = undecorated,
        icon = defaultIcon,
        onCloseRequest = onCloseRequest
    ) {
        val title = windowController.title ?: defaultTitle
        val icon = windowController.icon ?: defaultIcon

        LaunchedEffect(title) {
            window.title = title
        }

        CompositionLocalProvider(
            LocalDialogWindowController provides windowController,
            LocalDialogWindowState provides state,
            LocalDialogWindow provides window
        ) {
            CustomDialogWindowFrame(
                onRequestClose = onCloseRequest,
                title = title,
                windowIcon = icon
            ) {
                content()
            }
        }
    }
}
