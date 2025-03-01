package com.example.naymer4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.window.Dialog
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CardColors
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.TextFieldDefaults
//import androidx.compose.material3.TimePicker
//import androidx.compose.material3.TimePickerDefaults
//import androidx.compose.material3.TimePickerState
//import androidx.compose.material3.rememberTimePickerState
//import androidx.compose.material.icons.filled.Book
//import androidx.compose.material.icons.filled.Bookmark
//import androidx.compose.material.icons.filled.Person
//import androidx.compose.material.icons.filled.Home
//import androidx.compose.material.icons.filled.Whatshot
//import androidx.compose.material3.TextFieldColors
//import android.app.TimePickerDialog

data class Announcement(
    val title: String,
    val price: String,
    val category: String,
    val geo: String,
    val workingTime: String,
    val isHot: Boolean = false,
    val isUserAd: Boolean = false
)

class AppViewModel : ViewModel() {
    val allAds = mutableStateListOf<Announcement>()

    init {
        // Загрузка мок-данных из отдельного файла
        allAds.addAll(MockData.initialAnnouncements)
    }

    fun addAd(announcement: Announcement) {
        allAds.add(announcement.copy(isUserAd = true))
    }

    fun removeAd(announcement: Announcement) {
        allAds.remove(announcement)
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                MyApp()
            }
        }
    }
}

@Composable
fun TimePickerDialog(
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    Dialog(onDismissRequest = onCancel) {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                content()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Отмена")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("OK")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp() {
    val navController = rememberNavController()
    val viewModel: AppViewModel = viewModel()
    var showAdTypeSheet by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                navController = navController,
                onCreateClick = { showAdTypeSheet = true }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "adsList",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("adsList") { NormalAdsScreen(viewModel = viewModel) }
            composable("hotAds") { HotAdsScreen(viewModel = viewModel) } // Добавлено для горячих объявлений
            composable("createNormalAd") { NewAdsScreen(navController = navController, viewModel = viewModel, isHotAd = false) }
            composable("createHotAd") { NewAdsScreen(navController = navController, viewModel = viewModel, isHotAd = true) }
            composable("bookmarks") { /* ваш экран закладок */ }
            composable("profile") { ProfileScreen(viewModel = viewModel) }
        }
    }

    // Модальное нижнее меню с выбором типа объявления
    if (showAdTypeSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAdTypeSheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Выберите тип объявления",
                    style = MaterialTheme.typography.titleMedium
                )
                // Кнопка для обычного объявления
                Button(
                    onClick = {
                        showAdTypeSheet = false
                        navController.navigate("createNormalAd")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text("Обычное объявление")
                }
                // Кнопка для горячего объявления
                Button(
                    onClick = {
                        showAdTypeSheet = false
                        navController.navigate("createHotAd")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text("Горячее объявление")
                }
            }
        }
    }
}

@Composable
fun BottomNavBar(navController: NavController, onCreateClick: () -> Unit) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    // Общие цвета для элементов навигации
    val itemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = primaryColor,
        selectedTextColor = primaryColor,
        unselectedIconColor = onSurfaceColor.copy(alpha = 0.6f),
        unselectedTextColor = onSurfaceColor.copy(alpha = 0.6f)
    )

    // Разбиваем элементы навигации на левую и правую части
    val leftNavItems = listOf(
        Triple("adsList", "Main", Icons.Filled.Home),
        Triple("hotAds", "Hot Ads", Icons.Filled.Whatshot)
    )
    val rightNavItems = listOf(
        Triple("bookmarks", "Bookmarks", Icons.Default.Bookmarks),
        Triple("profile", "Profile", Icons.Filled.Person)
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = primaryColor,
        tonalElevation = 8.dp,
        modifier = Modifier.height(100.dp)
    ) {
        // Левые элементы навигации
        leftNavItems.forEach { (route, label, icon) ->
            NavigationBarItem(
                icon = { Icon(imageVector = icon, contentDescription = label) },
                label = { Text(text = label) },
                selected = currentRoute == route,
                onClick = { if (currentRoute != route) navController.navigate(route) },
                colors = itemColors
            )
        }

        // Кнопка создания в центре
        NavigationBarItem(
            icon = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(primaryColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Create",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            label = { Text("") },
            selected = false,
            onClick = onCreateClick,
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
        )

        // Правые элементы навигации
        rightNavItems.forEach { (route, label, icon) ->
            NavigationBarItem(
                icon = { Icon(imageVector = icon, contentDescription = label) },
                label = { Text(text = label) },
                selected = currentRoute == route,
                onClick = { if (currentRoute != route) navController.navigate(route) },
                colors = itemColors
            )
        }
    }
}

