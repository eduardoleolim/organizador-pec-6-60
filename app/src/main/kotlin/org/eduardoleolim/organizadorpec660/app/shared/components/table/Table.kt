package org.eduardoleolim.organizadorpec660.app.shared.components.table

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.IconButtonDefaults.iconButtonColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class SearchValues(
    val search: String,
    val pageNumber: Int,
    val pageSize: Int,
    val orderType: String?,
    val orderBy: String?
)

class TableColumn<T>(
    val name: String = "",
    val width: Dp = 100.dp,
    val formatter: (@Composable (T) -> Unit) = { Text(it.toString()) }
)

@Composable
private fun <T> TableRow(columns: List<TableColumn<T>>, item: T) {
    Row {
        columns.forEach {
            Column(modifier = Modifier.width(it.width)) {
                it.formatter(item)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TableHeader(
    pageSizes: List<Int> = listOf(10, 25, 50, 100),
    searchValues: SearchValues,
    onSearchValuesChange: (SearchValues) -> Unit
) {
    var expandedPageSize by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
    ) {
        Box {
            DropdownMenu(
                expanded = expandedPageSize,
                onDismissRequest = { expandedPageSize = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            ) {
                pageSizes.forEach { pageSize ->
                    DropdownMenuItem(
                        text = {
                            Text(text = pageSize.toString(), color = MaterialTheme.colorScheme.onSurface)
                        },
                        onClick = {
                            onSearchValuesChange(searchValues.copy(pageSize = pageSize))
                            expandedPageSize = false
                        }
                    )
                }
            }
            Button(
                onClick = { expandedPageSize = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                contentPadding = PaddingValues(8.dp),
                elevation = ButtonDefaults.elevatedButtonElevation(0.dp, 0.dp, 0.dp)
            ) {
                Text(text = "Pág: ${searchValues.pageSize}", fontSize = 14.sp)
                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
            }
        }

        Column(
            horizontalAlignment = Alignment.End,
        ) {
            SearchBar(
                query = searchValues.search,
                onQueryChange = { onSearchValuesChange(searchValues.copy(search = it)) },
                onSearch = { println("Search") },
                active = false,
                onActiveChange = {},
                placeholder = { Text(text = "Buscar") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        modifier = Modifier.size(20.dp),
                    )
                },
                trailingIcon = {
                    if (searchValues.search.isNotEmpty()) {
                        IconButton(
                            onClick = { onSearchValuesChange(searchValues.copy(search = "")) },
                            modifier = Modifier
                                .pointerHoverIcon(PointerIcon.Default)
                                .size(20.dp),
                            colors = iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Limpiar",
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                },
                content = {}
            )
        }
    }
}

@Composable
fun TableFooter(
    totalRecords: Int,
    searchValues: SearchValues,
    onSearchValuesChange: (SearchValues) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.End
    ) {
        val pageNumber = searchValues.pageNumber
        val pageSize = searchValues.pageSize
        val totalPages =
            if (totalRecords % pageSize == 0) totalRecords / pageSize else (totalRecords / pageSize) + 1

        Text(
            text = "Pág: $pageNumber de $totalPages",
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(end = 8.dp)
        )

        IconButton(
            onClick = {
                if (pageNumber > 1) {
                    onSearchValuesChange(searchValues.copy(pageNumber = pageNumber - 1))
                }
            },
            enabled = pageNumber > 1,
            modifier = Modifier
                .size(30.dp)
                .padding(2.dp),
            colors = iconButtonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "Página anterior",
                modifier = Modifier.size(20.dp),
            )
        }

        IconButton(
            onClick = {
                if (pageNumber < totalPages) {
                    onSearchValuesChange(searchValues.copy(pageNumber = pageNumber + 1))
                }
            },
            enabled = pageNumber < totalPages,
            modifier = Modifier
                .size(30.dp)
                .padding(2.dp),
            colors = iconButtonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Próxima página",
                modifier = Modifier.size(20.dp),
            )
        }
    }
}


@Composable
fun <T> Table(
    columns: List<TableColumn<T>>,
    pageSizes: List<Int> = listOf(10, 25, 50, 100),
    onSearchRequest: (searchValues: SearchValues, updateData: (List<T>, Int) -> Unit) -> Unit
) {
    if (pageSizes.isEmpty()) throw IllegalArgumentException("pageSizes must not be empty")

    var searchValues by remember { mutableStateOf(SearchValues("", 1, pageSizes[0], null, null)) }
    var records by remember { mutableStateOf(emptyList<T>()) }
    var totalRecords by remember { mutableStateOf(0) }

    fun loadRecords(values: List<T>, total: Int) {
        records = values
        totalRecords = total
    }

    LaunchedEffect(searchValues) {
        onSearchRequest(searchValues, ::loadRecords)
    }

    Column(
        modifier = Modifier
            .padding(8.dp)
            .animateContentSize()
    ) {
        TableHeader(
            pageSizes = pageSizes,
            searchValues = searchValues,
            onSearchValuesChange = { searchValues = it }
        )

        Column {
            Row {
                columns.forEach {
                    Column(modifier = Modifier.width(it.width)) {
                        Text(
                            text = it.name,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Divider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                thickness = 1.dp
            )

            for (record in records) {
                TableRow(columns = columns, item = record)
            }
        }

        TableFooter(
            totalRecords = totalRecords,
            searchValues = searchValues,
            onSearchValuesChange = { searchValues = it }
        )
    }
}
