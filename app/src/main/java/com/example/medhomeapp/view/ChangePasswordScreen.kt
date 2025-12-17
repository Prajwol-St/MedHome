package com.example.medhomeapp.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medhomeapp.R
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ChangePasswordScreen(onBackPress: () -> Unit) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val auth = FirebaseAuth.getInstance()
    val scrollState = rememberScrollState()

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var showCurrentPassword by remember { mutableStateOf(false) }
    var showNewPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
            }
            .verticalScroll(scrollState)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackPress) {
                Icon(
                    painter = painterResource(R.drawable.baseline_arrow_back_24),
                    contentDescription = "Back",
                    tint = Color(0xFF648DDB)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))


        Text(
            text = "Update your account password",
            style = TextStyle(
                color = Color.Gray,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))


        OutlinedTextField(
            value = currentPassword,
            onValueChange = { currentPassword = it },
            label = { Text("Current Password") },
            enabled = !isLoading,
            trailingIcon = {
                IconButton(onClick = { showCurrentPassword = !showCurrentPassword }) {
                    Icon(
                        painter = if (showCurrentPassword)
                            painterResource(R.drawable.baseline_visibility_off_24)
                        else painterResource(R.drawable.baseline_visibility_24),
                        contentDescription = null,
                        tint = Color(0xFF648DDB)
                    )
                }
            },
            visualTransformation = if (showCurrentPassword)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(15.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color(0xFF648DDB),
                unfocusedIndicatorColor = Color(0xFF648DDB),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedLabelColor = Color(0xFF648DDB),
                unfocusedLabelColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(20.dp))


        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("New Password") },
            enabled = !isLoading,
            trailingIcon = {
                IconButton(onClick = { showNewPassword = !showNewPassword }) {
                    Icon(
                        painter = if (showNewPassword)
                            painterResource(R.drawable.baseline_visibility_off_24)
                        else painterResource(R.drawable.baseline_visibility_24),
                        contentDescription = null,
                        tint = Color(0xFF648DDB)
                    )
                }
            },
            visualTransformation = if (showNewPassword)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(15.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color(0xFF648DDB),
                unfocusedIndicatorColor = Color(0xFF648DDB),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedLabelColor = Color(0xFF648DDB),
                unfocusedLabelColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(20.dp))


        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm New Password") },
            enabled = !isLoading,
            trailingIcon = {
                IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                    Icon(
                        painter = if (showConfirmPassword)
                            painterResource(R.drawable.baseline_visibility_off_24)
                        else painterResource(R.drawable.baseline_visibility_24),
                        contentDescription = null,
                        tint = Color(0xFF648DDB)
                    )
                }
            },
            visualTransformation = if (showConfirmPassword)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(15.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color(0xFF648DDB),
                unfocusedIndicatorColor = Color(0xFF648DDB),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedLabelColor = Color(0xFF648DDB),
                unfocusedLabelColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(8.dp))


        Text(
            text = "• Password must be at least 6 characters\n• Use a mix of letters and numbers for security",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 28.dp),
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(32.dp))


        Button(
            onClick = {
                // Validation
                if (currentPassword.isBlank()) {
                    Toast.makeText(context, "Enter current password", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                if (newPassword.isBlank()) {
                    Toast.makeText(context, "Enter new password", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                if (confirmPassword.isBlank()) {
                    Toast.makeText(context, "Confirm your new password", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                if (newPassword.length < 6) {
                    Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                if (newPassword != confirmPassword) {
                    Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                if (currentPassword == newPassword) {
                    Toast.makeText(context, "New password must be different from current password", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                isLoading = true


                val user = auth.currentUser
                if (user != null && user.email != null) {
                    val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)

                    user.reauthenticate(credential)
                        .addOnCompleteListener { reAuthTask ->
                            if (reAuthTask.isSuccessful) {
                                // Update password
                                user.updatePassword(newPassword)
                                    .addOnCompleteListener { updateTask ->
                                        isLoading = false
                                        if (updateTask.isSuccessful) {
                                            Toast.makeText(
                                                context,
                                                "Password changed successfully!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            onBackPress()
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Failed: ${updateTask.exception?.message}",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                            } else {
                                isLoading = false
                                Toast.makeText(
                                    context,
                                    "Current password is incorrect",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    isLoading = false
                    Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
                }
            },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(50.dp),
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF648DDB))
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Change Password",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}