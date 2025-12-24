package com.example.medhomeapp.view

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import com.example.medhomeapp.BaseActivity
import com.example.medhomeapp.R
import com.example.medhomeapp.model.UserModel
import com.example.medhomeapp.repository.UserRepoImpl
import com.example.medhomeapp.utils.AuthState
import com.example.medhomeapp.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth

class SignupDetailsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val emailFromIntent = intent.getStringExtra("email") ?: ""
        val googleUid = intent.getStringExtra("googleUid")
        val googleName = intent.getStringExtra("googleName")

        setContent {
            SignupDetailsBody(emailFromIntent, googleUid, googleName)
        }
    }
}

@Composable
fun SignupDetailsBody(
    emailFromIntent: String,
    googleUid: String? = null,
    googleName: String? = null
) {
    val context = LocalContext.current
    val viewModel = remember { UserViewModel(UserRepoImpl()) }
    val isGoogleSignup = !googleUid.isNullOrEmpty()
    val scrollState = rememberScrollState()

    var name by remember { mutableStateOf(googleName ?: "") }
    val email by remember { mutableStateOf(emailFromIntent) }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf("") }
    var emergencyContact by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var termsAccepted by remember { mutableStateOf(false) }

    var passwordVisibility by remember { mutableStateOf(false) }
    var confirmPasswordVisibility by remember { mutableStateOf(false) }

    val authState by viewModel.authState
    val isLoading = authState is AuthState.Loading

    // Handle auth state
    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthState.Success -> {
                Toast.makeText(context, "Account created successfully!", Toast.LENGTH_SHORT).show()

                // Save session
                val sharedPrefs = (context as ComponentActivity).getSharedPreferences("MedHomePrefs", MODE_PRIVATE)
                sharedPrefs.edit().putString("user_id", state.userId).apply()

                // Go to dashboard
                val intent = Intent(context, DashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
                context.finish()

                viewModel.resetAuthState()
            }
            is AuthState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                viewModel.resetAuthState()
            }
            else -> {}
        }
    }

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

            HorizontalDivider(thickness = 1.dp, color = Color(0xFF648DDB))

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                enabled = !isLoading,
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
                value = email,
                onValueChange = { },
                label = { Text("Email") },
                enabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(15.dp),
                colors = TextFieldDefaults.colors(
                    disabledIndicatorColor = Color(0xFF648DDB),
                    disabledContainerColor = Color(0xFFF5F5F5),
                    disabledTextColor = Color.Gray,
                    disabledLabelColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = contact,
                onValueChange = { contact = it },
                label = { Text("Contact Number") },
                enabled = !isLoading,
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
                value = gender,
                onValueChange = { gender = it },
                label = { Text("Gender (Male/Female/Other)") },
                enabled = !isLoading,
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
                value = dateOfBirth,
                onValueChange = { dateOfBirth = it },
                label = { Text("Date of Birth (YYYY/MM/DD)") },
                enabled = !isLoading,
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
                value = bloodGroup,
                onValueChange = { bloodGroup = it },
                label = { Text("Blood Group (A+, B-, O+, etc.)") },
                enabled = !isLoading,
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
                value = emergencyContact,
                onValueChange = { emergencyContact = it },
                label = { Text("Emergency Contact") },
                enabled = !isLoading,
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
                value = address,
                onValueChange = { address = it },
                label = { Text("Address") },
                enabled = !isLoading,
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

            // Password fields (required for all signups)
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                enabled = !isLoading,
                trailingIcon = {
                    IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                        Icon(
                            painter = if (passwordVisibility)
                                painterResource(R.drawable.baseline_visibility_off_24)
                            else
                                painterResource(R.drawable.baseline_visibility_24),
                            contentDescription = null
                        )
                    }
                },
                visualTransformation = if (passwordVisibility)
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
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                enabled = !isLoading,
                trailingIcon = {
                    IconButton(onClick = {
                        confirmPasswordVisibility = !confirmPasswordVisibility
                    }) {
                        Icon(
                            painter = if (confirmPasswordVisibility)
                                painterResource(R.drawable.baseline_visibility_off_24)
                            else
                                painterResource(R.drawable.baseline_visibility_24),
                            contentDescription = null
                        )
                    }
                },
                visualTransformation = if (confirmPasswordVisibility)
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

            // Terms and Conditions Checkbox
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = termsAccepted,
                    onCheckedChange = { termsAccepted = it },
                    enabled = !isLoading,
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFF648DDB),
                        checkmarkColor = White
                    )
                )
                Text(
                    text = "I agree to the Terms & Conditions",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    // Validation
                    when {
                        name.isBlank() -> Toast.makeText(context, "Please enter your name", Toast.LENGTH_SHORT).show()
                        contact.isBlank() -> Toast.makeText(context, "Please enter contact number", Toast.LENGTH_SHORT).show()
                        contact.length != 10 -> Toast.makeText(context, "Contact number must be 10 digits", Toast.LENGTH_SHORT).show()
                        gender.isBlank() -> Toast.makeText(context, "Please enter gender", Toast.LENGTH_SHORT).show()
                        dateOfBirth.isBlank() -> Toast.makeText(context, "Please enter date of birth", Toast.LENGTH_SHORT).show()
                        bloodGroup.isBlank() -> Toast.makeText(context, "Please enter blood group", Toast.LENGTH_SHORT).show()
                        emergencyContact.isBlank() -> Toast.makeText(context, "Please enter emergency contact", Toast.LENGTH_SHORT).show()
                        address.isBlank() -> Toast.makeText(context, "Please enter address", Toast.LENGTH_SHORT).show()
                        password.isBlank() -> Toast.makeText(context, "Please enter password", Toast.LENGTH_SHORT).show()
                        password.length < 6 -> Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                        password != confirmPassword -> Toast.makeText(context, "Passwords don't match", Toast.LENGTH_SHORT).show()
                        !termsAccepted -> Toast.makeText(context, "Please accept Terms & Conditions", Toast.LENGTH_SHORT).show()
                        else -> {
                            // Create user model
                            val userModel = UserModel(
                                name = name,
                                email = email,
                                contact = contact,
                                gender = gender,
                                dateOfBirth = dateOfBirth,
                                bloodGroup = bloodGroup,
                                emergencyContact = emergencyContact,
                                address = address,
                                role = "patient"
                            )

                            if (isGoogleSignup && googleUid != null) {
                                // For Google signup: User already in Auth, just add to DB
                                val repo = UserRepoImpl()
                                repo.addUserToDatabase(googleUid, userModel.copy(
                                    id = googleUid,
                                    createdAt = System.currentTimeMillis().toString(),
                                    updatedAt = System.currentTimeMillis().toString()
                                )) { success, message ->
                                    if (success) {
                                        // Link password to Google account
                                        val currentUser = FirebaseAuth.getInstance().currentUser
                                        currentUser?.updatePassword(password)

                                        // Navigate to dashboard
                                        Toast.makeText(context, "Profile created successfully!", Toast.LENGTH_SHORT).show()

                                        val sharedPrefs = (context as ComponentActivity).getSharedPreferences("MedHomePrefs", MODE_PRIVATE)
                                        sharedPrefs.edit().putString("user_id", googleUid).apply()

                                        val intent = Intent(context, DashboardActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        context.startActivity(intent)
                                        context.finish()
                                    } else {
                                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                    }
                                }
                            } else {
                                // Regular signup: Create Auth + DB together
                                viewModel.register(email, password, userModel)
                            }
                        }
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
                        color = White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = if (isGoogleSignup) "Complete Profile" else "Sign Up",
                        color = White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
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
                    modifier = Modifier.clickable(enabled = !isLoading) {
                        val intent = Intent(context, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                        (context as ComponentActivity).finish()
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}