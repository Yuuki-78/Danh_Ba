package com.example.contactify.routes

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val CONTACTS = "contacts"
    const val DETAIL = "detail/{contactId}"
    const val EDIT = "edit/{contactId}"
    const val SEARCH = "search"
    const val GROUP = "group"
    const val STATISTICS = "statistics"

    // Route Bài 6 (Retrofit User)
    const val USER_LIST = "user_list"
    const val USER_DETAIL = "user_detail/{userId}"

    // Hàm hỗ trợ
    fun detail(contactId: Int): String = "detail/$contactId"
    fun edit(contactId: Int): String = "edit/$contactId"

    fun userDetail(userId: Int): String = "user_detail/$userId"
}