package com.example.contactify.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.contactify.data.ContactDatabase
import com.example.contactify.data.ContactEntity
import com.example.contactify.data.ContactWithPhones
import com.example.contactify.repository.ContactRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ContactViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ContactRepository

    // Danh sách liên hệ kèm số điện thoại lấy từ Room Database (Real-time qua Flow)
    val contactsWithPhones: StateFlow<List<ContactWithPhones>>

    // Contact đang được chọn để xem chi tiết hoặc chỉnh sửa
    var selectedContact: ContactWithPhones? = null
        private set

    // Trạng thái từ ô tìm kiếm
    var searchQuery by mutableStateOf("")
        private set

    init {
        val contactDao = ContactDatabase.getDatabase(application).contactDao
        repository = ContactRepository(contactDao)

        contactsWithPhones = repository.allContactsWithPhones
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    // =========================
    // Thêm liên hệ mới
    // =========================
    fun addContact(
        name: String,
        phoneNumbers: List<String>,
        email: String,
        group: String,
        isFavorite: Boolean = false
    ) {
        viewModelScope.launch {
            val newContact = ContactEntity(
                name = name,
                email = email,
                group = group,
                isFavorite = isFavorite
            )
            repository.insertContactWithPhones(newContact, phoneNumbers)
        }
    }

    // =========================
    // Chọn Contact để xem/sửa
    // =========================
    fun selectContact(contact: ContactWithPhones?) {
        selectedContact = contact
    }

    // =========================
    // Xóa Contact
    // =========================
    fun deleteContact(contactWithPhones: ContactWithPhones) {
        viewModelScope.launch {
            repository.deleteContact(contactWithPhones.contact)
            if (selectedContact?.contact?.id == contactWithPhones.contact.id) {
                selectedContact = null
            }
        }
    }

    // =========================
    // Cập nhật Contact
    // =========================
    fun updateContact(
        id: Int,
        name: String,
        phoneNumbers: List<String>,
        email: String,
        group: String
    ) {
        viewModelScope.launch {
            val existingFavorite = contactsWithPhones.value
                .find { it.contact.id == id }?.contact?.isFavorite ?: false

            val contactEntity = ContactEntity(
                id = id,
                name = name,
                email = email,
                group = group,
                isFavorite = existingFavorite
            )
            repository.updateContactWithPhones(contactEntity, phoneNumbers)
        }
    }

    fun updateContact(
        contactEntity: ContactEntity,
        phoneNumbers: List<String>
    ) {
        viewModelScope.launch {
            repository.updateContactWithPhones(contactEntity, phoneNumbers)
        }
    }

    // =========================
    // Đổi trạng thái Yêu thích
    // =========================
    fun toggleFavorite(contactWithPhones: ContactWithPhones) {
        viewModelScope.launch {
            val updatedContact = contactWithPhones.contact.copy(
                isFavorite = !contactWithPhones.contact.isFavorite
            )
            val phoneNumbers = contactWithPhones.phoneNumbers.map { it.phoneNumber }
            repository.updateContactWithPhones(updatedContact, phoneNumbers)

            if (selectedContact?.contact?.id == updatedContact.id) {
                selectedContact = contactWithPhones.copy(contact = updatedContact)
            }
        }
    }

    // =========================
    // Tìm kiếm và Lọc dữ liệu (Đổi tên thành updateSearchQuery để tránh trùng)
    // =========================
    fun updateSearchQuery(query: String) {
        searchQuery = query
    }

    fun getFilteredContacts(list: List<ContactWithPhones>): List<ContactWithPhones> {
        if (searchQuery.isBlank()) return list
        return list.filter { item ->
            item.contact.name.contains(searchQuery, ignoreCase = true) ||
                    item.phoneNumbers.any { it.phoneNumber.contains(searchQuery) }
        }
    }

    fun getFilteredContacts(): List<ContactWithPhones> {
        val list = contactsWithPhones.value
        if (searchQuery.isBlank()) return list
        return list.filter { item ->
            item.contact.name.contains(searchQuery, ignoreCase = true) ||
                    item.phoneNumbers.any { it.phoneNumber.contains(searchQuery) }
        }
    }

    fun getFavoriteContacts(list: List<ContactWithPhones>): List<ContactWithPhones> {
        return list.filter { it.contact.isFavorite }
    }

    fun getContactsByGroup(list: List<ContactWithPhones>, group: String): List<ContactWithPhones> {
        return list.filter { it.contact.group == group }
    }

    // =========================
    // Thống kê
    // =========================
    fun getTotalContacts(list: List<ContactWithPhones>): Int = list.size

    fun getFavoriteCount(list: List<ContactWithPhones>): Int = list.count { it.contact.isFavorite }

    fun getGroupCount(list: List<ContactWithPhones>, group: String): Int = list.count { it.contact.group == group }
}