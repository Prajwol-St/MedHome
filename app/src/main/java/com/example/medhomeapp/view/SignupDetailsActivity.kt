package com.example.medhomeapp.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.medhomeapp.R
import com.example.medhomeapp.viewmodel.AuthViewModel

class SignupDetailsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        val emailFromIntent = intent.getStringExtra("email") ?: ""
        val googleUid = intent.getStringExtra("googleUid")
        val googleName = intent.getStringExtra("googleName")

        setContent {
            SignupDetailsBody(authViewModel, emailFromIntent, googleUid, googleName)
        }
    }
}

@Composable
fun SignupDetailsBody(
    authViewModel: AuthViewModel,
    emailFromIntent: String,
    googleUid: String? = null,
    googleName: String? = null
) {

    val context = LocalContext.current
    val isGoogleSignup = googleUid != null

    val name = remember { mutableStateOf(googleName ?: "") }
    val email = remember { mutableStateOf(emailFromIntent) }
    val password = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }
    val contact = remember { mutableStateOf("") }
    val gender = remember { mutableStateOf("") }
    val dateOfBirth = remember { mutableStateOf("") }
    val bloodGroup = remember { mutableStateOf("") }
    val emergencyContact = remember { mutableStateOf("") }
    val address = remember { mutableStateOf("") }
    val passwordVisibility = remember { mutableStateOf(false) }
    val confirmPasswordVisibility = remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(White)
                .verticalScroll(scrollState)
                .padding(padding)
                .padding(bottom = 50.dp)
        ) {

            Spacer(modifier = Modifier.height(50.dp))

            Text(
                text = "Complete Your Profile",
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    color = Color(0xFF648DDB),
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            )

            HorizontalDivider(
                thickness = 1.dp,
                color = Color(0xFF648DDB)
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = name.value,
                onValueChange = { name.value = it },
                label = { Text("Full Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(15.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color(0xFF648DDB),
                    unfocusedIndicatorColor = Color(0xFF648DDB),
                    focusedContainerColor = White,
                    unfocusedContainerColor = White,
                    focusedLabelColor = Color(0xFF648DDB),
                    unfocusedLabelColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email.value,
                onValueChange = { },
                label = { Text("Email") },
                enabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(15.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color(0xFF648DDB),
                    unfocusedIndicatorColor = Color(0xFF648DDB),
                    disabledContainerColor = Color(0xFFF5F5F5),
                    disabledTextColor = Color.Gray,
                    focusedLabelColor = Color(0xFF648DDB),
                    unfocusedLabelColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = contact.value,
                onValueChange = { contact.value = it },
                label = { Text("Contact Number") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(15.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color(0xFF648DDB),
                    unfocusedIndicatorColor = Color(0xFF648DDB),
                    focusedContainerColor = White,
                    unfocusedContainerColor = White,
                    focusedLabelColor = Color(0xFF648DDB),
                    unfocusedLabelColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = gender.value,
                onValueChange = { gender.value = it },
                label = { Text("Gender") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(15.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color(0xFF648DDB),
                    unfocusedIndicatorColor = Color(0xFF648DDB),
                    focusedContainerColor = White,
                    unfocusedContainerColor = White,
                    focusedLabelColor = Color(0xFF648DDB),
                    unfocusedLabelColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = dateOfBirth.value,
                onValueChange = { dateOfBirth.value = it },
                label = { Text("Date of Birth") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(15.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color(0xFF648DDB),
                    unfocusedIndicatorColor = Color(0xFF648DDB),
                    focusedContainerColor = White,
                    unfocusedContainerColor = White,
                    focusedLabelColor = Color(0xFF648DDB),
                    unfocusedLabelColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = bloodGroup.value,
                onValueChange = { bloodGroup.value = it },
                label = { Text("Blood Group") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(15.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color(0xFF648DDB),
                    unfocusedIndicatorColor = Color(0xFF648DDB),
                    focusedContainerColor = White,
                    unfocusedContainerColor = White,
                    focusedLabelColor = Color(0xFF648DDB),
                    unfocusedLabelColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = emergencyContact.value,
                onValueChange = { emergencyContact.value = it },
                label = { Text("Emergency Contact") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(15.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color(0xFF648DDB),
                    unfocusedIndicatorColor = Color(0xFF648DDB),
                    focusedContainerColor = White,
                    unfocusedContainerColor = White,
                    focusedLabelColor = Color(0xFF648DDB),
                    unfocusedLabelColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = address.value,
                onValueChange = { address.value = it },
                label = { Text("Address") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(15.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color(0xFF648DDB),
                    unfocusedIndicatorColor = Color(0xFF648DDB),
                    focusedContainerColor = White,
                    unfocusedContainerColor = White,
                    focusedLabelColor = Color(0xFF648DDB),
                    unfocusedLabelColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (!isGoogleSignup) {
                OutlinedTextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    label = { Text("Password") },
                    trailingIcon = {
                        IconButton(onClick = {
                            passwordVisibility.value = !passwordVisibility.value
                        }) {
                            Icon(
                                painter = if (passwordVisibility.value)
                                    painterResource(R.drawable.baseline_visibility_off_24)
                                else
                                    painterResource(R.drawable.baseline_visibility_24),
                                contentDescription = null
                            )
                        }
                    },
                    visualTransformation = if (passwordVisibility.value)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(15.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color(0xFF648DDB),
                        unfocusedIndicatorColor = Color(0xFF648DDB),
                        focusedContainerColor = White,
                        unfocusedContainerColor = White,
                        focusedLabelColor = Color(0xFF648DDB),
                        unfocusedLabelColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = confirmPassword.value,
                    onValueChange = { confirmPassword.value = it },
                    label = { Text("Confirm Password") },
                    trailingIcon = {
                        IconButton(onClick = {
                            confirmPasswordVisibility.value = !confirmPasswordVisibility.value
                        }) {
                            Icon(
                                painter = if (confirmPasswordVisibility.value)
                                    painterResource(R.drawable.baseline_visibility_off_24)
                                else
                                    painterResource(R.drawable.baseline_visibility_24),
                                contentDescription = null
                            )
                        }
                    },
                    visualTransformation = if (confirmPasswordVisibility.value)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(15.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color(0xFF648DDB),
                        unfocusedIndicatorColor = Color(0xFF648DDB),
                        focusedContainerColor = White,
                        unfocusedContainerColor = White,
                        focusedLabelColor = Color(0xFF648DDB),
                        unfocusedLabelColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (name.value.isEmpty()) {
                        Toast.makeText(context, "Please enter your name", Toast.LENGTH_SHORT).show()
                    } else if (contact.value.isEmpty()) {
                        Toast.makeText(context, "Please enter contact number", Toast.LENGTH_SHORT).show()
                    } else if (gender.value.isEmpty()) {
                        Toast.makeText(context, "Please enter gender", Toast.LENGTH_SHORT).show()
                    } else if (dateOfBirth.value.isEmpty()) {
                        Toast.makeText(context, "Please enter date of birth", Toast.LENGTH_SHORT).show()
                    } else if (bloodGroup.value.isEmpty()) {
                        Toast.makeText(context, "Please enter blood group", Toast.LENGTH_SHORT).show()
                    } else if (emergencyContact.value.isEmpty()) {
                        Toast.makeText(context, "Please enter emergency contact", Toast.LENGTH_SHORT).show()
                    } else if (address.value.isEmpty()) {
                        Toast.makeText(context, "Please enter address", Toast.LENGTH_SHORT).show()
                    } else if (!isGoogleSignup && password.value.isEmpty()) {
                        Toast.makeText(context, "Please enter password", Toast.LENGTH_SHORT).show()
                    } else if (!isGoogleSignup && password.value.length < 6) {
                        Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                    } else if (!isGoogleSignup && password.value != confirmPassword.value) {
                        Toast.makeText(context, "Passwords don't match", Toast.LENGTH_SHORT).show()
                    } else {
                        authViewModel.checkPhoneExists(contact.value) { phoneExists ->
                            if (phoneExists) {
                                Toast.makeText(context, "Phone number already in use", Toast.LENGTH_SHORT).show()
                            } else {
                                authViewModel.register(
                                    email = email.value,
                                    password = if (isGoogleSignup) "" else password.value,
                                    name = name.value,
                                    contact = contact.value,
                                    gender = gender.value,
                                    dateOfBirth = dateOfBirth.value,
                                    bloodGroup = bloodGroup.value,
                                    emergencyContact = emergencyContact.value,
                                    address = address.value
                                ) { success, message ->
                                    if (success) {
                                        Toast.makeText(context, "Account created successfully!", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(context, LoginActivity::class.java)
                                        context.startActivity(intent)
                                        (context as ComponentActivity).finish()
                                    } else {
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF648DDB)
                )
            ) {
                Text(
                    text = if (isGoogleSignup) "Complete Profile" else "Sign Up",
                    color = White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Already have an account? ",
                    color = Color.Gray,
                    fontSize = 14.sp
                )

                Text(
                    text = "Login",
                    color = Color(0xFF648DDB),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        val intent = Intent(context, LoginActivity::class.java)
                        context.startActivity(intent)
                        (context as ComponentActivity).finish()
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}