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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.isUnspecified
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.*
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.ParentDataModifierNode
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.*
import org.eduardoleolim.window.utils.macos.MacUtil
import java.awt.Window
import kotlin.math.max

internal const val TITLE_BAR_COMPONENT_LAYOUT_ID_PREFIX = "__TITLE_BAR_"

internal const val TITLE_BAR_LAYOUT_ID = "__TITLE_BAR_CONTENT__"

internal const val TITLE_BAR_BORDER_LAYOUT_ID = "__TITLE_BAR_BORDER__"

@Composable
fun DecoratedWindowScope.TitleBar(
    minHeight: Dp = 40.dp,
    modifier: Modifier = Modifier,
    gradientStartColor: Color = Color.Unspecified,
    onCloseRequest: () -> Unit,
    content: @Composable TitleBarScope.(DecoratedWindowState) -> Unit,
) {
    CompositionLocalProvider(
        LocalContentColor provides MaterialTheme.colorScheme.onPrimaryContainer,
        // LocalIconButtonStyle provides style.iconButtonStyle,
        // LocalDefaultDropdownStyle provides style.dropdownStyle,
    ) {
        when (DesktopPlatform.Current) {
            DesktopPlatform.Linux -> TitleBarOnLinux(minHeight, modifier, gradientStartColor, onCloseRequest, content)
            DesktopPlatform.Windows -> TitleBarOnWindows(minHeight, modifier, gradientStartColor, content)
            DesktopPlatform.MacOS -> TitleBarOnMacOs(minHeight, modifier, gradientStartColor, content)
            DesktopPlatform.Unknown -> error("TitleBar is not supported on this platform(${System.getProperty("os.name")})")
        }
    }
}

@Composable
fun DecoratedDialogWindowScope.TitleBar(
    modifier: Modifier = Modifier,
    gradientStartColor: Color = Color.Unspecified,
    onCloseRequest: () -> Unit,
    content: @Composable TitleBarScope.(DecoratedDialogWindowState) -> Unit,
) {
    CompositionLocalProvider(
        LocalContentColor provides MaterialTheme.colorScheme.onPrimaryContainer,
        // LocalIconButtonStyle provides style.iconButtonStyle,
        // LocalDefaultDropdownStyle provides style.dropdownStyle,
    ) {
        when (DesktopPlatform.Current) {
            DesktopPlatform.Linux -> TitleBarOnLinux(modifier, gradientStartColor, onCloseRequest, content)
            DesktopPlatform.Windows -> TitleBarOnWindows(modifier, gradientStartColor, content)
            DesktopPlatform.MacOS -> TitleBarOnMacOs(modifier, gradientStartColor, content)
            DesktopPlatform.Unknown -> error("TitleBar is not supported on this platform(${System.getProperty("os.name")})")
        }
    }
}

@Composable
internal fun DecoratedWindowScope.TitleBarImpl(
    minHeight: Dp,
    modifier: Modifier = Modifier,
    gradientStartColor: Color = Color.Unspecified,
    applyTitleBar: (Dp, DecoratedWindowState) -> PaddingValues,
    content: @Composable TitleBarScope.(DecoratedWindowState) -> Unit,
) {
    val titleBarInfo = LocalTitleBarInfo.current
    val density = LocalDensity.current

    TitleBarImpl(
        minHeight = minHeight,
        modifier = modifier,
        gradientStartColor = gradientStartColor,
        onSizeChanged = { with(density) { applyTitleBar(it.height.toDp(), state) } },
        content = {
            val scope = TitleBarScopeImpl(titleBarInfo.title, titleBarInfo.icon)
            scope.content(state)
        },
        measurePolicy = rememberDecoratedWindowTitleBarMeasurePolicy(
            window,
            state,
            applyTitleBar,
        )
    )
}

@Composable
internal fun DecoratedDialogWindowScope.TitleBarImpl(
    minHeight: Dp,
    modifier: Modifier = Modifier,
    gradientStartColor: Color = Color.Unspecified,
    applyTitleBar: (Dp, DecoratedDialogWindowState) -> PaddingValues,
    content: @Composable TitleBarScope.(DecoratedDialogWindowState) -> Unit,
) {
    val titleBarInfo = LocalTitleBarInfo.current
    val density = LocalDensity.current

    TitleBarImpl(
        minHeight = minHeight,
        modifier = modifier,
        gradientStartColor = gradientStartColor,
        onSizeChanged = { with(density) { applyTitleBar(it.height.toDp(), state) } },
        content = {
            val scope = TitleBarScopeImpl(titleBarInfo.title, titleBarInfo.icon)
            scope.content(state)
        },
        measurePolicy = rememberDecoratedDialogTitleBarMeasurePolicy(
            window,
            state,
            applyTitleBar,
        )
    )
}

