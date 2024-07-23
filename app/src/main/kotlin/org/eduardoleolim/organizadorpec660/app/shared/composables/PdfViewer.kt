package org.eduardoleolim.organizadorpec660.app.shared.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LastPage
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.FirstPage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.pdfbox.Loader
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.PDFRenderer
import org.eduardoleolim.organizadorpec660.app.generated.resources.Res
import org.eduardoleolim.organizadorpec660.app.generated.resources.pdf_viewer_show_all
import org.eduardoleolim.organizadorpec660.app.generated.resources.pdf_viewer_zoom
import org.jetbrains.compose.resources.stringResource
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

data class PdfViewerState(val pdDocument: PDDocument) {
    var index by mutableStateOf(0)
    var showAllPages by mutableStateOf(false)
    var zoom by mutableStateOf(1.0f)
}

@Composable
fun PdfViewerTopBar(
    pdfViewerState: PdfViewerState?,
    isReaderMode: Boolean,
    onOpenFileRequest: () -> Unit,
    containerColor: Color
) {
    val context = rememberCoroutineScope()
    val horizontalScrollState = rememberScrollState()
    var openButtonEnabled by remember { mutableStateOf(true) }
    val isPdfLoaded = remember(pdfViewerState) { pdfViewerState != null }
    val showAllPages = remember(pdfViewerState?.showAllPages) { pdfViewerState?.showAllPages ?: false }
    val zoom = remember(pdfViewerState?.zoom) { pdfViewerState?.zoom ?: 1.0f }

    Surface(
        modifier = Modifier.fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 10.dp),
        shape = MaterialTheme.shapes.small,
        color = containerColor  //MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min).horizontalScroll(horizontalScrollState),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isReaderMode.not()) {
                IconButton(
                    enabled = openButtonEnabled,
                    onClick = {
                        openButtonEnabled = false
                        context.launch(Dispatchers.IO) {
                            onOpenFileRequest()
                            openButtonEnabled = true
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.FileOpen,
                        contentDescription = "Open file",
                        modifier = Modifier.size(16.dp)
                    )
                }

                VerticalDivider()
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = stringResource(Res.string.pdf_viewer_show_all),
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(Modifier.width(8.dp))

                Switch(
                    enabled = isPdfLoaded,
                    checked = showAllPages,
                    onCheckedChange = { checked ->
                        pdfViewerState!!.showAllPages = checked
                    }
                )
            }

            if (showAllPages.not()) {
                VerticalDivider()

                IconButton(
                    enabled = isPdfLoaded && pdfViewerState!!.index > 0,
                    onClick = { pdfViewerState!!.index = 0 }
                ) {
                    Icon(
                        imageVector = Icons.Filled.FirstPage,
                        contentDescription = "First",
                    )
                }
                IconButton(
                    enabled = isPdfLoaded && pdfViewerState!!.index > 0,
                    onClick = {
                        if (pdfViewerState!!.index > 0) {
                            pdfViewerState.index -= 1
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.ChevronLeft,
                        contentDescription = "Previous"
                    )
                }

                val pageInput = remember(pdfViewerState?.index) {
                    pdfViewerState?.let { "${it.index + 1}" } ?: ""
                }
                OutlinedTextField(
                    enabled = isPdfLoaded,
                    value = pageInput,
                    onValueChange = { value ->
                        val pageNumber = value.toIntOrNull()

                        if (isPdfLoaded && pageNumber != null && pageNumber in 1..pdfViewerState!!.pdDocument.numberOfPages) {
                            pdfViewerState.index = pageNumber - 1
                        }
                    },
                    textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
                    modifier = Modifier.width(60.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    keyboardActions = KeyboardActions(
                        onDone = {

                        }
                    )
                )

                IconButton(
                    enabled = isPdfLoaded && pdfViewerState!!.index + 1 < pdfViewerState.pdDocument.numberOfPages,
                    onClick = {
                        if (pdfViewerState!!.index < pdfViewerState.pdDocument.numberOfPages - 1) {
                            pdfViewerState.index += 1
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = "Next"
                    )
                }
                IconButton(
                    enabled = isPdfLoaded && pdfViewerState!!.index + 1 < pdfViewerState.pdDocument.numberOfPages,
                    onClick = { pdfViewerState!!.index = pdfViewerState.pdDocument.numberOfPages - 1 }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.LastPage,
                        contentDescription = "Last"
                    )
                }
            }

            VerticalDivider()

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = stringResource(Res.string.pdf_viewer_zoom),
                    style = MaterialTheme.typography.bodySmall
                )

                Slider(
                    enabled = isPdfLoaded,
                    value = zoom,
                    onValueChange = { zoomLevel -> pdfViewerState!!.zoom = zoomLevel },
                    valueRange = 0.5f..3.0f,
                    modifier = Modifier.width(100.dp).padding(start = 8.dp, end = 8.dp)
                )
            }
        }
    }
}

