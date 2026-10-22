package com.example.contactify.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.contactify.data.User
import com.example.contactify.network.RetrofitClient
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val apiService = RetrofitClient.instance

    init {
        fetchUsers()
    }

    fun fetchUsers() {
        viewModelScope.launch {
            try {
                _users.value = apiService.getUsers()
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi tải danh sách: ${e.message}"
            }
        }
    }

    fun fetchUser(id: Int) {
        viewModelScope.launch {
            try {
                _user.value = apiService.getUser(id)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi tải chi tiết user $id: ${e.message}"
            }
        }
    }

    fun createUser(name: String, email: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val newUser = User(id = 0, name = name, email = email)
                val created = apiService.createUser(newUser)
                // Cập nhật danh sách tạm thời trên UI
                _users.value = _users.value.orEmpty() + created
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi thêm user: ${e.message}"
            }
        }
    }

    fun deleteUser(id: Int) {
        viewModelScope.launch {
            try {
                apiService.deleteUser(id)
                // Xóa khỏi danh sách hiện tại trên UI
                _users.value = _users.value.orEmpty().filter { it.id != id }
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi xóa user: ${e.message}"
            }
        }
    }
}