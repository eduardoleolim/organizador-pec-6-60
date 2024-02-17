package org.eduardoleolim.organizadorpec660.app.shared.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.seanproctor.datatable.DataColumn
import com.seanproctor.datatable.DataTableScope
import com.seanproctor.datatable.material3.Material3CellContentProvider
import com.seanproctor.datatable.paging.BasicPaginatedDataTable
import com.seanproctor.datatable.paging.PaginatedDataTableState
import com.seanproctor.datatable.paging.rememberPaginatedDataTableState
import kotlin.math.min

@Composable
fun PaginatedDataTable(
    value: String,
    onValueChange: (String) -> Unit,
    pageSizes: List<Int> = listOf(10, 25, 50, 100),
    columns: List<DataColumn>,
    modifier: Modifier = Modifier,
    separator: @Composable (rowIndex: Int) -> Unit = { Divider() },
    header: @Composable RowScope.() -> Unit = {},
    headerHeight: Dp = 56.dp,
    rowHeight: Dp = 52.dp,
    horizontalPadding: Dp = 16.dp,
    verticalPadding: Dp = 16.dp,
    sortColumnIndex: Int? = null,
    sortAscending: Boolean = true,
    onSearch: (search: String, pageIndex: Int, pageSize: Int, sortBy: Int?, isAscending: Boolean) -> Unit,
    state: PaginatedDataTableState = rememberPaginatedDataTableState(pageSizes.first()),
    content: DataTableScope.() -> Unit,
) {
    LaunchedEffect(value, state.pageIndex, state.pageSize, sortColumnIndex, sortAscending) {
        if (state.pageIndex < 0) state.pageIndex = 0

        onSearch(value, state.pageIndex, state.pageSize, sortColumnIndex, sortAscending)
    }

    LaunchedEffect(value) {
        state.pageIndex = 0
    }

    Column(
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = horizontalPadding,
                    vertical = verticalPadding
                )
        ) {
            var expandedPageSize by remember { mutableStateOf(false) }

            Box {
                TextButton(
                    onClick = { expandedPageSize = true },
                    enabled = state.pageSize > 1,
                ) {
                    Text(
                        text = "Mostrar ${state.pageSize} items",
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Icon(
                        imageVector = if (expandedPageSize) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = "Expand"
                    )
                }
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
                                expandedPageSize = false
                                state.pageSize = pageSize
                                state.pageIndex = 0
                            }
                        )
                    }
                }
            }

            header()

            Spacer(modifier = Modifier.weight(1f))

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = {
                    Text(
                        text = "Buscar",
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize
                    )
                },
                textStyle = MaterialTheme.typography.bodyMedium,
                leadingIcon = { Icon(Icons.Default.Search, "Buscar") },
                singleLine = true,
                modifier = Modifier.width(250.dp),
                shape = MaterialTheme.shapes.extraLarge,
                trailingIcon = {
                    if (value.isNotEmpty()) {
                        IconButton(
                            onClick = { onValueChange("") },
                            enabled = value.isNotEmpty(),
                        ) {
                            Icon(Icons.Default.Clear, "Clear")
                        }
                    }
                }
            )
        }

        BasicPaginatedDataTable(
            columns = columns,
            modifier = Modifier.fillMaxSize(),
            separator = separator,
            headerHeight = headerHeight,
            horizontalPadding = horizontalPadding,
            state = state,
            footer = {
                Row(
                    modifier = Modifier.height(rowHeight).padding(horizontal = 16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.End),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val start = min(state.pageIndex * state.pageSize + 1, state.count)
                    val end = min(start + state.pageSize - 1, state.count)
                    val pageCount = (state.count + state.pageSize - 1) / state.pageSize

                    Text("$start-$end de ${state.count}")
                    IconButton(
                        onClick = { state.pageIndex = 0 },
                        enabled = state.pageIndex > 0,
                    ) {
                        Icon(Icons.Default.FirstPage, "First")
                    }
                    IconButton(
                        onClick = { state.pageIndex-- },
                        enabled = state.pageIndex > 0,
                    ) {
                        Icon(Icons.Default.ChevronLeft, "Previous")
                    }
                    IconButton(
                        onClick = { state.pageIndex++ },
                        enabled = state.pageIndex < pageCount - 1
                    ) {
                        Icon(Icons.Default.ChevronRight, "Next")
                    }
                    IconButton(
                        onClick = { state.pageIndex = pageCount - 1 },
                        enabled = state.pageIndex < pageCount - 1
                    ) {
                        Icon(Icons.Default.LastPage, "Last")
                    }
                }
            },
            cellContentProvider = Material3CellContentProvider,
            sortColumnIndex = sortColumnIndex,
            sortAscending = sortAscending,
            content = content
        )
    }
}