@Composable
private fun TitleBarImpl(
    minHeight: Dp,
    modifier: Modifier = Modifier,
    gradientStartColor: Color = Color.Unspecified,
    onSizeChanged: (IntSize) -> Unit,
    measurePolicy: MeasurePolicy,
    content: @Composable () -> Unit
) {
    val background = MaterialTheme.colorScheme.primaryContainer

    val backgroundBrush =
        remember(background, gradientStartColor) {
            if (gradientStartColor.isUnspecified) {
                SolidColor(background)
            } else {
                Brush.horizontalGradient(
                    0.0f to background,
                    0.5f to gradientStartColor,
                    1.0f to background,
                    // startX = style.metrics.gradientStartX.toPx(),
                    // endX = style.metrics.gradientEndX.toPx(),
                )
            }
        }

    Layout(
        content = content,
        modifier = modifier.background(backgroundBrush)
            .focusProperties { canFocus = false }
            .layoutId(TITLE_BAR_LAYOUT_ID)
            .heightIn(min = minHeight)
            .onSizeChanged { onSizeChanged(it) }
            .fillMaxWidth(),
        measurePolicy = measurePolicy
    )

    Spacer(
        Modifier.layoutId(TITLE_BAR_BORDER_LAYOUT_ID)
            .height(1.dp)
            .fillMaxWidth()
            //.background(style.colors.border)
            .background(MaterialTheme.colorScheme.secondaryContainer),
    )
}

internal class DecoratedWindowTitleBarMeasurePolicy(
    private val window: Window,
    private val state: DecoratedWindowState,
    private val applyTitleBar: (Dp, DecoratedWindowState) -> PaddingValues,
) : MeasurePolicy {
    override fun MeasureScope.measure(measurables: List<Measurable>, constraints: Constraints): MeasureResult {
        if (measurables.isEmpty()) {
            return layout(width = constraints.minWidth, height = constraints.minHeight) {}
        }

        var occupiedSpaceHorizontally = 0

        var maxSpaceVertically = constraints.minHeight
        val contentConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        val measuredPlaceable = mutableListOf<Pair<Measurable, Placeable>>()

        for (it in measurables) {
            val placeable = it.measure(contentConstraints.offset(horizontal = -occupiedSpaceHorizontally))
            if (constraints.maxWidth < occupiedSpaceHorizontally + placeable.width) {
                break
            }
            occupiedSpaceHorizontally += placeable.width
            maxSpaceVertically = max(maxSpaceVertically, placeable.height)
            measuredPlaceable += it to placeable
        }

        val boxHeight = maxSpaceVertically

        val contentPadding = applyTitleBar(boxHeight.toDp(), state)

        val leftInset = contentPadding.calculateLeftPadding(layoutDirection).roundToPx()
        val rightInset = contentPadding.calculateRightPadding(layoutDirection).roundToPx()

        occupiedSpaceHorizontally += leftInset
        occupiedSpaceHorizontally += rightInset

        val boxWidth = maxOf(constraints.minWidth, occupiedSpaceHorizontally)

        return layout(boxWidth, boxHeight) {
            if (state.isFullscreen) {
                MacUtil.updateFullScreenButtons(window)
            }
            val placeableGroups = measuredPlaceable.groupBy { (measurable, _) ->
                (measurable.parentData as? TitleBarChildDataNode)?.horizontalAlignment ?: Alignment.CenterHorizontally
            }

            var headUsedSpace = leftInset
            var trailerUsedSpace = rightInset

            placeableGroups[Alignment.Start]?.forEach { (_, placeable) ->
                val x = headUsedSpace
                val y = Alignment.CenterVertically.align(placeable.height, boxHeight)
                placeable.placeRelative(x, y)
                headUsedSpace += placeable.width
            }
            placeableGroups[Alignment.End]?.forEach { (_, placeable) ->
                val x = boxWidth - placeable.width - trailerUsedSpace
                val y = Alignment.CenterVertically.align(placeable.height, boxHeight)
                placeable.placeRelative(x, y)
                trailerUsedSpace += placeable.width
            }

            val centerPlaceable = placeableGroups[Alignment.CenterHorizontally].orEmpty()

            val requiredCenterSpace = centerPlaceable.sumOf { it.second.width }
            val minX = headUsedSpace
            val maxX = boxWidth - trailerUsedSpace - requiredCenterSpace
            var centerX = (boxWidth - requiredCenterSpace) / 2

            if (minX <= maxX) {
                if (centerX > maxX) {
                    centerX = maxX
                }
                if (centerX < minX) {
                    centerX = minX
                }

                centerPlaceable.forEach { (_, placeable) ->
                    val x = centerX
                    val y = Alignment.CenterVertically.align(placeable.height, boxHeight)
                    placeable.placeRelative(x, y)
                    centerX += placeable.width
                }
            }
        }
    }
}

