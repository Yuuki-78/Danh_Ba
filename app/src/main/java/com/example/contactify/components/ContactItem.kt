package com.example.contactify.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.contactify.data.ContactWithPhones

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContactItem(
    item: ContactWithPhones, // Đổi từ Contact sang ContactWithPhones
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val contact = item.contact
    val phoneNumbers = item.phoneNumbers

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contact.name,
                    style = MaterialTheme.typography.titleMedium
                )

                // Hiển thị số điện thoại đầu tiên (hoặc các số nếu có)
                val firstPhone = phoneNumbers.firstOrNull()?.phoneNumber ?: "Không có số điện thoại"
                Text(
                    text = firstPhone,
                    style = MaterialTheme.typography.bodyMedium
                )

                if (contact.group.isNotBlank()) {
                    Text(
                        text = contact.group,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            if (contact.isFavorite) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Yêu thích"
                )
            }
        }
    }
}