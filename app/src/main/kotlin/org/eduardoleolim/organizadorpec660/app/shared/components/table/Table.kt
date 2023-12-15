package org.eduardoleolim.organizadorpec660.app.shared.components.table

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.IconButtonDefaults.iconButtonColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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


@Composable
fun <T> Table(
    columns: List<TableColumn<T>>,
    pageSizes: List<Int> = listOf(10, 25, 50, 100),
    onSearchRequest: (searchValues: SearchValues, updateData: (List<T>, Int) -> Unit) -> Unit
) {
    if (pageSizes.isEmpty()) throw IllegalArgumentException("pageSizes must not be empty")

    var searchValues by remember { mutableStateOf(SearchValues("", 1, pageSizes[0], null, null)) }
    var expandedPageSize by remember { mutableStateOf(false) }
    var records by remember { mutableStateOf(emptyList<T>()) }
    var totalRecords by remember { mutableStateOf(0) }

    val load = { values: List<T>, total: Int ->
        records = values
        totalRecords = total
    }

    LaunchedEffect(searchValues) {
        onSearchRequest(searchValues, load)
    }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(2f)
                    .padding(end = 8.dp)
            ) {
                OutlinedTextField(
                    value = searchValues.search,
                    onValueChange = {
                        searchValues = searchValues.copy(search = it, pageNumber = 1)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 8.dp),
                    label = { Text("Buscar") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Text
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                    )
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
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
                                searchValues = searchValues.copy(pageNumber = 1, pageSize = pageSize)
                                expandedPageSize = false
                            }
                        )
                    }
                }
                Button(
                    onClick = { expandedPageSize = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(6.dp),
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
        }

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
            records.forEach { TableRow(columns, it) }
        }

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
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = 8.dp)
            )

            IconButton(
                onClick = {
                    if (pageNumber > 1) {
                        searchValues = searchValues.copy(pageNumber = pageNumber - 1)
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
                        searchValues = searchValues.copy(pageNumber = pageNumber + 1)
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
}
