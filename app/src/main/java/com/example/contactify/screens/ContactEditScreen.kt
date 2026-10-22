package com.example.contactify.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.contactify.data.ContactWithPhones

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactEditScreen(
    contact: ContactWithPhones?, // Nhận vào null (thêm mới) hoặc ContactWithPhones (sửa)
    onSave: (
        contentResolver: android.content.ContentResolver,
        name: String,
        phones: List<String>, // Truyền danh sách số điện thoại dạng List<String>
        email: String,
        group: String
    ) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val contentResolver = context.contentResolver

    // 🟢 1. Khai báo các state TRƯỚC
    val initialName = contact?.contact?.name ?: ""
    val initialPhone = contact?.phoneNumbers?.firstOrNull()?.phoneNumber ?: ""
    val initialEmail = contact?.contact?.email ?: ""
    val initialGroup = contact?.contact?.group ?: "Gia đình"

    var nameState by remember { mutableStateOf(TextFieldValue(initialName)) }
    var phoneState by remember { mutableStateOf(TextFieldValue(initialPhone)) }
    var emailState by remember { mutableStateOf(TextFieldValue(initialEmail)) }
    var groupState by remember { mutableStateOf(TextFieldValue(initialGroup)) }

    // 🟢 2. Khởi tạo bộ xin quyền SAU KHI các state đã được định nghĩa
    val writePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onSave(
                contentResolver,
                nameState.text,
                listOf(phoneState.text),
                emailState.text,
                groupState.text
            )
        } else {
            Toast.makeText(context, "Cần cấp quyền ghi danh bạ để đồng bộ ra máy!", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (contact == null) "Thêm liên hệ" else "Sửa liên hệ")
                },
                navigationIcon = {
                    IconButton(
                        onClick = onCancel
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
                .padding(24.dp),
            verticalArrangement = Arrangement.Top
        ) {

            // TÊN
            OutlinedTextField(
                value = nameState,
                onValueChange = { nameState = it },
                label = { Text("Tên liên hệ") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // SỐ ĐIỆN THOẠI
            OutlinedTextField(
                value = phoneState,
                onValueChange = { phoneState = it },
                label = { Text("Số điện thoại") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // EMAIL
            OutlinedTextField(
                value = emailState,
                onValueChange = { emailState = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // NHÓM
            OutlinedTextField(
                value = groupState,
                onValueChange = { groupState = it },
                label = { Text("Nhóm") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // NÚT LƯU
            Button(
                onClick = {
                    if (nameState.text.isNotBlank() && phoneState.text.isNotBlank()) {
                        // Kiểm tra quyền WRITE_CONTACTS trước khi thực hiện
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CONTACTS)
                            == PackageManager.PERMISSION_GRANTED) {
                            onSave(
                                contentResolver,
                                nameState.text,
                                listOf(phoneState.text),
                                emailState.text,
                                groupState.text
                            )
                        } else {
                            writePermissionLauncher.launch(Manifest.permission.WRITE_CONTACTS)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Lưu thay đổi")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // NÚT HỦY
            Button(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Hủy")
            }
        }
    }
}