package org.eduardoleolim.organizadorpec660.app.shared.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LastPage
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.seanproctor.datatable.BasicDataTable
import com.seanproctor.datatable.DataColumn
import com.seanproctor.datatable.DataTableScope
import com.seanproctor.datatable.material3.Material3CellContentProvider
import com.seanproctor.datatable.paging.PaginatedDataTableState
import com.seanproctor.datatable.paging.rememberPaginatedDataTableState
import org.eduardoleolim.organizadorpec660.app.generated.resources.Res
import org.eduardoleolim.organizadorpec660.app.generated.resources.table_pagination
import org.eduardoleolim.organizadorpec660.app.generated.resources.table_search
import org.eduardoleolim.organizadorpec660.app.generated.resources.table_show_items
import org.jetbrains.compose.resources.stringResource
import kotlin.math.min

var PaginatedDataTableState.sortColumnIndex by mutableStateOf<Int?>(null)

var PaginatedDataTableState.sortAscending by mutableStateOf(true)

fun PaginatedDataTableState.reset(pageSize: Int = 10) {
    this.sortColumnIndex = null
    this.sortAscending = true
    this.pageIndex = 0
    this.pageSize = pageSize
    this.count = 0
}

@Composable
fun PaginatedDataTable(
    total: Int,
    value: String,
    onValueChange: (String) -> Unit,
    delayMillis: Long = 500L,
    pageSizes: List<Int> = listOf(10, 25, 50, 100),
    columns: List<DataColumn>,
    modifier: Modifier = Modifier,
    separator: @Composable (rowIndex: Int) -> Unit = { HorizontalDivider() },
    header: @Composable RowScope.() -> Unit = {},
    headerHeight: Dp = 56.dp,
    rowHeight: Dp = 52.dp,
    horizontalPadding: Dp = 16.dp,
    verticalPadding: Dp = 16.dp,
    onSearch: (search: String, pageIndex: Int, pageSize: Int, sortBy: Int?, isAscending: Boolean) -> Unit,
    state: PaginatedDataTableState = rememberPaginatedDataTableState(pageSizes.first()),
    content: DataTableScope.() -> Unit
) {
    var size by remember { mutableStateOf(Size.Zero) }

    value.useDebounce(delayMillis) {
        state.pageIndex = 0
        onSearch(value, state.pageIndex, state.pageSize, state.sortColumnIndex, state.sortAscending)
    }

    LaunchedEffect(state.pageIndex, state.pageSize, state.sortColumnIndex, state.sortAscending) {
        if (state.pageIndex < 0) state.pageIndex = 0

        onSearch(value, state.pageIndex, state.pageSize, state.sortColumnIndex, state.sortAscending)
    }

    Column(
        modifier = Modifier.onGloballyPositioned { size = it.size.toSize() }
            .then(modifier)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = horizontalPadding, vertical = verticalPadding)
        ) {
            var expandedPageSize by remember { mutableStateOf(false) }

            Box {
                TextButton(
                    onClick = { expandedPageSize = true },
                    enabled = state.pageSize > 1,
                ) {
                    Text(
                        text = stringResource(Res.string.table_show_items, state.pageSize),
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

            Spacer(Modifier.weight(1f))

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = {
                    Text(
                        text = stringResource(Res.string.table_search),
                        minLines = 1
                    )
                },
                textStyle = MaterialTheme.typography.bodyMedium,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(Res.string.table_search)
                    )
                },
                singleLine = true,
                modifier = Modifier.width(250.dp),
                shape = MaterialTheme.shapes.extraLarge,
                trailingIcon = {
                    if (value.isNotEmpty()) {
                        IconButton(
                            onClick = { onValueChange("") },
                            enabled = value.isNotEmpty(),
                            modifier = Modifier.pointerHoverIcon(PointerIcon.Default)
                        ) {
                            Icon(Icons.Default.Clear, "Clear")
                        }
                    }
                }
            )
        }

        val stateVertical = rememberScrollState(0)
        val stateHorizontal = rememberScrollState(0)

        Column(
            modifier = Modifier.weight(1.0f)
                .padding(8.dp)
                .verticalScroll(stateVertical)
        ) {
            BasicDataTable(
                modifier = Modifier.fillMaxSize(),
                columns = columns,
                separator = separator,
                headerHeight = headerHeight,
                horizontalPadding = horizontalPadding,
                cellContentProvider = Material3CellContentProvider,
                sortColumnIndex = state.sortColumnIndex,
                sortAscending = state.sortAscending,
                content = {
                    state.count = total
                    content()
                }
            )
        }

        Row(
            modifier = Modifier.height(rowHeight)
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.End),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val start = min(state.pageIndex * state.pageSize + 1, state.count)
            val end = min(start + state.pageSize - 1, state.count)
            val pageCount = (state.count + state.pageSize - 1) / state.pageSize

            Text(stringResource(Res.string.table_pagination, start, end, state.count))
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
                Icon(Icons.AutoMirrored.Filled.LastPage, "Last")
            }
        }
    }
}
