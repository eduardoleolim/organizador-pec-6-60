package org.eduardoleolim.organizadorpec660.shared.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.rememberDialogState
import org.eduardoleolim.organizadorpec660.app.generated.resources.*
import org.eduardoleolim.organizadorpec660.shared.window.DecoratedDialogWindow
import org.jetbrains.compose.resources.stringResource

class Dialogs {
    enum class Type { ERROR, INFORMATION, WARNING, QUESTION, PLAIN }
    enum class Option { DEFAULT, YES_NO, YES_NO_CANCEL, OK_CANCEL }
    enum class Response { YES, NO, CANCEL, OK, CLOSED }
    enum class Mode { LOAD, SAVE }

    companion object {
        @Composable
        private fun YesButton(onClick: (Response) -> Unit) {
            TextButton(onClick = { onClick(Response.YES) }) {
                Text(stringResource(Res.string.yes))
            }
        }

        @Composable
        private fun NoButton(onClick: (Response) -> Unit) {
            TextButton(onClick = { onClick(Response.NO) }) {
                Text(stringResource(Res.string.no))
            }
        }

        @Composable
        private fun CancelButton(onClick: (Response) -> Unit) {
            TextButton(onClick = { onClick(Response.CANCEL) }) {
                Text(stringResource(Res.string.cancel))
            }
        }

        @Composable
        private fun OkButton(onClick: (Response) -> Unit) {
            TextButton(onClick = { onClick(Response.OK) }) {
                Text(stringResource(Res.string.ok))
            }
        }

        @Composable
        fun OptionDialog(
            message: String,
            title: String,
            optionType: Option,
            messageType: Type,
            icon: ImageVector?,
            options: List<Any>?,
            initialOption: Any?
        ) = OptionDialog(
            message = { Text(message) },
            title = title,
            optionType = optionType,
            messageType = messageType,
            icon = icon,
            options = options,
            initialOption = initialOption
        )

        @Composable
        fun OptionDialog(
            message: @Composable ColumnScope.() -> Unit,
            title: String,
            optionType: Option,
            messageType: Type,
            icon: ImageVector?,
            options: List<Any>?,
            initialOption: Any?
        ): Response? {
            var isShowing by remember { mutableStateOf(true) }
            var result by remember { mutableStateOf<Response?>(null) }

            if (isShowing) {
                DecoratedDialogWindow(
                    state = rememberDialogState(size = DpSize(400.dp, 200.dp)),
                    title = title,
                    onCloseRequest = {
                        result = Response.CLOSED
                        isShowing = false
                    }
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(16.dp)
                        ) {
                            val messageIcon = icon ?: when (messageType) {
                                Type.ERROR -> Icons.Filled.Error
                                Type.INFORMATION -> Icons.Filled.Info
                                Type.WARNING -> Icons.Filled.Warning
                                Type.QUESTION -> Icons.Filled.QuestionMark
                                Type.PLAIN -> null
                            }

                            Row(
                                modifier = Modifier.weight(1.0f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                messageIcon?.let {
                                    Image(
                                        imageVector = messageIcon,
                                        contentDescription = "icon",
                                        colorFilter = ColorFilter.tint(LocalContentColor.current),
                                        modifier = Modifier.size(45.dp)
                                    )

                                    Spacer(Modifier.width(16.dp))
                                }

                                Column(modifier = Modifier.weight(1.0f)) {
                                    message()
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = when (optionType) {
                                    Option.DEFAULT -> Arrangement.Center
                                    Option.YES_NO, Option.OK_CANCEL, Option.YES_NO_CANCEL -> Arrangement.End
                                }
                            ) {
                                if (optionType == Option.DEFAULT || optionType == Option.OK_CANCEL) {
                                    OkButton(
                                        onClick = {
                                            result = it
                                            isShowing = false
                                        }
                                    )
                                }

                                if (optionType == Option.YES_NO || optionType == Option.YES_NO_CANCEL) {
                                    YesButton(
                                        onClick = {
                                            result = it
                                            isShowing = false
                                        }
                                    )

                                    NoButton(
                                        onClick = {
                                            result = it
                                            isShowing = false
                                        }
                                    )
                                }

                                if (optionType == Option.OK_CANCEL || optionType == Option.YES_NO_CANCEL) {
                                    CancelButton(
                                        onClick = {
                                            result = it
                                            isShowing = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            return result
        }

        @Composable
        fun MessageDialog(message: String) {
            val title = stringResource(Res.string.message_dialog_title)

            MessageDialog(message, title, Type.INFORMATION)
        }

        @Composable
        fun MessageDialog(message: @Composable ColumnScope.() -> Unit) {
            val title = stringResource(Res.string.message_dialog_title)

            MessageDialog(message, title, Type.INFORMATION)
        }

        @Composable
        fun MessageDialog(message: String, title: String, messageType: Type) {
            MessageDialog(message, title, messageType, null)
        }

        @Composable
        fun MessageDialog(message: @Composable ColumnScope.() -> Unit, title: String, messageType: Type) {
            MessageDialog(message, title, messageType, null)
        }

        @Composable
        fun MessageDialog(message: String, title: String, messageType: Type, icon: ImageVector?) {
            OptionDialog(message, title, Option.DEFAULT, messageType, icon, null, null)
        }

        @Composable
        fun MessageDialog(
            message: @Composable ColumnScope.() -> Unit,
            title: String,
            messageType: Type,
            icon: ImageVector?
        ) {
            OptionDialog(message, title, Option.DEFAULT, messageType, icon, null, null)
        }

        @Composable
        fun ConfirmDialog(message: @Composable ColumnScope.() -> Unit): Response? {
            val title = stringResource(Res.string.confirm_dialog_title)

            return ConfirmDialog(message, title, Option.YES_NO_CANCEL)
        }

        @Composable
        fun ConfirmDialog(message: String): Response? {
            val title = stringResource(Res.string.confirm_dialog_title)

            return ConfirmDialog(message, title, Option.YES_NO_CANCEL)
        }

        @Composable
        fun ConfirmDialog(message: @Composable ColumnScope.() -> Unit, title: String, optionType: Option) =
            ConfirmDialog(message, title, optionType, Type.QUESTION)

        @Composable
        fun ConfirmDialog(message: String, title: String, optionType: Option) =
            ConfirmDialog(message, title, optionType, Type.QUESTION)

        @Composable
        fun ConfirmDialog(
            message: @Composable ColumnScope.() -> Unit,
            title: String,
            optionType: Option,
            messageType: Type
        ) = ConfirmDialog(message, title, optionType, messageType, null)

        @Composable
        fun ConfirmDialog(message: String, title: String, optionType: Option, messageType: Type) =
            ConfirmDialog(message, title, optionType, messageType, null)

        @Composable
        fun ConfirmDialog(
            message: @Composable ColumnScope.() -> Unit,
            title: String,
            optionType: Option,
            messageType: Type,
            icon: ImageVector?
        ) = OptionDialog(message, title, optionType, messageType, icon, null, null)

        @Composable
        fun ConfirmDialog(message: String, title: String, optionType: Option, messageType: Type, icon: ImageVector?) =
            OptionDialog(message, title, optionType, messageType, icon, null, null)
    }
}
