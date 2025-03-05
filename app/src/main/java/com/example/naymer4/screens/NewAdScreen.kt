package com.example.naymer4.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.naymer4.Announcement
import com.example.naymer4.AppViewModel
import com.example.naymer4.TimePickerDialog
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.items

//fun formatTime(hours: Int, minutes: Int): String {
//    return String.format("%02d:%02d", hours, minutes)
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewAdsScreen(
    navController: NavController,
    viewModel: AppViewModel,
    isHotAd: Boolean // параметр определяет тип объявления
) {
    var price by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var photoAttached by remember { mutableStateOf(false) }

    // Выбор категории
    var expanded by remember { mutableStateOf(false) }
    val categories = listOf("Программист", "Сантехник", "Электрик")
    var selectedCategory by remember { mutableStateOf(categories[0]) }

    // Состояния для выбора дней работы (только для обычного объявления)
    val daysOfWeek = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
    var selectedDays by remember { mutableStateOf(setOf<String>()) }

    // Состояния для выбора времени
    val startTimeState = rememberTimePickerState(is24Hour = true)
    val endTimeState = rememberTimePickerState(is24Hour = true)
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    val services = remember { mutableStateListOf<Pair<String, String>>() }
    var showAddServiceDialog by remember { mutableStateOf(false) }
    var newServiceName by remember { mutableStateOf("") }
    var newServicePrice by remember { mutableStateOf("") }

    // Add a state for showing error messages
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Create a reusable error message Composable
    @Composable
    fun ErrorMessageDisplay(message: String?) {
        message?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }
    }

    fun formatTime(hours: Int, minutes: Int): String {
        return String.format("%02d:%02d", hours, minutes)
    }

    // Функция для проверки и фильтрации ввода цены (только цифры)
    fun filterDigitsOnly(text: String): String {
        return text.filter { it.isDigit() }
    }

    // Диалог для добавления услуги
    if (showAddServiceDialog) {
        AlertDialog(
            onDismissRequest = { showAddServiceDialog = false },
            title = { Text("Добавить услугу") },
            text = {
                Column {
                    TextField(
                        value = newServiceName,
                        onValueChange = { newServiceName = it },
                        label = { Text("Название услуги") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                    Spacer(Modifier.height(8.dp))
                    TextField(
                        value = newServicePrice,
                        onValueChange = { newServicePrice = filterDigitsOnly(it) },
                        label = { Text("Стоимость") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newServiceName.isNotBlank() && newServicePrice.isNotBlank()) {
                            services.add(newServiceName to newServicePrice)
                            newServiceName = ""
                            newServicePrice = ""
                            showAddServiceDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Добавить")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showAddServiceDialog = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Отмена")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            // Изменение заголовка в зависимости от типа объявления
            Text(
                text = if (isHotAd) "Создание горячего объявления" else "Создание объявления",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Название объявления") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { photoAttached = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (photoAttached) "Фото прикреплено" else "Прикрепить фото")
            }
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Описание") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Выбор категории
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    value = selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Категория объявления") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category) },
                            onClick = {
                                selectedCategory = category
                                expanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Адрес") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Поле для ввода цены (только цифры)
            TextField(
                value = price,
                onValueChange = { price = filterDigitsOnly(it) },
                label = { Text("Цена") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                placeholder = { Text("Введите стоимость") }
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Если обычное объявление, показываем выбор рабочих дней
            if (!isHotAd) {

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Список услуг",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { showAddServiceDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Добавить услугу")
                }
                Spacer(modifier = Modifier.height(8.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.heightIn(max = 200.dp), // Ограничение высоты
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = services,
                        key = { (name, price) -> name + price }
                    ) { (name, price) ->
                        Modifier
                            .fillMaxWidth()
                        Surface(
                            modifier = Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null), // Правильный модификатор анимации
                            color = MaterialTheme.colorScheme.surface,
                            shape = MaterialTheme.shapes.medium,
                            shadowElevation = 2.dp
                        ) {
                            Box(modifier = Modifier.padding(12.dp)) {
                                Column {
                                    Text(
                                        text = name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "$price ₽",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.secondary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                IconButton(
                                    onClick = { services.remove(name to price) },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Удалить",
                                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Дни работы", style = MaterialTheme.typography.titleMedium)
                Row(modifier = Modifier.fillMaxWidth()) {
                    daysOfWeek.forEach { day ->
                        val isSelected = selectedDays.contains(day)
                        Column(
                            modifier = Modifier.padding(3.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            androidx.compose.material3.Checkbox(
                                checked = isSelected,
                                onCheckedChange = {
                                    selectedDays =
                                        if (it) selectedDays + day else selectedDays - day
                                }
                            )
                            Text(text = day)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Выбор времени работы
            Text(
                text = "Время работы:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Button(
                onClick = { showStartTimePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Начало: ${formatTime(startTimeState.hour, startTimeState.minute)}")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { showEndTimePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Окончание: ${formatTime(endTimeState.hour, endTimeState.minute)}")
            }

            if (showStartTimePicker) {
                TimePickerDialog(
                    onCancel = { showStartTimePicker = false },
                    onConfirm = { showStartTimePicker = false }
                ) {
                    TimePicker(state = startTimeState)
                }
            }
            if (showEndTimePicker) {
                TimePickerDialog(
                    onCancel = { showEndTimePicker = false },
                    onConfirm = { showEndTimePicker = false }
                ) {
                    TimePicker(state = endTimeState)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Modify the button onClick to include error handling and Supabase saving

            ErrorMessageDisplay(errorMessage)

            Button(
                onClick = {
                    // Validate required fields
                    when {
                        title.isBlank() -> {
                            errorMessage = "Название объявления не может быть пустым"
                            return@Button
                        }
                        price.isBlank() -> {
                            errorMessage = "Цена не может быть пустой"
                            return@Button
                        }
                        address.isBlank() -> {
                            errorMessage = "Адрес не может быть пустым"
                            return@Button
                        }
                        else -> {
                            // Clear any previous error messages
                            errorMessage = null

                            // Prepare working time string
                            val workTime = formatTime(startTimeState.hour, startTimeState.minute) + " - " +
                                    formatTime(endTimeState.hour, endTimeState.minute)

                            // Prepare additional data for hot/normal ads
                            val additionalData = if (!isHotAd) {
                                // For normal ads, include services and working days
                                mapOf(
                                    "services" to services.map { "${it.first}: ${it.second} ₽" }.joinToString("; "),
                                    "working_days" to selectedDays.joinToString(", ")
                                )
                            } else {
                                emptyMap()
                            }

                            val newAnnouncement = Announcement(
                                title = title,
                                price = "$price ₽", // Add currency
                                category = selectedCategory,
                                geo = address,
                                workingTime = workTime,
                                isHot = isHotAd
                            )

                            // Call ViewModel method to add ad
                            viewModel.addAd(newAnnouncement)

                            // Navigate back to ads list
                            navController.navigate("adsList") {
                                popUpTo("adsList") { inclusive = true }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Разместить объявление")
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        // Display error message if exists
//        errorMessage?.let { error ->
//            Text(
//                text = error,
//                color = MaterialTheme.colorScheme.error,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 8.dp)
//            )
//        }
    }
}