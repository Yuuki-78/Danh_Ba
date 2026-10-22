package com.example.contactify.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.contactify.data.ContactWithPhones

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailScreen(
    contact: ContactWithPhones, // Dùng ContactWithPhones
    onBack: () -> Unit,
    onEdit: () -> Unit
) {
    val c = contact.contact
    val phones = contact.phoneNumbers

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Chi tiết liên hệ")
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
                },
                actions = {
                    IconButton(
                        onClick = onEdit
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Sửa"
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
                .padding(24.dp),
            verticalArrangement = Arrangement.Top
        ) {

            // Tên Contact
            Text(
                text = c.name
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            // Số điện thoại (Hiển thị danh sách 1-N)
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Số điện thoại: "
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (phones.isEmpty()) {
                    Text(text = "Chưa có")
                } else {
                    phones.forEach { p ->
                        Text(
                            text = "• ${p.phoneNumber}",
                            modifier = Modifier.padding(start = 8.dp, bottom = 2.dp)
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // Email
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Email: "
                )

                Text(
                    text = if (c.email.isBlank()) {
                        "Chưa có"
                    } else {
                        c.email
                    }
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // Nhóm
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Nhóm: "
                )

                Text(
                    text = c.group
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // Trạng thái yêu thích
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Yêu thích: "
                )

                if (c.isFavorite) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Yêu thích"
                    )
                } else {
                    Text("Không")
                }
            }
        }
    }
}