package com.example.contactify.data

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String? = null,
    val website: String? = null
)