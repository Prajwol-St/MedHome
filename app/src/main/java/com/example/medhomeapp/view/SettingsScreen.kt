package com.example.medhomeapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medhomeapp.R
import com.example.medhomeapp.viewmodel.UserViewModel


@Composable
fun SettingsScreen(
    userViewModel: UserViewModel,
    userId: String,
    onLogoutSuccess: () -> Unit
) {
    val scrollState = rememberScrollState()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
    ) {

        Spacer(modifier = Modifier.height(24.dp))
        SectionHeader(title = "Account")

        SettingsItem(
            icon = R.drawable.baseline_person_24,
            title = "Edit Profile",
            subtitle = "Update your personal information",
            onClick = {}
        )

        SettingsItem(
            icon = R.drawable.baseline_lock_24,
            title = "Change Password",
            subtitle = "Update your password",
            onClick = {}
        )

        Spacer(modifier = Modifier.height(16.dp))
        SectionHeader(title = "Preferences")

        SettingsItem(
            icon = R.drawable.baseline_notifications_24,
            title = "Notifications",
            subtitle = "Manage notification settings",
            onClick = {}
        )

        SettingsItem(
            icon = R.drawable.baseline_language_24,
            title = "Language",
            subtitle = "English",
            onClick = {}
        )

        Spacer(modifier = Modifier.height(16.dp))
        SectionHeader(title = "Help & Support")

        SettingsItem(
            icon = R.drawable.baseline_warning_24,
            title = "Help Center",
            subtitle = "Get help and support",
            onClick = {}
        )

        SettingsItem(
            icon = R.drawable.baseline_privacy_tip_24,
            title = "Privacy Policy",
            subtitle = "Read our privacy policy",
            onClick = {}
        )

        SettingsItem(
            icon = R.drawable.baseline_menu_book_24,
            title = "Terms of Service",
            subtitle = "Read our terms",
            onClick = {}
        )

        SettingsItem(
            icon = R.drawable.baseline_info_24,
            title = "About",
            subtitle = "Version 1.0.0",
            onClick = {}
        )

        Spacer(modifier = Modifier.height(16.dp))
        SectionHeader(title = "Danger Zone")

        SettingsItem(
            icon = R.drawable.baseline_logout_24,
            title = "Logout",
            subtitle = "Sign out of your account",
            titleColor = Color(0xFFE53935),
            onClick = { showLogoutDialog = true }
        )

        SettingsItem(
            icon = R.drawable.baseline_delete_24,
            title = "Delete Account",
            subtitle = "Permanently delete your account",
            titleColor = Color(0xFFE53935),
            onClick = { showDeleteDialog = true }
        )

        Spacer(modifier = Modifier.height(80.dp))
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                Text(
                    text = "Logout",
                    color = Color.Red,
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            userViewModel.logout()
                            onLogoutSuccess()
                        }
                )
            },
            dismissButton = {
                Text(
                    text = "Cancel",
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable { showLogoutDialog = false }
                )
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Account") },
            text = {
                Text("This action is permanent. Your account and all data will be deleted.")
            },
            confirmButton = {
                Text(
                    text = "Delete",
                    color = Color.Red,
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            userViewModel.deleteUser(userId) { dbSuccess: Boolean, _ ->
                                if (dbSuccess) {
                                    userViewModel.deleteAuthAccount { authSuccess: Boolean, _ ->
                                        if (authSuccess) {
                                            userViewModel.logout()
                                            onLogoutSuccess()
                                        }
                                    }
                                }
                            }
                        }
                )
            },
            dismissButton = {
                Text(
                    text = "Cancel",
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable { showDeleteDialog = false }
                )
            }
        )
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = TextStyle(
            color = Color(0xFF648DDB),
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
    )
}

@Composable
fun SettingsItem(
    icon: Int,
    title: String,
    subtitle: String,
    titleColor: Color = Color.Black,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                painter = painterResource(id = icon),
                contentDescription = title,
                tint = Color(0xFF648DDB),
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = TextStyle(
                        color = titleColor,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subtitle,
                    style = TextStyle(
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                )
            }

            Icon(
                painter = painterResource(id = R.drawable.baseline_arrow_right_24),
                contentDescription = "Navigate",
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
