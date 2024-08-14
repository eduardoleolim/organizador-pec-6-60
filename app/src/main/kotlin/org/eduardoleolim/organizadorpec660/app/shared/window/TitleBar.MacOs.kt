package org.eduardoleolim.organizadorpec660.app.shared.window

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jetbrains.JBR
import org.eduardoleolim.organizadorpec660.app.shared.window.utils.macos.MacUtil

public fun Modifier.newFullscreenControls(newControls: Boolean = true): Modifier =
    this then
        NewFullscreenControlsElement(
            newControls,
            debugInspectorInfo {
                name = "newFullscreenControls"
                value = newControls
            },
        )

private class NewFullscreenControlsElement(
    val newControls: Boolean,
    val inspectorInfo: InspectorInfo.() -> Unit,
) : ModifierNodeElement<NewFullscreenControlsNode>() {
    override fun create(): NewFullscreenControlsNode = NewFullscreenControlsNode(newControls)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val otherModifier =
            other as? NewFullscreenControlsElement
                ?: return false
        return newControls == otherModifier.newControls
    }

    override fun hashCode(): Int = newControls.hashCode()

    override fun InspectorInfo.inspectableProperties() {
        inspectorInfo()
    }

    override fun update(node: NewFullscreenControlsNode) {
        node.newControls = newControls
    }
}

private class NewFullscreenControlsNode(var newControls: Boolean) : Modifier.Node()

@Composable
internal fun DecoratedWindowScope.TitleBarOnMacOs(
    minHeight: Dp = 40.dp,
    modifier: Modifier = Modifier,
    gradientStartColor: Color = Color.Unspecified,
    content: @Composable TitleBarScope.(DecoratedWindowState) -> Unit,
) {
    val newFullscreenControls =
        modifier.foldOut(false) { e, r ->
            if (e is NewFullscreenControlsElement) {
                e.newControls
            } else {
                r
            }
        }

    if (newFullscreenControls) {
        System.setProperty("apple.awt.newFullScreeControls", true.toString())
        System.setProperty(
            "apple.awt.newFullScreeControls.background",
            "${MaterialTheme.colorScheme.primaryContainer.toArgb()}",
        )
        MacUtil.updateColors(window)
    } else {
        System.clearProperty("apple.awt.newFullScreeControls")
        System.clearProperty("apple.awt.newFullScreeControls.background")
    }

    val titleBar = remember { JBR.windowDecorations!!.createCustomTitleBar()!! }

    TitleBarImpl(
        modifier = modifier.customTitleBarMouseEventHandler(titleBar),
        gradientStartColor = gradientStartColor,
        applyTitleBar = { height, state ->
            if (state.isFullscreen) {
                MacUtil.updateFullScreenButtons(window)
            }
            titleBar.height = height.value
            JBR.windowDecorations!!.setCustomTitleBar(window, titleBar)

            if (state.isFullscreen && newFullscreenControls) {
                PaddingValues(start = 80.dp)
            } else {
                PaddingValues(start = titleBar.leftInset.dp, end = titleBar.rightInset.dp)
            }
        },
        content = content,
        minHeight = minHeight
    )
}
