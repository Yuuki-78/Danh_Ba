package com.example.contactify.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.contactify.data.ContactWithPhones

@Composable
fun ContactContextMenu(
    expanded: Boolean,
    contact: ContactWithPhones, // Đổi từ Contact sang ContactWithPhones
    onDismiss: () -> Unit,
    onCall: () -> Unit,
    onEdit: () -> Unit,
    onFavorite: () -> Unit,
    onCopy: () -> Unit,
    onDelete: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss
    ) {

        // 📞 Gọi điện
        DropdownMenuItem(
            text = {
                Text("Gọi điện")
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Gọi điện"
                )
            },
            onClick = {
                onCall()
                onDismiss()
            }
        )

        // ✏️ Sửa thông tin
        DropdownMenuItem(
            text = {
                Text("Sửa thông tin")
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Sửa thông tin"
                )
            },
            onClick = {
                onEdit()
                onDismiss()
            }
        )

        // ⭐ Yêu thích
        DropdownMenuItem(
            text = {
                Text(
                    if (contact.contact.isFavorite) // Truy cập qua contact.isFavorite
                        "Bỏ yêu thích"
                    else
                        "Thêm vào yêu thích"
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Yêu thích"
                )
            },
            onClick = {
                onFavorite()
                onDismiss()
            }
        )

        // 📋 Sao chép số điện thoại
        DropdownMenuItem(
            text = {
                Text("Sao chép số điện thoại")
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Sao chép"
                )
            },
            onClick = {
                onCopy()
                onDismiss()
            }
        )

        // 🗑️ Xóa
        DropdownMenuItem(
            text = {
                Text("Xóa liên hệ")
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Xóa"
                )
            },
            onClick = {
                onDelete()
                onDismiss()
            }
        )
    }
}