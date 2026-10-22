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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.contactify.components.ContactItem
import com.example.contactify.data.ContactWithPhones
import com.example.contactify.viewmodel.ContactViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupScreen(
    viewModel: ContactViewModel,
    onContactClick: (ContactWithPhones) -> Unit, // Dùng ContactWithPhones
    onBack: () -> Unit
) {
    // Lấy danh sách thời gian thực từ Room Database
    val contactsList by viewModel.contactsWithPhones.collectAsState()

    // Nhóm đang được chọn
    var selectedGroup by remember {
        mutableStateOf<String?>(null)
    }

    // Lấy danh sách nhóm từ ContactWithPhones
    val groups = contactsList
        .map { it.contact.group }
        .distinct()
        .filter { it.isNotBlank() }

    // Nếu đã chọn nhóm thì lọc Contact
    val contacts = if (selectedGroup == null) {
        emptyList()
    } else {
        contactsList.filter { it.contact.group == selectedGroup }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Nhóm liên hệ")
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = "Chọn một nhóm:"
            )

            // =========================
            // DANH SÁCH NHÓM
            // =========================
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(
                        if (selectedGroup == null) 1f else 0.4f
                    )
            ) {

                items(groups) { group ->
                    val count = contactsList.count { it.contact.group == group }

                    Button(
                        onClick = {
                            selectedGroup = group
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = "$group ($count)"
                        )
                    }
                }
            }

            // =========================
            // CONTACT TRONG NHÓM
            // =========================
            if (selectedGroup != null) {

                Text(
                    text = "Danh bạ: $selectedGroup"
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.6f)
                ) {

                    items(
                        items = contacts,
                        key = { it.contact.id } // Lấy id từ contact bên trong
                    ) { item ->

                        ContactItem(
                            item = item, // Truyền ContactWithPhones
                            onClick = {
                                onContactClick(item)
                            },
                            onLongClick = {
                                // Context Menu được xử lý ở ContactScreen
                            }
                        )
                    }
                }
            }
        }
    }
}