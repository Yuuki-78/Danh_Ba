package com.example.contactify.screens

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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.contactify.data.ContactWithPhones

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactEditScreen(
    contact: ContactWithPhones?, // Nhận vào null (thêm mới) hoặc ContactWithPhones (sửa)
    onSave: (
        name: String,
        phones: List<String>, // Truyền danh sách số điện thoại dạng List<String>
        email: String,
        group: String
    ) -> Unit,
    onCancel: () -> Unit
) {
    // Lấy dữ liệu cũ nếu là sửa, hoặc để trống nếu là thêm mới
    val initialName = contact?.contact?.name ?: ""
    val initialPhone = contact?.phoneNumbers?.firstOrNull()?.phoneNumber ?: ""
    val initialEmail = contact?.contact?.email ?: ""
    val initialGroup = contact?.contact?.group ?: "Gia đình"

    var nameState by remember { mutableStateOf(TextFieldValue(initialName)) }
    var phoneState by remember { mutableStateOf(TextFieldValue(initialPhone)) }
    var emailState by remember { mutableStateOf(TextFieldValue(initialEmail)) }
    var groupState by remember { mutableStateOf(TextFieldValue(initialGroup)) }

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
                        onSave(
                            nameState.text,
                            listOf(phoneState.text), // Đóng gói thành List<String>
                            emailState.text,
                            groupState.text
                        )
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