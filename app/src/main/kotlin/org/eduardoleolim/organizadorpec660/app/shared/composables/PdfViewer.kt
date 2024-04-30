package org.eduardoleolim.organizadorpec660.app.shared.composables

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LastPage
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.FirstPage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.pdfbox.Loader
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.PDFRenderer
import org.eduardoleolim.organizadorpec660.app.shared.theme.AppTheme
import java.awt.image.BufferedImage
import java.io.File
import javax.swing.JFileChooser
import javax.swing.JFrame
import javax.swing.filechooser.FileNameExtensionFilter

data class PdfViewerState(val pdDocument: PDDocument) {
    var currentPageIndex by mutableStateOf(0)
}

@Composable
fun PdfViewerTopBar(
    pdfViewerState: PdfViewerState?,
    modifier: Modifier = Modifier,
    onOpenFileRequest: () -> Unit
) {
    val context = rememberCoroutineScope()
    var openButtonEnabled by remember { mutableStateOf(true) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 10.dp)
            .then(modifier),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceContainerHighest
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
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

            IconButton(
                enabled = pdfViewerState != null && pdfViewerState.currentPageIndex > 0,
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
                enabled = pdfViewerState != null && pdfViewerState.currentPageIndex > 0,
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
            IconButton(
                enabled = pdfViewerState != null && pdfViewerState.currentPageIndex + 1 < pdfViewerState.pdDocument.numberOfPages,
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
                enabled = pdfViewerState != null && pdfViewerState.currentPageIndex + 1 < pdfViewerState.pdDocument.numberOfPages,
                onClick = {
                    pdfViewerState!!.currentPageIndex = pdfViewerState.pdDocument.numberOfPages - 1
                },
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.LastPage,
                    contentDescription = "Last"
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
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 20.dp)
            .then(modifier),
        shape = MaterialTheme.shapes.extraSmall,
        contentColor = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        val verticalScrollState = rememberScrollState()
        val horizontalScrollState = rememberScrollState()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(verticalScrollState)
                .horizontalScroll(horizontalScrollState)
        ) {
            pdfViewerState?.let {
                val renderer = remember(pdfViewerState.pdDocument) { PDFRenderer(it.pdDocument) }
                var image by remember { mutableStateOf<BufferedImage?>(null) }
                val density = LocalDensity.current.density

                LaunchedEffect(pdfViewerState.currentPageIndex) {
                    image = renderer.renderImage(pdfViewerState.currentPageIndex, density)
                }

                Canvas(
                    modifier = Modifier
                ) {
                    image?.let {
                        drawImage(image = it.toComposeImageBitmap())
                    }
                }
            }
        }
    }
}

@Composable
fun PdfViewer(
    modifier: Modifier = Modifier,
    onFileOpened: (File) -> Unit = {}
) {
    var pdfDocument: File? by remember { mutableStateOf(null) }
    var pdfViewerState by remember { mutableStateOf<PdfViewerState?>(null) }
    val fileChooser = remember {
        JFileChooser().apply {
            isMultiSelectionEnabled = false
            fileFilter = FileNameExtensionFilter("Documentos PDF (*.pdf)", "pdf")
        }
    }

    DisposableEffect(pdfDocument) {
        pdfDocument?.let {
            pdfViewerState = PdfViewerState(Loader.loadPDF(it))
            onFileOpened(it)
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
                onOpenFileRequest = {
                    val result = fileChooser.showOpenDialog(null as JFrame?)

                    if (result == JFileChooser.APPROVE_OPTION) {
                        pdfDocument = fileChooser.selectedFile
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

@Preview
@Composable
fun Preview() {
    AppTheme(
        darkTheme = true
    ) {
        PdfViewer(
            modifier = Modifier.padding(20.dp)
        )
    }
}
