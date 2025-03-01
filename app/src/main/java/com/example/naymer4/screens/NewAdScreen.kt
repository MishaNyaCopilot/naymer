package com.example.naymer4.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.naymer4.Announcement
import com.example.naymer4.AppViewModel
import com.example.naymer4.TimePickerDialog

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

    fun formatTime(hours: Int, minutes: Int): String {
        return String.format("%02d:%02d", hours, minutes)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(text = "Создание объявления", style = MaterialTheme.typography.titleMedium)

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
                        .menuAnchor()
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

            TextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Цена") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Если обычное объявление, показываем выбор рабочих дней
            if (!isHotAd) {
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

            Button(
                onClick = {
                    val newAnnouncement = Announcement(
                        title = title,
                        price = price,
                        category = selectedCategory,
                        geo = address,
                        workingTime = "${formatTime(startTimeState.hour, startTimeState.minute)} - " +
                                "${formatTime(endTimeState.hour, endTimeState.minute)}",
                        isHot = isHotAd
                    )
                    viewModel.addAd(newAnnouncement)
                    navController.navigate("adsList") {
                        popUpTo("adsList") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Разместить объявление")
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}