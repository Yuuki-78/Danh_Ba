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
import com.example.contactify.screens.SearchScreen
import com.example.contactify.screens.StatisticsScreen
import com.example.contactify.viewmodel.ContactViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: ContactViewModel = viewModel()

    // Lấy danh sách thời gian thực từ Room Database
    val contactsList by viewModel.contactsWithPhones.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Routes.CONTACTS
    ) {
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
                    // Truyền ID là -1 để màn hình sửa/thêm biết đây là thêm mới contact
                    navController.navigate(Routes.edit(-1))
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
                    contact = item, // Truyền ContactWithPhones
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
                // Thêm mới: Truyền dữ liệu rỗng
                ContactEditScreen(
                    contact = null, // null nghĩa là thêm mới
                    onSave = { name, phones, email, group ->
                        viewModel.addContact(
                            name = name,
                            phoneNumbers = phones, // Truyền List<String> số điện thoại
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
                // Sửa contact cũ
                val item = contactsList.find { it.contact.id == contactId }

                if (item != null) {
                    ContactEditScreen(
                        contact = item, // Truyền ContactWithPhones cũ để load dữ liệu lên form
                        onSave = { name, phones, email, group ->
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
    }
}