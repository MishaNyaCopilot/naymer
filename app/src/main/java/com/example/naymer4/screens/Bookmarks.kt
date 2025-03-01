package com.example.naymer4.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.naymer4.AnnouncementItemFigma
import com.example.naymer4.AppViewModel

@Composable
fun BookmarksScreen(viewModel: AppViewModel) {
    val bookmarkedAds = viewModel.bookmarkedAds

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Закладки",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn {
            items(bookmarkedAds) { ad ->
                AnnouncementItemFigma(
                    announcement = ad,
                    onBookmarkToggle = { viewModel.toggleBookmark(ad) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}