@Composable
fun PdfViewerContent(
    pdfViewerState: PdfViewerState?,
    modifier: Modifier = Modifier,
    containerColor: Color
) {
    val density = LocalDensity.current
    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    Surface(
        modifier = Modifier.fillMaxSize()
            .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 20.dp)
            .then(modifier),
        shape = MaterialTheme.shapes.extraSmall,
        color = containerColor
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
                .horizontalScroll(horizontalScrollState)
                .run {
                    if (pdfViewerState?.showAllPages == false) {
                        verticalScroll(verticalScrollState)
                    } else {
                        this
                    }
                }
        ) {
            pdfViewerState?.let { state ->
                val renderer = remember(state.pdDocument) { PDFRenderer(state.pdDocument) }

                if (state.showAllPages) {
                    LazyColumn {
                        items(state.pdDocument.numberOfPages) { index ->
                            val image = remember(renderer, state.zoom) {
                                val scale = with(density) { (1f * state.zoom).toDp().toPx() }
                                renderer.renderImage(index, scale)
                            }

                            Image(
                                painter = image.toPainter(),
                                contentDescription = "Page: $index",
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                } else {
                    val image = remember(renderer, state.index, state.zoom) {
                        val scale = with(density) { (1f * state.zoom).toDp().toPx() }
                        renderer.renderImage(state.index, scale)
                    }

                    Image(
                        painter = image.toPainter(),
                        contentDescription = "Index: ${state.index}"
                    )
                }
            }
        }
    }
}

@Composable
fun PdfViewer(
    pdfPath: String? = null,
    isReaderMode: Boolean = false,
    onFileOpened: (File) -> Unit = {},
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    topBarColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    pageColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
) {
    val coroutineScope = rememberCoroutineScope()
    var pdfFile by remember { mutableStateOf<File?>(null) }
    var pdfViewerState by remember { mutableStateOf<PdfViewerState?>(null) }
    val fileChooser = remember {
        JFileChooser().apply {
            isMultiSelectionEnabled = false
            fileFilter = FileNameExtensionFilter("Documentos PDF (*.pdf)", "pdf")
        }
    }

    LaunchedEffect(pdfPath) {
        pdfFile = pdfPath?.let { path ->
            val file = File(path)

            if (file.run { exists() && isFile && extension == "pdf" }) {
                file
            } else {
                null
            }
        }
    }

    DisposableEffect(pdfFile) {
        coroutineScope.launch(Dispatchers.IO) {
            pdfViewerState = pdfFile?.runCatching {
                PdfViewerState(Loader.loadPDF(this)).also {
                    onFileOpened(this)
                }
            }?.getOrNull()
        }

        onDispose {
            pdfViewerState?.pdDocument?.close()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize()
            .then(modifier),
        shape = MaterialTheme.shapes.medium,
        color = containerColor
    ) {
        Column {
            PdfViewerTopBar(
                pdfViewerState = pdfViewerState,
                isReaderMode = isReaderMode,
                onOpenFileRequest = {
                    val result = fileChooser.showOpenDialog(null)

                    if (result == JFileChooser.APPROVE_OPTION) {
                        pdfFile = fileChooser.selectedFile
                    }
                },
                containerColor = topBarColor
            )

            PdfViewerContent(
                pdfViewerState = pdfViewerState,
                modifier = Modifier.weight(1f),
                containerColor = pageColor
            )
        }
    }
}
