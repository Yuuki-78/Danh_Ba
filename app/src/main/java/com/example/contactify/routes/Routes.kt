package com.example.contactify.routes

object Routes {
    const val CONTACTS = "contacts"
    const val DETAIL = "detail/{contactId}"
    const val EDIT = "edit/{contactId}"
    const val SEARCH = "search"
    const val GROUP = "group"
    const val STATISTICS = "statistics"

    // Hàm hỗ trợ sinh đường dẫn kèm ID tự động
    fun detail(contactId: Int): String = "detail/$contactId"
    fun edit(contactId: Int): String = "edit/$contactId"
}