package com.example.contactify.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "phone_numbers",
    // Có thể cấu hình ForeignKey nếu muốn ràng buộc chặt chẽ với bảng contacts
)
data class PhoneNumberEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val contactId: Int, // Khóa ngoại liên kết với ID của Contact
    val phoneNumber: String
)