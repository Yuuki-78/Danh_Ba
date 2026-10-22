package com.example.contactify.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.core.content.ContextCompat
import com.example.contactify.components.ContactContextMenu
import com.example.contactify.components.ContactDropdownMenu
import com.example.contactify.components.ContactItem
import com.example.contactify.data.ContactWithPhones
import com.example.contactify.viewmodel.ContactViewModel
import androidx.compose.material.icons.filled.Person
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactScreen(
    viewModel: ContactViewModel,
    onContactClick: (ContactWithPhones) -> Unit,
    onEditContact: (ContactWithPhones) -> Unit,
    onSearchClick: () -> Unit,
    onGroupClick: () -> Unit,
    onStatisticsClick: () -> Unit,
    onAddContactClick: () -> Unit,
    onUserListClick: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current

    // Trạng thái menu trên TopAppBar
    var showTopMenu by remember {
        mutableStateOf(false)
    }

    // Contact đang được nhấn giữ (kiểu ContactWithPhones)
    var selectedContact by remember {
        mutableStateOf<ContactWithPhones?>(null)
    }

    // Trạng thái Context Menu
    var showContextMenu by remember {
        mutableStateOf(false)
    }

    // Bộ khởi chạy xin quyền đọc danh bạ hệ thống cho Bài thực hành 05
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.syncSystemContacts(context.contentResolver)
            Toast.makeText(context, "Đang đồng bộ danh bạ từ điện thoại...", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Cần cấp quyền đọc danh bạ để đồng bộ!", Toast.LENGTH_SHORT).show()
        }
    }

    // 1. Lấy danh sách thời gian thực từ Room Database
    val contactsWithPhones by viewModel.contactsWithPhones.collectAsState()

    // Lọc danh sách theo ô tìm kiếm
    val displayedContacts = viewModel.getFilteredContacts(contactsWithPhones)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Danh bạ")
                },
                actions = {
                    // Nút chuyển sang trang User API (Bài 6)
                    IconButton(
                        onClick = { onUserListClick() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person, // 👈 Đổi từ Cloud sang Person (hoặc List)
                            contentDescription = "Xem danh sách User API"
                        )
                    }
                    // 🟢 NÚT ĐỒNG BỘ DANH BẠ HỆ THỐNG (BÀI THỰC HÀNH 05)
                    IconButton(
                        onClick = {
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
                                == PackageManager.PERMISSION_GRANTED) {
                                viewModel.syncSystemContacts(context.contentResolver)
                                Toast.makeText(context, "Đang đồng bộ danh bạ...", Toast.LENGTH_SHORT).show()
                            } else {
                                permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = "Đồng bộ danh bạ"
                        )
                    }

                    // Nút menu dọc ba chấm sẵn có
                    Box {
                        IconButton(
                            onClick = {
                                showTopMenu = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Menu"
                            )
                        }

                        // Menu chính
                        ContactDropdownMenu(
                            expanded = showTopMenu,
                            onDismiss = {
                                showTopMenu = false
                            },
                            onSearch = {
                                onSearchClick()
                            },
                            onGroup = {
                                onGroupClick()
                            },
                            onStatistics = {
                                onStatisticsClick()
                            },
                            onTheme = {
                                Toast.makeText(
                                    context,
                                    "Chức năng giao diện",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onAddContactClick()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Thêm liên lạc"
                )
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // Danh sách Contact (Truyền ContactWithPhones)
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = displayedContacts,
                    key = { it.contact.id }
                ) { item ->

                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        // Item Contact (Hiển thị thông tin 1-N)
                        ContactItem(
                            item = item,
                            // Click bình thường
                            onClick = {
                                viewModel.selectContact(item)
                                onContactClick(item)
                            },
                            // Nhấn giữ
                            onLongClick = {
                                selectedContact = item
                                showContextMenu = true
                            }
                        )

                        // Context Menu khi nhấn giữ
                        if (
                            showContextMenu &&
                            selectedContact?.contact?.id == item.contact.id
                        ) {
                            ContactContextMenu(
                                expanded = true,
                                contact = item,

                                // Đóng menu
                                onDismiss = {
                                    showContextMenu = false
                                    selectedContact = null
                                },

                                // 📞 Gọi (Lấy số điện thoại đầu tiên trong danh sách 1-N)
                                onCall = {
                                    val phoneNumber = item.phoneNumbers.firstOrNull()?.phoneNumber ?: ""
                                    val intent = Intent(
                                        Intent.ACTION_DIAL,
                                        Uri.parse("tel:$phoneNumber")
                                    )
                                    context.startActivity(intent)
                                },

                                // ✏️ Sửa
                                onEdit = {
                                    onEditContact(item)
                                },

                                // ⭐ Yêu thích
                                onFavorite = {
                                    viewModel.toggleFavorite(item)
                                },

                                // 📋 Sao chép số đầu tiên
                                onCopy = {
                                    val phoneNumber = item.phoneNumbers.firstOrNull()?.phoneNumber ?: ""
                                    clipboardManager.setText(
                                        AnnotatedString(phoneNumber)
                                    )
                                    Toast.makeText(
                                        context,
                                        "Đã sao chép số điện thoại",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },

                                // 🗑️ Xóa
                                onDelete = {
                                    viewModel.deleteContact(item)
                                    Toast.makeText(
                                        context,
                                        "Đã xóa liên hệ",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}