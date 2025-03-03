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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import com.example.naymer4.screens.BookmarksScreen
import com.example.naymer4.screens.FilterCriteria
import com.example.naymer4.screens.NewAdsScreen
import com.example.naymer4.screens.NormalAdsScreen
import com.example.naymer4.screens.ProfileScreen

// model/Announcement.kt
data class Announcement(
    val title: String,
    val price: String,
    val category: String,
    val geo: String,
    val workingTime: String,
    val isHot: Boolean = false,
    val isUserAd: Boolean = false,
    val isBookmarked: Boolean = false // Добавляем новое поле
)

enum class SortOrder {
    DEFAULT, CHEAPER, EXPENSIVE, DATE
}

class AppViewModel : ViewModel() {
    private val _allAds = mutableStateListOf<Announcement>()
    val allAds: List<Announcement> get() = _allAds

    val bookmarkedAds: List<Announcement>
        get() = allAds.filter { it.isBookmarked }

    // Состояние для фильтров (по умолчанию пустое)
    var filterCriteria by mutableStateOf(FilterCriteria())
        private set

    init {
        _allAds.addAll(MockData.initialAnnouncements)
    }

    fun toggleBookmark(announcement: Announcement) {
        val index = _allAds.indexOfFirst { it == announcement }
        if (index != -1) {
            _allAds[index] = announcement.copy(isBookmarked = !announcement.isBookmarked)
        }
    }

    fun addAd(announcement: Announcement) {
        _allAds.add(announcement.copy(isUserAd = true))
    }

    fun removeAd(announcement: Announcement) {
        _allAds.remove(announcement)
    }

    // Применяем фильтры, обновляя состояние
    fun applyFilters(criteria: FilterCriteria) {
        filterCriteria = criteria
    }

    val filteredAds: List<Announcement>
        get() {
            var ads = _allAds.toList()

            // Фильтрация по нескольким категориям
            filterCriteria.categories?.takeIf { it.isNotEmpty() }?.let { selectedCategories ->
                ads = ads.filter { ad -> selectedCategories.contains(ad.category) }
            }

            // Фильтрация по местоположению (ищем совпадения в поле geo)
            filterCriteria.location?.let { loc ->
                ads = ads.filter { it.geo.contains(loc, ignoreCase = true) }
            }

            // Фильтрация по цене "от"
            filterCriteria.priceFrom?.let { from ->
                ads = ads.filter { extractPrice(it.price) >= from }
            }

            // Фильтрация по цене "до"
            filterCriteria.priceTo?.let { to ->
                ads = ads.filter { extractPrice(it.price) <= to }
            }

            // Сортировка
            ads = when (filterCriteria.sortOrder) {
                SortOrder.CHEAPER -> ads.sortedBy { extractPrice(it.price) }
                SortOrder.EXPENSIVE -> ads.sortedByDescending { extractPrice(it.price) }
                else -> ads
            }

            return ads
        }

    // Вспомогательная функция для извлечения числа из строки цены (например, "50000 руб")
    private fun extractPrice(price: String): Int {
        return price.filter { it.isDigit() }.toIntOrNull() ?: 0
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp() {
    val navController = rememberNavController()
    val viewModel: AppViewModel = viewModel()
    var showAdTypeSheet by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            Column {
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
                BottomNavBar(
                    navController = navController,
                    onCreateClick = { showAdTypeSheet = true }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "adsList",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("adsList") { NormalAdsScreen(viewModel = viewModel) }
            composable("hotAds") { HotAdsScreen(viewModel = viewModel) }
            composable("createNormalAd") { NewAdsScreen(navController = navController, viewModel = viewModel, isHotAd = false) }
            composable("createHotAd") { NewAdsScreen(navController = navController, viewModel = viewModel, isHotAd = true) }
            composable("bookmarks") { BookmarksScreen(viewModel = viewModel) }
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
        modifier = Modifier
            .height(100.dp)
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

@Composable
fun AnnouncementItemFigma(
    announcement: Announcement,
    isHot: Boolean = false,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    onBookmarkToggle: () -> Unit = {} // Добавляем значение по умолчанию
) {
    Card(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isHot) MaterialTheme.colorScheme.errorContainer else containerColor
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(65.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(MaterialTheme.colorScheme.onSurfaceVariant)
                            .clip(TriangleShape())
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(MaterialTheme.colorScheme.onSurfaceVariant)
                            .clip(CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = announcement.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(
                        onClick = onBookmarkToggle,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(start = 8.dp)
                    ) {
                        Icon(
                            imageVector = if (announcement.isBookmarked) {
                                Icons.Filled.Favorite
                            } else {
                                Icons.Outlined.FavoriteBorder
                            },
                            contentDescription = "Закладка",
                            tint = if (announcement.isBookmarked) Color.Red
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (isHot) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Whatshot,
                                contentDescription = "Hot",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Осталось 12 ч.",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }

                Text(
                    text = announcement.price,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = announcement.category,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = announcement.geo,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = "Time",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (announcement.isHot) "Время действия: ${announcement.workingTime}"
                        else "Время работы: ${announcement.workingTime}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun HotAdsScreen(viewModel: AppViewModel) {
    val hotAds = viewModel.allAds.filter { it.isHot }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Text(
            text = "Горячие объявления",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(hotAds) { ad ->
                AnnouncementItemFigma(
                    announcement = ad,
                    isHot = true,
                    containerColor = Color(0xFFFFF3E0),
                    onBookmarkToggle = { viewModel.toggleBookmark(ad) }
                )
            }
        }
    }
}

@Composable
fun UserAdItem(
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