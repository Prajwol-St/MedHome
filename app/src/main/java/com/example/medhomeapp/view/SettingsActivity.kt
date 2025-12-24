package com.example.medhomeapp.view

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medhomeapp.BaseActivity
import com.example.medhomeapp.R
import com.example.medhomeapp.repository.UserRepoImpl
import com.example.medhomeapp.utils.LanguageManager
import com.example.medhomeapp.viewmodel.UserViewModel

class SettingsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SettingsScreen()
        }
    }
}

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val viewModel = remember { UserViewModel(UserRepoImpl()) }
    val scrollState = rememberScrollState()

    val sharedPrefs = (context as BaseActivity).getSharedPreferences("MedHomePrefs", MODE_PRIVATE)
    val userId = sharedPrefs.getString("user_id", null)

    val currentUser by viewModel.currentUser

    LaunchedEffect(userId) {
        userId?.let { viewModel.getUserByID(it) }
    }

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    // Get current language
    val currentLanguage = LanguageManager.getLanguage(context)
    val selectedLanguage = if (currentLanguage == LanguageManager.ENGLISH) {
        stringResource(R.string.english)
    } else {
        stringResource(R.string.nepali)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
    ) {
        // Back Button Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { (context as BaseActivity).finish() }) {
                Icon(
                    painter = painterResource(R.drawable.baseline_arrow_back_24),
                    contentDescription = stringResource(R.string.back),
                    tint = Color(0xFF648DDB)
                )
            }
            Text(
                text = stringResource(R.string.settings),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF648DDB)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Profile Info Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
            elevation = CardDefaults.cardElevation(2.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF648DDB).copy(alpha = 0.1f))
                        .border(2.dp, Color(0xFF648DDB), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = stringResource(R.string.profile),
                        modifier = Modifier.size(48.dp),
                        tint = Color(0xFF648DDB)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = currentUser?.name ?: stringResource(R.string.loading),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentUser?.email ?: "",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF648DDB).copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = currentUser?.role?.uppercase() ?: stringResource(R.string.patient),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF648DDB),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        SectionHeader(title = stringResource(R.string.account))

        SettingsItem(
            icon = R.drawable.baseline_person_24,
            title = stringResource(R.string.edit_profile),
            subtitle = stringResource(R.string.update_personal_info),
            onClick = {
                val intent = Intent(context, EditProfileActivity::class.java)
                context.startActivity(intent)
            }
        )

        SettingsItem(
            icon = R.drawable.baseline_lock_24,
            title = stringResource(R.string.change_password),
            subtitle = stringResource(R.string.update_password),
            onClick = {
                val intent = Intent(context, ChangePasswordActivity::class.java)
                context.startActivity(intent)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
        SectionHeader(title = stringResource(R.string.preferences))

        SettingsItem(
            icon = R.drawable.baseline_notifications_24,
            title = stringResource(R.string.notifications),
            subtitle = stringResource(R.string.manage_notifications),
            onClick = {
                val intent = Intent(context, NotificationSettingsActivity::class.java)
                context.startActivity(intent)
            }
        )

        SettingsItem(
            icon = R.drawable.baseline_language_24,
            title = stringResource(R.string.language),
            subtitle = selectedLanguage,
            onClick = { showLanguageDialog = true }
        )

        Spacer(modifier = Modifier.height(16.dp))
        SectionHeader(title = stringResource(R.string.help_and_support))

        SettingsItem(
            icon = R.drawable.baseline_warning_24,
            title = stringResource(R.string.help_center),
            subtitle = stringResource(R.string.get_help),
            onClick = { /* TODO */ }
        )

        SettingsItem(
            icon = R.drawable.baseline_privacy_tip_24,
            title = stringResource(R.string.privacy_policy),
            subtitle = stringResource(R.string.read_privacy),
            onClick = { /* TODO */ }
        )

        SettingsItem(
            icon = R.drawable.baseline_menu_book_24,
            title = stringResource(R.string.terms_of_service),
            subtitle = stringResource(R.string.read_terms),
            onClick = { /* TODO */ }
        )

        SettingsItem(
            icon = R.drawable.baseline_info_24,
            title = stringResource(R.string.about),
            subtitle = stringResource(R.string.version),
            onClick = { showAboutDialog = true }
        )

        Spacer(modifier = Modifier.height(16.dp))
        SectionHeader(title = stringResource(R.string.danger_zone))

        SettingsItem(
            icon = R.drawable.baseline_logout_24,
            title = stringResource(R.string.logout),
            subtitle = stringResource(R.string.sign_out),
            titleColor = Color(0xFFE53935),
            onClick = { showLogoutDialog = true }
        )

        SettingsItem(
            icon = R.drawable.baseline_delete_24,
            title = stringResource(R.string.delete_account),
            subtitle = stringResource(R.string.permanently_delete),
            titleColor = Color(0xFFE53935),
            onClick = { showDeleteDialog = true }
        )

        Spacer(modifier = Modifier.height(80.dp))
    }

    // Logout Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(stringResource(R.string.logout)) },
            text = { Text(stringResource(R.string.logout_confirmation)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.logout()
                        sharedPrefs.edit().clear().apply()
                        val intent = Intent(context, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                        context.finish()
                    }
                ) {
                    Text(stringResource(R.string.logout), color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    // Delete Account Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete_account)) },
            text = { Text(stringResource(R.string.delete_confirmation)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (userId != null) {
                            viewModel.deleteAccount(userId) { success, message ->
                                if (success) {
                                    viewModel.logout()
                                    sharedPrefs.edit().clear().apply()
                                    val intent = Intent(context, LoginActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    context.startActivity(intent)
                                    context.finish()
                                }
                            }
                        }
                    }
                ) {
                    Text(stringResource(R.string.delete), color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    // Language Selection Dialog - THE IMPORTANT PART!
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text(stringResource(R.string.select_language)) },
            text = {
                Column {
                    // English Option
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                LanguageManager.setLanguage(context, LanguageManager.ENGLISH)
                                (context as BaseActivity).recreate()
                                showLanguageDialog = false
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentLanguage == LanguageManager.ENGLISH,
                            onClick = {
                                LanguageManager.setLanguage(context, LanguageManager.ENGLISH)
                                (context as BaseActivity).recreate()
                                showLanguageDialog = false
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.english))
                    }

                    // Nepali Option
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                LanguageManager.setLanguage(context, LanguageManager.NEPALI)
                                (context as BaseActivity).recreate()
                                showLanguageDialog = false
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentLanguage == LanguageManager.NEPALI,
                            onClick = {
                                LanguageManager.setLanguage(context, LanguageManager.NEPALI)
                                (context as BaseActivity).recreate()
                                showLanguageDialog = false
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.nepali))
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text(stringResource(R.string.close))
                }
            }
        )
    }

    // About Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text(stringResource(R.string.about_medhome)) },
            text = {
                Column {
                    Text(stringResource(R.string.about_version))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(stringResource(R.string.about_description))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(stringResource(R.string.about_copyright))
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text(stringResource(R.string.close))
                }
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