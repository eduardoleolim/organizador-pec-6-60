package org.eduardoleolim.organizadorpec660.shared.composables

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp

@Composable
fun <T> SearchList(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onSearch: (search: String, page: Int, pageSize: Int) -> Unit,
    onSearchNextPage: (page: Int) -> Unit,
    page: Int,
    pageSize: Int,
    header: @Composable RowScope.() -> Unit = {},
    items: List<T>,
    itemKey: ((item: T) -> Any)? = null,
    contentType: (item: T) -> Any? = { null },
    itemContent: @Composable LazyItemScope.(item: T) -> Unit
) {
    val lazyListState = rememberLazyListState(0, 0)
    val paginatedRequest = remember {
        derivedStateOf {
            (lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -2) == items.size - 1
        }
    }

    LaunchedEffect(value, page, pageSize) {
        onSearch(value, page, pageSize)
    }

    LaunchedEffect(paginatedRequest.value) {
        if (paginatedRequest.value) {
            onSearchNextPage(page + 1)
        }
    }

    Column(
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState())
                    .weight(1f)
            ) {
                header()
            }

            Spacer(Modifier.width(16.dp))

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = {
                    Text("Buscar")
                },
                textStyle = MaterialTheme.typography.bodyLarge,
                leadingIcon = {
                    Icon(Icons.Default.Search, "Buscar")
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

        LazyColumn(
            state = lazyListState
        ) {
            items(items, itemKey, contentType, itemContent)
        }
    }
}
