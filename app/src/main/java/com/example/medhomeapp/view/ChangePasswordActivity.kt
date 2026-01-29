package com.example.medhomeapp.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medhomeapp.BaseActivity
import com.example.medhomeapp.R
import com.example.medhomeapp.repository.UserRepoImpl
import com.example.medhomeapp.ui.theme.BackgroundCream
import com.example.medhomeapp.ui.theme.LightSage
import com.example.medhomeapp.ui.theme.SageGreen
import com.example.medhomeapp.ui.theme.TextDark
import com.example.medhomeapp.ui.theme.TextGray
import com.example.medhomeapp.viewmodel.UserViewModel

class ChangePasswordActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ChangePasswordScreen()
        }
    }
}

@Composable
fun ChangePasswordScreen() {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val viewModel = remember { UserViewModel(UserRepoImpl()) }

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val isLoading by viewModel.loading

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SageGreen)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { (context as ComponentActivity).finish() }) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_arrow_back_24),
                        contentDescription = stringResource(R.string.cd_back),
                        tint = Color.White
                    )
                }
                Text(
                    text = stringResource(R.string.title_change_password),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .fillMaxHeight(0.82f),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            colors = CardDefaults.cardColors(containerColor = BackgroundCream),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 28.dp)
                    .padding(top = 32.dp, bottom = 32.dp)
            ) {
                Text(
                    text = stringResource(R.string.security),
                    style = TextStyle(
                        color = TextDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.security_desc),
                    style = TextStyle(
                        color = TextGray,
                        fontSize = 13.sp
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = stringResource(R.string.label_current_password),
                    style = TextStyle(
                        color = TextDark,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    placeholder = {
                        Text(
                            text = stringResource(R.string.label_new_password),
                            color = TextGray.copy(alpha = 0.6f),
                            fontSize = 14.sp
                        )
                    },
                    enabled = !isLoading,
                    trailingIcon = {
                        IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }) {
                            Icon(
                                painter = if (currentPasswordVisible)
                                    painterResource(R.drawable.baseline_visibility_off_24)
                                else
                                    painterResource(R.drawable.baseline_visibility_24),
                                contentDescription = if (currentPasswordVisible) "Hide password" else "Show password",
                                tint = TextGray
                            )
                        }
                    },
                    visualTransformation = if (currentPasswordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = SageGreen,
                        unfocusedIndicatorColor = LightSage,
                        cursorColor = SageGreen,
                        focusedTextColor = TextDark,
                        unfocusedTextColor = TextDark,
                        disabledContainerColor = Color.White.copy(alpha = 0.5f)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = stringResource(R.string.label_new_password),
                    style = TextStyle(
                        color = TextDark,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    placeholder = {
                        Text(
                            text = stringResource(R.string.ph_confirm_password),
                            color = TextGray.copy(alpha = 0.6f),
                            fontSize = 14.sp
                        )
                    },
                    enabled = !isLoading,
                    trailingIcon = {
                        IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                            Icon(
                                painter = if (newPasswordVisible)
                                    painterResource(R.drawable.baseline_visibility_off_24)
                                else
                                    painterResource(R.drawable.baseline_visibility_24),
                                contentDescription =
                                    if (currentPasswordVisible)
                                        stringResource(R.string.hide_password)
                                    else
                                        stringResource(R.string.show_password),
                                tint = TextGray
                            )
                        }
                    },
                    visualTransformation = if (newPasswordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = SageGreen,
                        unfocusedIndicatorColor = LightSage,
                        cursorColor = SageGreen,
                        focusedTextColor = TextDark,
                        unfocusedTextColor = TextDark,
                        disabledContainerColor = Color.White.copy(alpha = 0.5f)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = stringResource(R.string.label_confirm_new_password),
                    style = TextStyle(
                        color = TextDark,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = {
                        Text(
                            stringResource(R.string.ph_new_password),
                            color = TextGray.copy(alpha = 0.6f),
                            fontSize = 14.sp
                        )
                    },
                    enabled = !isLoading,
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                painter = if (confirmPasswordVisible)
                                    painterResource(R.drawable.baseline_visibility_off_24)
                                else
                                    painterResource(R.drawable.baseline_visibility_24),
                                contentDescription =
                                    if (currentPasswordVisible)
                                        stringResource(R.string.hide_password)
                                    else
                                        stringResource(R.string.show_password),
                                tint = TextGray
                            )
                        }
                    },
                    visualTransformation = if (confirmPasswordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = SageGreen,
                        unfocusedIndicatorColor = LightSage,
                        cursorColor = SageGreen,
                        focusedTextColor = TextDark,
                        unfocusedTextColor = TextDark,
                        disabledContainerColor = Color.White.copy(alpha = 0.5f)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = {
                        when {
                            currentPassword.isBlank() -> {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.err_current_password),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            newPassword.isBlank() -> {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.err_new_password),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            newPassword.length < 6 -> {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.err_password_length),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            newPassword != confirmPassword -> {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.err_password_mismatch),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            currentPassword == newPassword -> {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.err_same_password),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            else -> {
                                viewModel.changePassword(currentPassword, newPassword) { success, message ->
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    if (success) {
                                        (context as BaseActivity).finish()
                                    }
                                }
                            }
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TextDark,
                        disabledContainerColor = TextDark.copy(alpha = 0.6f)
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            stringResource(R.string.btn_change_password),
                            style = TextStyle(
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = SageGreen.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_info_24),
                            contentDescription = null,
                            tint = SageGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = stringResource(R.string.password_requirements),
                                style = TextStyle(
                                    color = TextDark,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(R.string.password_rules),
                                style = TextStyle(
                                    color = TextGray,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}