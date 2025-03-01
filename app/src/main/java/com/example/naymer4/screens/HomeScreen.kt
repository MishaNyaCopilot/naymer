package com.example.naymer4.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.naymer4.AnnouncementItemFigma
import com.example.naymer4.AppViewModel

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

        LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
            items(normalAds.filter {
                it.title.contains(searchQuery, true) ||
                        it.category.contains(searchQuery, true)
            }) { ad ->
                AnnouncementItemFigma(
                    announcement = ad,
                    onBookmarkToggle = { viewModel.toggleBookmark(ad) }, // Добавляем обработчик
                    containerColor = MaterialTheme.colorScheme.surface // Явно указываем цвет
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}