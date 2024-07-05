package org.eduardoleolim.organizadorpec660.app.window

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindowScope
import androidx.compose.ui.window.FrameWindowScope
import org.eduardoleolim.organizadorpec660.app.window.icons.*
import java.beans.PropertyChangeListener

@Composable
fun SystemButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    background: Color = Color.Transparent,
    onBackground: Color = LocalContentColor.current,
    hoveredBackgroundColor: Color = background,
    onHoveredBackgroundColor: Color = LocalContentColor.current,
    icon: Painter,
    modifier: Modifier = Modifier,
) {
    val isFocused = isWindowFocused()
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    Icon(
        painter = icon,
        contentDescription = null,
        tint = animateColorAsState(
            when {
                isHovered -> onHoveredBackgroundColor
                else -> onBackground
            }.copy(
                alpha = when {
                    !enabled -> 0.5f
                    isFocused || isHovered -> 1f
                    else -> 0.7f
                }
            )
        ).value,
        modifier = modifier
            .clickable(enabled = enabled) { onClick() }
            .background(
                animateColorAsState(
                    when {
                        isHovered -> hoveredBackgroundColor
                        else -> background
                    }
                ).value
            )
            .hoverable(interactionSource)
            .windowButton()
    )
}

@Composable
fun CloseButton(onRequestClose: () -> Unit, modifier: Modifier) {
    SystemButton(
        onRequestClose,
        background = Color.Transparent,
        onBackground = MaterialTheme.colorScheme.onBackground,
        hoveredBackgroundColor = Color(0xFFc42b1c),
        onHoveredBackgroundColor = Color.White,
        icon = rememberVectorPainter(CustomIcons.Close),
        modifier = modifier,
    )
}

private fun Modifier.windowButton(): Modifier {
    return padding(
        vertical = 10.dp,
        horizontal = 20.dp
    ).size(10.dp)
}

@Composable
fun FrameWindowScope.WindowsActionButtons(
    onRequestClose: () -> Unit,
    onRequestMinimize: (() -> Unit)?,
    onToggleMaximize: (() -> Unit)?
) {
    var isResizable by remember { mutableStateOf(window.isResizable) }

    DisposableEffect(window) {
        val listener = PropertyChangeListener {
            isResizable = it.newValue as Boolean
        }

        window.addPropertyChangeListener("resizable", listener)
        onDispose {
            window.removePropertyChangeListener("resizable", listener)
        }
    }

    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.Top
    ) {
        onRequestMinimize?.let {
            SystemButton(
                icon = rememberVectorPainter(CustomIcons.Minimize),
                onClick = onRequestMinimize,
                modifier = Modifier
                    .windowFrameItem("minimize", HitSpots.MINIMIZE_BUTTON)
                    .fillMaxHeight()
            )
        }

        onToggleMaximize?.let {
            SystemButton(
                icon = rememberVectorPainter(
                    if (isWindowMaximized()) {
                        CustomIcons.Floating
                    } else {
                        CustomIcons.Maximize
                    }
                ),
                onClick = onToggleMaximize,
                enabled = isResizable,
                modifier = Modifier
                    .windowFrameItem("maximize", HitSpots.MAXIMIZE_BUTTON)
                    .fillMaxHeight()
            )
        }

        CloseButton(
            onRequestClose = onRequestClose,
            modifier = Modifier
                .windowFrameItem("close", HitSpots.CLOSE_BUTTON)
                .fillMaxHeight()
        )
    }
}

@Composable
fun DialogWindowScope.DialogWindowsActionButtons(onRequestClose: () -> Unit) {
    var isResizable by remember { mutableStateOf(window.isResizable) }

    DisposableEffect(window) {
        val listener = PropertyChangeListener {
            isResizable = it.newValue as Boolean
        }

        window.addPropertyChangeListener("resizable", listener)
        onDispose {
            window.removePropertyChangeListener("resizable", listener)
        }
    }

    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.Top
    ) {

        CloseButton(
            onRequestClose = onRequestClose,
            modifier = Modifier
                .dialogWindowFrameItem("close", HitSpots.CLOSE_BUTTON)
                .fillMaxHeight()
        )
    }
}
