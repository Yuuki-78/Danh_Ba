package com.example.contactify.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.contactify.routes.Routes
import com.example.contactify.screens.ContactDetailScreen
import com.example.contactify.screens.ContactEditScreen
import com.example.contactify.screens.ContactScreen
import com.example.contactify.screens.GroupScreen
import com.example.contactify.screens.LoginScreen
import com.example.contactify.screens.RegisterScreen
import com.example.contactify.screens.SearchScreen
import com.example.contactify.screens.StatisticsScreen
import com.example.contactify.screens.UserDetailScreen
import com.example.contactify.screens.UserListScreen
import com.example.contactify.viewmodel.ContactViewModel
import com.example.contactify.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: ContactViewModel = viewModel()

    // Lấy danh sách thời gian thực từ Room Database
    val contactsList by viewModel.contactsWithPhones.collectAsState()

    // Kiểm tra xem user đã đăng nhập và xác thực email chưa để cấu hình màn hình khởi đầu
    val currentUser = FirebaseAuth.getInstance().currentUser
    val startDest = if (currentUser != null && currentUser.isEmailVerified) {
        Routes.CONTACTS
    } else {
        Routes.LOGIN
    }

    NavHost(
        navController = navController,
        startDestination = startDest
    ) {
        // 🟢 0A. MÀN HÌNH ĐĂNG NHẬP (CẦN THIẾT ĐỂ KHÔNG BỊ VĂNG APP)
        composable(Routes.LOGIN) {
            LoginScreen(
                navController = navController,
                onLoginSuccess = {
                    navController.navigate(Routes.CONTACTS) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // 🟢 0B. MÀN HÌNH ĐĂNG KÝ (BÀI 7)
        composable(Routes.REGISTER) {
            RegisterScreen(navController = navController)
        }

        // 1. DANH SÁCH CONTACT
        composable(Routes.CONTACTS) {
            ContactScreen(
                viewModel = viewModel,
                onContactClick = { item ->
                    navController.navigate(Routes.detail(item.contact.id))
                },
                onEditContact = { item ->
                    navController.navigate(Routes.edit(item.contact.id))
                },
                onSearchClick = {
                    navController.navigate(Routes.SEARCH)
                },
                onGroupClick = {
                    navController.navigate(Routes.GROUP)
                },
                onStatisticsClick = {
                    navController.navigate(Routes.STATISTICS)
                },
                onAddContactClick = {
                    navController.navigate(Routes.edit(-1))
                },
                onUserListClick = {
                    navController.navigate(Routes.USER_LIST)
                },
                onLogout = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.CONTACTS) { inclusive = true }
                    }
                }
            )
        }

        // 2. CHI TIẾT CONTACT
        composable(
            route = Routes.DETAIL,
            arguments = listOf(
                navArgument("contactId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val contactId = backStackEntry.arguments?.getInt("contactId")
            val item = contactsList.find { it.contact.id == contactId }

            if (item != null) {
                ContactDetailScreen(
                    contact = item,
                    onBack = { navController.popBackStack() },
                    onEdit = {
                        navController.navigate(Routes.edit(item.contact.id))
                    }
                )
            }
        }

        // 3. SỬA / THÊM CONTACT
        composable(
            route = Routes.EDIT,
            arguments = listOf(
                navArgument("contactId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val contactId = backStackEntry.arguments?.getInt("contactId") ?: -1

            if (contactId == -1) {
                ContactEditScreen(
                    contact = null,
                    onSave = { contentResolver, name, phones, email, group ->
                        val phoneNumber = phones.firstOrNull() ?: ""
                        viewModel.addNewContact(
                            contentResolver = contentResolver,
                            name = name,
                            phone = phoneNumber,
                            email = email,
                            group = group
                        )
                        navController.popBackStack()
                    },
                    onCancel = {
                        navController.popBackStack()
                    }
                )
            } else {
                val item = contactsList.find { it.contact.id == contactId }

                if (item != null) {
                    ContactEditScreen(
                        contact = item,
                        onSave = { _, name, phones, email, group ->
                            viewModel.updateContact(
                                id = item.contact.id,
                                name = name,
                                phoneNumbers = phones,
                                email = email,
                                group = group
                            )
                            navController.popBackStack()
                        },
                        onCancel = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }

        // 4. TÌM KIẾM
        composable(Routes.SEARCH) {
            SearchScreen(
                viewModel = viewModel,
                onContactClick = { item ->
                    navController.navigate(Routes.detail(item.contact.id))
                },
                onBack = { navController.popBackStack() }
            )
        }

        // 5. NHÓM CONTACT
        composable(Routes.GROUP) {
            GroupScreen(
                viewModel = viewModel,
                onContactClick = { item ->
                    navController.navigate(Routes.detail(item.contact.id))
                },
                onBack = { navController.popBackStack() }
            )
        }

        // 6. THỐNG KÊ
        composable(Routes.STATISTICS) {
            StatisticsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // 7. DANH SÁCH USER (RETROFIT - BÀI 06)
        composable(Routes.USER_LIST) {
            val userViewModel: UserViewModel = viewModel()
            UserListScreen(
                viewModel = userViewModel,
                onUserClick = { userId ->
                    navController.navigate(Routes.userDetail(userId))
                },
                onAddClick = { }
            )
        }

        // 8. CHI TIẾT USER (RETROFIT - BÀI 06)
        composable(
            route = Routes.USER_DETAIL,
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            val userViewModel: UserViewModel = viewModel()

            UserDetailScreen(
                userId = userId,
                viewModel = userViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}