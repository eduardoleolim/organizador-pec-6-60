package org.eduardoleolim.organizadorpec660.app.window

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
import org.eduardoleolim.organizadorpec660.app.window.utils.CustomWindowDecorationAccessing
import java.awt.Rectangle
import java.awt.Shape
import java.awt.Window
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent

object HitSpots {
    const val NO_HIT_SPOT = 0
    const val OTHER_HIT_SPOT = 1
    const val MINIMIZE_BUTTON = 2
    const val MAXIMIZE_BUTTON = 3
    const val CLOSE_BUTTON = 4
    const val MENU_BAR = 5
    const val DRAGGABLE_AREA = 6
}

private val LocalWindowHitSpots =
    compositionLocalOf<MutableMap<Any, Pair<Rectangle, Int>>> { error("LocalWindowHitSpots not provided") }

@Composable
private fun FrameWindowScope.getCurrentWindowSize(): DpSize {
    var windowSize by remember { mutableStateOf(DpSize(window.width.dp, window.height.dp)) }

    DisposableEffect(window) {
        val listener = object : ComponentAdapter() {
            override fun componentResized(event: ComponentEvent) {
                val size = event.component.size
                windowSize = DpSize(size.width.dp, size.height.dp)
            }
        }

        window.addComponentListener(listener)
        onDispose {
            window.removeComponentListener(listener)
        }
    }

    return windowSize
}

context (FrameWindowScope)
@Composable
private fun Modifier.onPositionInRect(onChange: (Rectangle) -> Unit) = composed {
    val density = LocalDensity.current

    onGloballyPositioned {
        val position = it.positionInWindow()
        val shape = position.toDpRectangle(it.size.width, it.size.height, density)
        onChange(shape)
    }
}

private fun Offset.toDpRectangle(width: Int, height: Int, density: Density): Rectangle {
    density.run {
        val awtX = x.toAwtUnitSize()
        val awtY = y.toAwtUnitSize()
        val awtWidth = width.toAwtUnitSize()
        val awtHeight = height.toAwtUnitSize()

        return Rectangle(awtX, awtY, awtWidth, awtHeight)
    }
}

context (Density)
private fun Int.toAwtUnitSize() = toDp().value.toInt()

context (Density)
private fun Float.toAwtUnitSize() = toDp().value.toInt()

private fun placeHitSpots(window: Window, spots: Map<Shape, Int>, height: Int) {
    CustomWindowDecorationAccessing.setCustomDecorationEnabled(window, true)
    CustomWindowDecorationAccessing.setCustomDecorationTitleBarHeight(window, height)
    CustomWindowDecorationAccessing.setCustomDecorationHitTestSpotsMethod(window, spots)
}


context (FrameWindowScope)
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ProvideWindowSpotContainer(content: @Composable () -> Unit) {
    val density = LocalDensity.current
    val spotInfoState = remember { mutableStateMapOf<Any, Pair<Rectangle, Int>>() }
    var toolbarHeight by remember { mutableStateOf(0) }
    val spotsWithInfo = spotInfoState.toMap()
    val windowSize = getCurrentWindowSize()
    val containerSize = with(density) {
        LocalWindowInfo.current.containerSize.let {
            DpSize(it.width.toDp(), (it.height - toolbarHeight).toDp())
        }
    }

    LaunchedEffect(spotsWithInfo, toolbarHeight, window, windowSize, containerSize) {
        if (CustomWindowDecorationAccessing.isSupported) {
            val startOffset = (windowSize - containerSize) / 2
            val startWidthOffsetInDp = startOffset.width.value.toInt()
            // val startHeightInDp=delta.height.value.toInt() //it seems no need here
            val spots: Map<Shape, Int> = spotsWithInfo.values.associate { (rect, spot) ->
                Rectangle(rect.x + startWidthOffsetInDp, rect.y, rect.width, rect.height) to spot
            }
            placeHitSpots(window, spots, toolbarHeight)
        }
    }

    CompositionLocalProvider(LocalWindowHitSpots provides spotInfoState) {
        Box(
            modifier = Modifier.onGloballyPositioned {
                toolbarHeight = with(density) {
                    it.size.height.toAwtUnitSize()
                }
            }
        ) {
            content()
        }
    }
}

context (FrameWindowScope)
@Composable
fun Modifier.windowFrameItem(key: Any, spot: Int) = composed {
    var shape by remember(key) { mutableStateOf<Rectangle?>(null) }
    val localWindowSpots = LocalWindowHitSpots.current

    DisposableEffect(shape, key) {
        shape.let { shape ->
            if (shape != null) {
                localWindowSpots[key] = shape to spot
                onDispose {
                    localWindowSpots.remove(key)
                }
            } else {
                onDispose {}
            }
        }
    }

    onPositionInRect { shape = it }
}