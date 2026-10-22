package com.example.contactify.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.contactify.viewmodel.ContactViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    viewModel: ContactViewModel,
    onBack: () -> Unit
) {
    // Lấy danh sách thời gian thực từ Room Database
    val contactsList by viewModel.contactsWithPhones.collectAsState()

    // Tổng số liên hệ
    val totalContacts = contactsList.size

    // Tổng số liên hệ yêu thích
    val favoriteContacts = contactsList.count { it.contact.isFavorite }

    // Lấy danh sách nhóm độc nhất
    val groups = contactsList
        .map { it.contact.group }
        .distinct()
        .filter { it.isNotBlank() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Thống kê danh bạ")
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Quay lại"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // =========================
            // TỔNG SỐ LIÊN HỆ
            // =========================
            item {
                StatisticsCard(
                    title = "📱 Tổng số liên hệ",
                    value = totalContacts.toString()
                )
            }

            // =========================
            // YÊU THÍCH
            // =========================
            item {
                StatisticsCard(
                    title = "⭐ Liên hệ yêu thích",
                    value = favoriteContacts.toString()
                )
            }

            // =========================
            // THỐNG KÊ THEO NHÓM
            // =========================
            item {
                Text(
                    text = "👥 Thống kê theo nhóm",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(groups) { group ->
                val groupCount = contactsList.count { it.contact.group == group }
                StatisticsCard(
                    title = group,
                    value = "$groupCount liên hệ"
                )
            }
        }
    }
}

@Composable
private fun StatisticsCard(
    title: String,
    value: String
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}