internal class DecoratedDialogWindowTitleBarMeasurePolicy(
    private val window: Window,
    private val state: DecoratedDialogWindowState,
    private val applyTitleBar: (Dp, DecoratedDialogWindowState) -> PaddingValues,
) : MeasurePolicy {
    override fun MeasureScope.measure(measurables: List<Measurable>, constraints: Constraints): MeasureResult {
        if (measurables.isEmpty()) {
            return layout(width = constraints.minWidth, height = constraints.minHeight) {}
        }

        var occupiedSpaceHorizontally = 0

        var maxSpaceVertically = constraints.minHeight
        val contentConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        val measuredPlaceable = mutableListOf<Pair<Measurable, Placeable>>()

        for (it in measurables) {
            val placeable = it.measure(contentConstraints.offset(horizontal = -occupiedSpaceHorizontally))
            if (constraints.maxWidth < occupiedSpaceHorizontally + placeable.width) {
                break
            }
            occupiedSpaceHorizontally += placeable.width
            maxSpaceVertically = max(maxSpaceVertically, placeable.height)
            measuredPlaceable += it to placeable
        }

        val boxHeight = maxSpaceVertically

        val contentPadding = applyTitleBar(boxHeight.toDp(), state)

        val leftInset = contentPadding.calculateLeftPadding(layoutDirection).roundToPx()
        val rightInset = contentPadding.calculateRightPadding(layoutDirection).roundToPx()

        occupiedSpaceHorizontally += leftInset
        occupiedSpaceHorizontally += rightInset

        val boxWidth = maxOf(constraints.minWidth, occupiedSpaceHorizontally)

        return layout(boxWidth, boxHeight) {
            val placeableGroups = measuredPlaceable.groupBy { (measurable, _) ->
                (measurable.parentData as? TitleBarChildDataNode)?.horizontalAlignment ?: Alignment.CenterHorizontally
            }

            var headUsedSpace = leftInset
            var trailerUsedSpace = rightInset

            placeableGroups[Alignment.Start]?.forEach { (_, placeable) ->
                val x = headUsedSpace
                val y = Alignment.CenterVertically.align(placeable.height, boxHeight)
                placeable.placeRelative(x, y)
                headUsedSpace += placeable.width
            }
            placeableGroups[Alignment.End]?.forEach { (_, placeable) ->
                val x = boxWidth - placeable.width - trailerUsedSpace
                val y = Alignment.CenterVertically.align(placeable.height, boxHeight)
                placeable.placeRelative(x, y)
                trailerUsedSpace += placeable.width
            }

            val centerPlaceable = placeableGroups[Alignment.CenterHorizontally].orEmpty()

            val requiredCenterSpace = centerPlaceable.sumOf { it.second.width }
            val minX = headUsedSpace
            val maxX = boxWidth - trailerUsedSpace - requiredCenterSpace
            var centerX = (boxWidth - requiredCenterSpace) / 2

            if (minX <= maxX) {
                if (centerX > maxX) {
                    centerX = maxX
                }
                if (centerX < minX) {
                    centerX = minX
                }

                centerPlaceable.forEach { (_, placeable) ->
                    val x = centerX
                    val y = Alignment.CenterVertically.align(placeable.height, boxHeight)
                    placeable.placeRelative(x, y)
                    centerX += placeable.width
                }
            }
        }
    }
}

@Composable
internal fun rememberDecoratedWindowTitleBarMeasurePolicy(
    window: Window,
    state: DecoratedWindowState,
    applyTitleBar: (Dp, DecoratedWindowState) -> PaddingValues,
): MeasurePolicy =
    remember(window, state, applyTitleBar) {
        DecoratedWindowTitleBarMeasurePolicy(window, state, applyTitleBar)
    }

@Composable
internal fun rememberDecoratedDialogTitleBarMeasurePolicy(
    window: Window,
    state: DecoratedDialogWindowState,
    applyTitleBar: (Dp, DecoratedDialogWindowState) -> PaddingValues,
): MeasurePolicy =
    remember(window, state, applyTitleBar) {
        DecoratedDialogWindowTitleBarMeasurePolicy(window, state, applyTitleBar)
    }

interface TitleBarScope {
    val title: String

    val icon: Painter?

    @Stable
    fun Modifier.align(alignment: Alignment.Horizontal): Modifier
}

private class TitleBarScopeImpl(
    override val title: String,
    override val icon: Painter?,
) : TitleBarScope {
    override fun Modifier.align(alignment: Alignment.Horizontal): Modifier =
        this then
            TitleBarChildDataElement(
                alignment,
                debugInspectorInfo {
                    name = "align"
                    value = alignment
                },
            )
}

private class TitleBarChildDataElement(
    val horizontalAlignment: Alignment.Horizontal,
    val inspectorInfo: InspectorInfo.() -> Unit = NoInspectorInfo,
) : ModifierNodeElement<TitleBarChildDataNode>(), InspectableValue {
    override fun create(): TitleBarChildDataNode = TitleBarChildDataNode(horizontalAlignment)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val otherModifier = other as? TitleBarChildDataElement ?: return false
        return horizontalAlignment == otherModifier.horizontalAlignment
    }

    override fun hashCode(): Int = horizontalAlignment.hashCode()

    override fun update(node: TitleBarChildDataNode) {
        node.horizontalAlignment = horizontalAlignment
    }

    override fun InspectorInfo.inspectableProperties() {
        inspectorInfo()
    }
}

private class TitleBarChildDataNode(
    var horizontalAlignment: Alignment.Horizontal,
) : ParentDataModifierNode, Modifier.Node() {
    override fun Density.modifyParentData(parentData: Any?) = this@TitleBarChildDataNode
}
