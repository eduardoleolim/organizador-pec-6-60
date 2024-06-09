package org.eduardoleolim.organizadorpec660.app.shared.composables

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

@Composable
fun <T> OutlinedSelect(
    values: List<T>,
    onValueSelected: (T?) -> Unit,
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
    var selectedIndex by remember { mutableStateOf(valueIndex) }
    var selectedValue by remember(selectedIndex) { mutableStateOf(selectedIndex?.let { values[it] }) }

    LaunchedEffect(selectedValue) {
        onValueSelected(selectedValue)
    }

    Box {
        val focusManager = LocalFocusManager.current

        var expanded by remember { mutableStateOf(false) }
        var textFieldSize by remember { mutableStateOf(IntSize.Zero) }
        var supportingTextSize by remember(supportingText) { mutableStateOf(IntSize.Zero) }

        OutlinedTextField(
            value = selectedValue?.let { visualTransformation(it) } ?: "",
            onValueChange = { },
            modifier = modifier.onGloballyPositioned { coordinates ->
                textFieldSize = coordinates.size
            }.onPreviewKeyEvent { keyEvent ->
                when {
                    (keyEvent.key == Key.DirectionDown && keyEvent.type == KeyEventType.KeyDown) -> {
                        if (selectedIndex == null) {
                            selectedIndex = 0

                            true
                        } else if (selectedIndex!! + 1 < values.size) {
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
                        focusManager.moveFocus(FocusDirection.Down)

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
                        modifier = Modifier.onGloballyPositioned { coordinates ->
                            supportingTextSize = coordinates.size
                        }
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
            modifier = Modifier.widthIn(min = textFieldSize.width.dp),
            offset = DpOffset(0.dp, (0 - supportingTextSize.height).dp)
        ) {
            values.forEach {
                DropdownMenuItem(
                    text = { Text(visualTransformation(it)) },
                    onClick = {
                        expanded = false
                        selectedValue = it
                    }
                )
            }
        }
    }
}
