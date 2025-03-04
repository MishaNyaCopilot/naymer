package com.example.naymer4.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.naymer4.AnnouncementItemFigma
import com.example.naymer4.AppViewModel
import com.example.naymer4.SortOrder
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset

data class FilterCriteria(
    val categories: List<String>? = null,
    val location: String? = null,
    val priceFrom: Int? = null,
    val priceTo: Int? = null,
    val sortOrder: SortOrder = SortOrder.DEFAULT
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun NormalAdsScreen(viewModel: AppViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    var showFilterSheet by remember { mutableStateOf(false) }

    // Применяем фильтрацию из ViewModel и локальный поиск
    val normalAds = viewModel.filteredAds.filter { ad ->
        !ad.isHot && (
                ad.title.contains(searchQuery, ignoreCase = true) ||
                        ad.category.contains(searchQuery, ignoreCase = true)
                )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Строка поиска с иконкой Menu для вызова фильтров
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Поиск") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            modifier = Modifier.clickable { showFilterSheet = true }
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Искать"
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFEEE6F8),
                        unfocusedContainerColor = Color(0xFFEEE6F8),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = MaterialTheme.shapes.large,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Список объявлений
            LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
                items(normalAds) { ad ->
                    AnnouncementItemFigma(
                        announcement = ad,
                        onBookmarkToggle = { viewModel.toggleBookmark(ad) },
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        // Нижнее меню фильтров
        if (showFilterSheet) {
            ModalBottomSheet(
                onDismissRequest = { showFilterSheet = false },
                dragHandle = { BottomSheetDefaults.DragHandle() },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ) {
                // Передаем текущие фильтры из ViewModel
                FullScreenFilterMenu(
                    currentFilters = viewModel.filterCriteria,
                    onDismiss = { showFilterSheet = false },
                    onApplyFilters = { criteria ->
                        viewModel.applyFilters(criteria)
                        showFilterSheet = false
                    },
                    onResetFilters = {
                        viewModel.resetFilters()
                        showFilterSheet = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FullScreenFilterMenu(
    currentFilters: FilterCriteria,
    onDismiss: () -> Unit,
    onApplyFilters: (FilterCriteria) -> Unit,
    onResetFilters: () -> Unit
) {
    // Инициализируем состояние на основе текущих фильтров
    var selectedCategories by remember { mutableStateOf(currentFilters.categories ?: emptyList()) }
    var locationInput by remember { mutableStateOf(currentFilters.location ?: "") }
    var priceFromInput by remember { mutableStateOf(currentFilters.priceFrom?.toString() ?: "") }
    var priceToInput by remember { mutableStateOf(currentFilters.priceTo?.toString() ?: "") }
    var selectedSortOrder by remember { mutableStateOf(currentFilters.sortOrder) }
    var categoryMenuExpanded by remember { mutableStateOf(false) }

    val availableCategories = listOf("Программист", "Сантехник", "Электрик")

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Заголовок меню с добавленной кнопкой сброса
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Фильтры",
                    style = MaterialTheme.typography.titleMedium
                )

                TextButton(
                    onClick = onResetFilters,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Сбросить")
                }
            }

            // Выбор категорий с исправленным позиционированием выпадающего меню
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Category text with wrapping
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Категории:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )

                    if (selectedCategories.isEmpty()) {
                        Text(
                            text = "Не выбрано",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        // Display categories as wrapped Flow layout
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(0.9f),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            selectedCategories.forEach { category ->
                                CategoryChip(
                                    category = category,
                                    onRemove = {
                                        selectedCategories = selectedCategories - category
                                    }
                                )
                            }
                        }
                    }
                }

                // Add button with dropdown menu
                Box {
                    Button(
                        onClick = { categoryMenuExpanded = true },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text("Добавить")
                    }

                    // Правильно позиционированное меню
                    DropdownMenu(
                        expanded = categoryMenuExpanded,
                        onDismissRequest = { categoryMenuExpanded = false },
                        offset = DpOffset(0.dp, 0.dp)  // Можно настроить смещение, если нужно
                    ) {
                        availableCategories.forEach { category ->
                            DropdownMenuItem(
                                onClick = {
                                    selectedCategories = if (selectedCategories.contains(category))
                                        selectedCategories - category
                                    else
                                        selectedCategories + category
                                    // Не закрываем меню после выбора, чтобы можно было выбрать несколько категорий
                                },
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Checkbox(
                                            checked = selectedCategories.contains(category),
                                            onCheckedChange = null
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = category)
                                    }
                                }
                            )
                        }

                        // Добавляем кнопку "Готово" для закрытия меню
                        DropdownMenuItem(
                            onClick = { categoryMenuExpanded = false },
                            text = {
                                Text(
                                    text = "Готово",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }
                        )
                    }
                }
            }

            // Остальной код без изменений...
            // Фильтр по местоположению
            TextField(
                value = locationInput,
                onValueChange = { locationInput = it },
                label = { Text("Местоположение") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            // Фильтр по стоимости (от и до)
            Row(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    value = priceFromInput,
                    onValueChange = { priceFromInput = it },
                    label = { Text("Цена от") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp)
                )
                TextField(
                    value = priceToInput,
                    onValueChange = { priceToInput = it },
                    label = { Text("Цена до") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp)
                )
            }

            // Опции сортировки
            Text(
                text = "Сортировка",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 16.dp)
            )
            Column {
                listOf(
                    SortOrder.DEFAULT to "По умолчанию",
                    SortOrder.CHEAPER to "Дешевле",
                    SortOrder.EXPENSIVE to "Дороже",
                    SortOrder.DATE to "По дате"
                ).forEach { (order, label) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedSortOrder = order }
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = selectedSortOrder == order,
                            onClick = { selectedSortOrder = order }
                        )
                        Text(text = label)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Кнопки "Отмена" и "Применить"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Отмена")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    onApplyFilters(
                        FilterCriteria(
                            categories = selectedCategories.takeIf { it.isNotEmpty() },
                            location = locationInput.takeIf { it.isNotBlank() },
                            priceFrom = priceFromInput.toIntOrNull(),
                            priceTo = priceToInput.toIntOrNull(),
                            sortOrder = selectedSortOrder
                        )
                    )
                }) {
                    Text("Применить")
                }
            }
        }
    }
}

// New composable for displaying category chips
@Composable
fun CategoryChip(
    category: String,
    onRemove: () -> Unit
) {
    Surface(
        modifier = Modifier.padding(vertical = 4.dp),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Row(
            modifier = Modifier.padding(start = 8.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = category,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Удалить",
                    modifier = Modifier.size(12.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}