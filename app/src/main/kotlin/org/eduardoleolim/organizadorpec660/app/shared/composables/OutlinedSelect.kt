package org.eduardoleolim.organizadorpec660.app.shared.composables

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

@Composable
fun <T> OutlinedSelect(
    items: List<T>,
    onValueSelected: (Int?, T?) -> Unit,
    valueIndex: Int? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: (T) -> String = { it.toString() },
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = OutlinedTextFieldDefaults.shape,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors()
) {
    var selectedIndex by remember(valueIndex) { mutableStateOf(valueIndex) }
    val value = remember(selectedIndex) { selectedIndex?.let { visualTransformation(items[it]) } ?: "" }

    LaunchedEffect(selectedIndex) {
        onValueSelected(selectedIndex, selectedIndex?.let { items[it] })
    }

    Box {
        val density = LocalDensity.current
        val focusManager = LocalFocusManager.current
        var expanded by remember { mutableStateOf(false) }
        var textFieldSize by remember { mutableStateOf(IntSize.Zero) }
        var supportingTextSize by remember(supportingText) { mutableStateOf(IntSize.Zero) }
        val itemHeights = remember { mutableStateMapOf<Int, Int>() }
        val baseHeight = remember { 330.dp }
        val maxHeight = remember(itemHeights.toMap()) {
            if (itemHeights.keys.toSet() != items.indices.toSet()) {
                return@remember baseHeight
            }
            val baseHeightInt = with(density) { baseHeight.toPx().toInt() }

            var sum = 2 * with(density) {
                8.dp.toPx().toInt() // See DropdownMenuVerticalPadding in androidx.compose.material3.Menu
            }

            for ((_, itemSize) in itemHeights.toSortedMap()) {
                sum += itemSize
                if (sum >= baseHeightInt) {
                    return@remember with(density) { (sum - itemSize / 2).toDp() }
                }
            }

            baseHeight
        }

        OutlinedTextField(
            value = value,
            onValueChange = { },
            modifier = modifier.onGloballyPositioned { textFieldSize = it.size }
                .onPreviewKeyEvent { keyEvent ->
                    when {
                        items.isEmpty() -> false

                        (keyEvent.key == Key.DirectionDown && keyEvent.type == KeyEventType.KeyDown) -> {
                            if (selectedIndex == null) {
                                selectedIndex = 0

                                true
                            } else if (selectedIndex!! + 1 < items.size) {
                                selectedIndex = selectedIndex!! + 1

                                true
                            } else {
                                false
                            }
                        }

                        (keyEvent.key == Key.DirectionUp && keyEvent.type == KeyEventType.KeyDown) -> {
                            if (selectedIndex != null && selectedIndex!! > 0) {
                                selectedIndex = selectedIndex!! - 1

                                true
                            } else {
                                false
                            }
                        }

                        (keyEvent.key == Key.Tab && keyEvent.type == KeyEventType.KeyDown) -> {
                            focusManager.moveFocus(FocusDirection.Next)

                            true
                        }

                        else -> false
                    }
                },
            enabled = enabled,
            readOnly = true,
            textStyle = textStyle,
            label = label,
            singleLine = true,
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            trailingIcon = {
                IconButton(
                    enabled = enabled,
                    onClick = { expanded = true },
                    modifier = Modifier.pointerHoverIcon(PointerIcon.Default),
                    content = {
                        Icon(
                            imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                            contentDescription = "Expand"
                        )
                    }
                )
            },
            supportingText = supportingText?.let {
                {
                    Box(
                        modifier = Modifier.onGloballyPositioned { supportingTextSize = it.size }
                    ) {
                        supportingText()
                    }
                }
            },
            isError = isError,
            interactionSource = interactionSource,
            shape = shape,
            colors = colors
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.widthIn(min = textFieldSize.width.dp)
                .requiredSizeIn(maxHeight = maxHeight),
            offset = DpOffset(0.dp, (0 - supportingTextSize.height).dp)
        ) {
            items.forEachIndexed { index, item ->
                DropdownMenuItem(
                    text = {
                        Text(visualTransformation(item))
                    },
                    onClick = {
                        expanded = false
                        selectedIndex = index
                    }
                )
            }
        }
    }
}