// Add missing NavigationBarItemDefaults to your imports
object NavigationBarItemDefaults {
    @Composable
    fun colors(
        selectedIconColor: Color = Color.Unspecified,
        selectedTextColor: Color = Color.Unspecified,
        unselectedIconColor: Color = Color.Unspecified,
        unselectedTextColor: Color = Color.Unspecified,
        indicatorColor: Color = Color.Unspecified
    ): NavigationBarItemColors {
        return androidx.compose.material3.NavigationBarItemDefaults.colors(
            selectedIconColor = selectedIconColor,
            selectedTextColor = selectedTextColor,
            indicatorColor = indicatorColor
        )
    }
}

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
                Text("Создать объявление")
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun AnnouncementItemFigma(announcement: Announcement, isHot: Boolean = false,
                          containerColor: Color = Color(0xFFCBC3C3)
) {
    Card(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isHot) Color(0xFFFFEBEE) else containerColor
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Placeholder image with shapes
            Box(
                modifier = Modifier
                    .size(65.dp)
                    .background(Color.LightGray)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Triangle shape
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(Color.Gray)
                            .clip(TriangleShape())
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    // Square shape
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(Color.Gray)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    // Circle shape
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(Color.Gray)
                            .clip(CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Text content
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = announcement.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black
                    )
                    if (isHot) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Whatshot,
                                contentDescription = "Hot",
                                tint = Color.Red,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Осталось 12 ч.",
                                color = Color.Red,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }

                Text(
                    text = announcement.price,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )
                Text(
                    text = announcement.category,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )
                Text(
                    text = announcement.geo,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )

                // Time information
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = "Time",
                        modifier = Modifier.size(16.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (announcement.isHot) "Время действия: ${announcement.workingTime}"
                        else "Время работы: ${announcement.workingTime}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun NormalAdsScreen(viewModel: AppViewModel) {
    val normalAds = viewModel.allAds.filter { !it.isHot }
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Search bar with rounded corners
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
                        imageVector = Icons.Default.Menu, // Replace with search icon
                        contentDescription = "Поиск"
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search, // Replace with magnifying glass icon
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

        // Список обычных объявлений
        LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
            items(normalAds.filter {
                it.title.contains(searchQuery, true) ||
                        it.category.contains(searchQuery, true)
            }) { ad ->
                AnnouncementItemFigma(
                    announcement = ad,
                    // modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun HotAdsScreen(viewModel: AppViewModel) {
    val hotAds = viewModel.allAds.filter { it.isHot }

    LazyColumn(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        items(hotAds) { ad ->
            AnnouncementItemFigma(
                announcement = ad,
                isHot = true,
                containerColor = Color(0xFFFFF3E0),
                // modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
fun ProfileScreen(viewModel: AppViewModel) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Аватар и основная информация
        Card(
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.size(100.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Person,
                    contentDescription = "Avatar",
                    modifier = Modifier.size(60.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Ник: User123", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Телефон: +7 (999) 123-45-67", style = MaterialTheme.typography.bodyMedium)

        // Секция с объявлениями пользователя
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Мои объявления",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.Start)
        )

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(viewModel.allAds.filter { it.isUserAd }) { ad ->
                UserAdItem(
                    announcement = ad,
                    onDelete = { viewModel.removeAd(ad) }
                )
            }
        }
    }
}

@Composable
private fun UserAdItem(
    announcement: Announcement,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = announcement.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Цена: ${announcement.price}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Категория: ${announcement.category}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Удалить",
                        tint = Color.Red
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (announcement.isHot) "Горячее объявление" else "Обычное объявление",
                style = MaterialTheme.typography.labelSmall,
                color = if (announcement.isHot) Color.Red else Color.Gray
            )
        }
    }
}

// Triangle Shape class
class TriangleShape : Shape {
    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: androidx.compose.ui.unit.LayoutDirection,
        density: androidx.compose.ui.unit.Density
    ): androidx.compose.ui.graphics.Outline {
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(size.width / 2f, 0f)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        return androidx.compose.ui.graphics.Outline.Generic(path)
    }
}

// Add missing CardDefaults to your imports
object CardDefaults {
    @Composable
    fun cardColors(containerColor: Color): CardColors {
        return androidx.compose.material3.CardDefaults.cardColors(
            containerColor = containerColor
        )
    }
}