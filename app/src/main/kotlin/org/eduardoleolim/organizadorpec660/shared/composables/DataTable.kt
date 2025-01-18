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

package org.eduardoleolim.organizadorpec660.shared.composables

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.v2.ScrollbarAdapter
import androidx.compose.foundation.v2.maxScrollOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LastPage
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.seanproctor.datatable.*
import com.seanproctor.datatable.material3.Material3CellContentProvider
import com.seanproctor.datatable.paging.PaginatedDataTableState
import com.seanproctor.datatable.paging.rememberPaginatedDataTableState
import org.eduardoleolim.organizadorpec660.shared.resources.Res
import org.eduardoleolim.organizadorpec660.shared.resources.table_pagination
import org.eduardoleolim.organizadorpec660.shared.resources.table_search
import org.eduardoleolim.organizadorpec660.shared.resources.table_show_items
import org.jetbrains.compose.resources.stringResource
import kotlin.math.min
import kotlin.math.roundToInt

var PaginatedDataTableState.sortColumnIndex by mutableStateOf<Int?>(null)

var PaginatedDataTableState.sortAscending by mutableStateOf(true)

@Composable
fun PaginatedDataTable(
    total: Int,
    value: String,
    onValueChange: (String) -> Unit,
    pageSizes: List<Int> = listOf(10, 25, 50, 100),
    columns: List<DataColumn>,
    modifier: Modifier = Modifier,
    separator: @Composable () -> Unit = { HorizontalDivider() },
    header: @Composable RowScope.() -> Unit = {},
    headerHeight: Dp = 58.dp,
    rowHeight: Dp = 52.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 18.dp),
    onSearch: (search: String, pageIndex: Int, pageSize: Int, sortBy: Int?, isAscending: Boolean) -> Unit,
    state: PaginatedDataTableState = rememberPaginatedDataTableState(pageSizes.first()),
    content: DataTableScope.() -> Unit
) {
    val tableState = rememberDataTableState()
    val horizontalScrollBarAdapter = rememberScrollbarAdapter(tableState.horizontalScrollState)
    val verticalScrollBarAdapter = rememberScrollbarAdapter(tableState.verticalScrollState)

    LaunchedEffect(state.pageIndex, state.pageSize, state.sortColumnIndex, state.sortAscending) {
        onSearch(value, state.pageIndex, state.pageSize, state.sortColumnIndex, state.sortAscending)
    }

    LaunchedEffect(horizontalScrollBarAdapter.maxScrollOffset) {
        if (horizontalScrollBarAdapter.maxScrollOffset == 0.0) {
            horizontalScrollBarAdapter.scrollTo(0.0)
        }
    }

    LaunchedEffect(verticalScrollBarAdapter.maxScrollOffset) {
        if (verticalScrollBarAdapter.maxScrollOffset == 0.0) {
            verticalScrollBarAdapter.scrollTo(0.0)
        }
    }

    Column(
        modifier = Modifier.then(modifier)
    ) {
        PaginatedDataTableHeader(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            value = value,
            onValueChange = onValueChange,
            state = state,
            pageSizes = pageSizes,
            content = header
        )

        Box(
            modifier = Modifier
                .weight(1.0f)
                .padding(8.dp)
        ) {
            BasicDataTable(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        end = if (verticalScrollBarAdapter.maxScrollOffset > 0) 8.dp else 0.dp,
                        bottom = if (horizontalScrollBarAdapter.maxScrollOffset > 0) 8.dp else 0.dp
                    ),
                state = tableState,
                columns = columns,
                separator = separator,
                headerHeight = headerHeight,
                contentPadding = contentPadding,
                cellContentProvider = Material3CellContentProvider,
                sortColumnIndex = state.sortColumnIndex,
                sortAscending = state.sortAscending,
                content = {
                    state.count = total
                    content()
                }
            )

            HorizontalScrollbar(
                adapter = horizontalScrollBarAdapter,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(
                        end = if (verticalScrollBarAdapter.maxScrollOffset > 0) 8.dp else 0.dp
                    )
            )

            VerticalScrollbar(
                adapter = verticalScrollBarAdapter,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .padding(
                        bottom = if (horizontalScrollBarAdapter.maxScrollOffset > 0) 8.dp else 0.dp
                    )
            )
        }

        Row(
            modifier = Modifier
                .height(rowHeight)
                .padding(horizontal = 18.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(18.dp, alignment = Alignment.End),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val start = with(state) { min(pageIndex * pageSize + 1, count) }
            val end = with(state) { min(start + pageSize - 1, count) }
            val pageCount = with(state) { (count + pageSize - 1) / pageSize }

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

@Composable
private fun PaginatedDataTableHeader(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    state: PaginatedDataTableState,
    pageSizes: List<Int>,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val horizontalScrollState = rememberScrollState(0)
        val horizontalScrollbarAdapter = rememberScrollbarAdapter(horizontalScrollState)

        Box(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .horizontalScroll(horizontalScrollState)
                    .padding(
                        bottom = if (horizontalScrollbarAdapter.maxScrollOffset > 0) 8.dp else 0.dp
                    )
            ) {
                SelectPageSize(
                    state = state,
                    pageSizes = pageSizes
                )

                content()
            }

            HorizontalScrollbar(
                adapter = horizontalScrollbarAdapter,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
            )
        }

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = {
                Text(
                    text = stringResource(Res.string.table_search),
                    maxLines = 1
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
            modifier = Modifier.widthIn(max = 250.dp),
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
}

@Composable
private fun SelectPageSize(state: PaginatedDataTableState, pageSizes: List<Int>) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        TextButton(
            onClick = { expanded = true },
            enabled = state.pageSize > 1,
        ) {
            Text(
                text = stringResource(Res.string.table_show_items, state.pageSize),
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Icon(
                imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                contentDescription = "Expand"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            pageSizes.forEach { pageSize ->
                DropdownMenuItem(
                    text = {
                        Text(text = pageSize.toString(), color = MaterialTheme.colorScheme.onSurface)
                    },
                    onClick = {
                        expanded = false
                        state.pageSize = pageSize
                        state.pageIndex = 0
                    }
                )
            }
        }
    }
}

@Composable
fun rememberScrollbarAdapter(scrollState: DataTableScrollState): ScrollbarAdapter {
    return remember(scrollState) { DataTableScrollbarAdapter(scrollState) }
}

internal class DataTableScrollbarAdapter(
    private val scrollState: DataTableScrollState
) : ScrollbarAdapter {
    override val scrollOffset: Double get() = scrollState.offset.toDouble()

    override suspend fun scrollTo(scrollOffset: Double) {
        scrollState.scrollTo(scrollOffset.roundToInt())
    }

    override val contentSize: Double
        get() = scrollState.totalSize.toDouble()

    override val viewportSize: Double
        get() = scrollState.viewportSize.toDouble()
}
