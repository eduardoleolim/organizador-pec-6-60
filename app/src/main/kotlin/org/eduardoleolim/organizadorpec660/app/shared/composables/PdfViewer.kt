package org.eduardoleolim.organizadorpec660.app.shared.composables

import androidx.compose.foundation.Image
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
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

data class PdfViewerState(val pdDocument: PDDocument) {
    var currentPageIndex by mutableStateOf(0)
    var showAllPages by mutableStateOf(false)
    var zoom by mutableStateOf(1.0f)
}

@Composable
fun PdfViewerTopBar(
    pdfViewerState: PdfViewerState?,
    isReaderMode: Boolean,
    onOpenFileRequest: () -> Unit
) {
    val context = rememberCoroutineScope()
    var openButtonEnabled by remember { mutableStateOf(true) }
    val isPdfLoaded = remember(pdfViewerState) { pdfViewerState != null }
    val showAllPages = remember(pdfViewerState?.showAllPages) { pdfViewerState?.showAllPages ?: false }
    val zoom = remember(pdfViewerState?.zoom) { pdfViewerState?.zoom ?: 1.0f }

    Surface(
        modifier = Modifier.fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 10.dp),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceContainerHighest
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min),
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
                    text = "Show all pages",
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
                    enabled = isPdfLoaded && pdfViewerState!!.currentPageIndex > 0,
                    onClick = {
                        pdfViewerState!!.currentPageIndex = 0
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.FirstPage,
                        contentDescription = "First",
                    )
                }
                IconButton(
                    enabled = isPdfLoaded && pdfViewerState!!.currentPageIndex > 0,
                    onClick = {
                        if (pdfViewerState!!.currentPageIndex > 0) {
                            pdfViewerState.currentPageIndex -= 1
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.ChevronLeft,
                        contentDescription = "Previous"
                    )
                }

                val pageInput = remember(pdfViewerState?.currentPageIndex) {
                    pdfViewerState?.let { "${it.currentPageIndex + 1}" } ?: ""
                }
                OutlinedTextField(
                    enabled = isPdfLoaded,
                    value = pageInput,
                    onValueChange = { value ->
                        val pageNumber = value.toIntOrNull()

                        if (isPdfLoaded && pageNumber != null && pageNumber in 1..pdfViewerState!!.pdDocument.numberOfPages) {
                            pdfViewerState.currentPageIndex = pageNumber - 1
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
                    enabled = isPdfLoaded && pdfViewerState!!.currentPageIndex + 1 < pdfViewerState.pdDocument.numberOfPages,
                    onClick = {
                        if (pdfViewerState!!.currentPageIndex < pdfViewerState.pdDocument.numberOfPages - 1) {
                            pdfViewerState.currentPageIndex += 1
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = "Next"
                    )
                }
                IconButton(
                    enabled = isPdfLoaded && pdfViewerState!!.currentPageIndex + 1 < pdfViewerState.pdDocument.numberOfPages,
                    onClick = {
                        pdfViewerState!!.currentPageIndex = pdfViewerState.pdDocument.numberOfPages - 1
                    }
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
                    text = "Zoom",
                    style = MaterialTheme.typography.bodySmall
                )

                Slider(
                    enabled = isPdfLoaded,
                    value = zoom,
                    onValueChange = { zoomLevel ->
                        pdfViewerState!!.zoom = zoomLevel
                    },
                    valueRange = 0.5f..3.0f,
                    modifier = Modifier.width(100.dp)
                )
            }
        }
    }
}

@Composable
fun PdfViewerContent(
    pdfViewerState: PdfViewerState?,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current.density
    val verticalScrollState = rememberScrollState()

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 20.dp)
            .then(modifier),
        shape = MaterialTheme.shapes.extraSmall,
        contentColor = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize().run {
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
                            val image = remember(renderer, state.zoom, density) {
                                renderer.renderImage(index, density * state.zoom)
                            }

                            Image(
                                painter = image.toPainter(),
                                contentDescription = "Page: $index",
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                } else {
                    val image = remember(renderer, state.currentPageIndex, state.zoom, density) {
                        renderer.renderImage(state.currentPageIndex, density * state.zoom)
                    }

                    Image(
                        painter = image.toPainter(),
                        contentDescription = "Index: ${state.currentPageIndex}"
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
    modifier: Modifier = Modifier
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
        pdfPath?.let { path ->
            val file = File(path)

            if (file.exists() && file.isFile && file.extension == "pdf") {
                pdfFile = file
            }
        }
    }

    DisposableEffect(pdfFile) {
        coroutineScope.launch(Dispatchers.IO) {
            pdfFile?.runCatching {
                pdfViewerState = PdfViewerState(Loader.loadPDF(this))
                onFileOpened(this)
            }
        }

        onDispose {
            pdfViewerState?.pdDocument?.close()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize().then(modifier),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainer
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
                }
            )

            PdfViewerContent(
                pdfViewerState = pdfViewerState,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
