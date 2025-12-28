package com.example.medhomeapp.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import com.example.medhomeapp.ui.theme.DeepSage
import com.example.medhomeapp.ui.theme.LightSage
import com.example.medhomeapp.ui.theme.SageGreen
import com.example.medhomeapp.ui.theme.TextDark
import com.example.medhomeapp.ui.theme.TextGray
import com.example.medhomeapp.utils.AuthState
import com.example.medhomeapp.viewmodel.UserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

class LoginActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            LoginBody()
        }
    }
}

@Composable
fun LoginBody() {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val viewModel = remember { UserViewModel(UserRepoImpl()) }

    val sharedPrefs = context.getSharedPreferences("MedHomePrefs", Context.MODE_PRIVATE)
    val savedEmail = sharedPrefs.getString("saved_email", "") ?: ""
    val rememberMeChecked = sharedPrefs.getBoolean("remember_me", false)

    var email by remember { mutableStateOf(savedEmail) }
    var password by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(rememberMeChecked) }
    var isGoogleLoading by remember { mutableStateOf(false) }

    val authState by viewModel.authState
    val currentUser by viewModel.currentUser
    val isLoading = authState is AuthState.Loading || isGoogleLoading

    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }

    val googleSignInClient = remember {
        GoogleSignIn.getClient(context, gso)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken

            if (idToken.isNullOrEmpty()) {
                Toast.makeText(context, "Google Sign-In failed: no ID token", Toast.LENGTH_SHORT).show()
                isGoogleLoading = false
                return@rememberLauncherForActivityResult
            }

            isGoogleLoading = true

            val credential = GoogleAuthProvider.getCredential(idToken, null)
            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        val userId = FirebaseAuth.getInstance().currentUser?.uid
                        if (userId != null) {
                            viewModel.getUserByID(userId)

                            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                                kotlinx.coroutines.delay(1000)
                                isGoogleLoading = false

                                val user = viewModel.currentUser.value
                                if (user != null) {
                                    sharedPrefs.edit().putString("user_id", userId).apply()
                                    Toast.makeText(context, "Welcome back, ${user.name}!", Toast.LENGTH_SHORT).show()

                                    val intent = Intent(context, DashboardActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    context.startActivity(intent)
                                    (context as BaseActivity).finish()
                                } else {
                                    Toast.makeText(context, "Please complete your profile", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(context, SignupDetailsActivity::class.java)
                                    intent.putExtra("email", account.email ?: "")
                                    intent.putExtra("googleUid", userId)
                                    intent.putExtra("googleName", account.displayName ?: "")
                                    context.startActivity(intent)
                                    (context as BaseActivity).finish()
                                }
                            }
                        } else {
                            isGoogleLoading = false
                            Toast.makeText(context, "Failed to get user ID", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        isGoogleLoading = false
                        Toast.makeText(context, "Firebase Sign-In failed", Toast.LENGTH_SHORT).show()
                    }
                }

        } catch (e: ApiException) {
            isGoogleLoading = false
            Toast.makeText(context, "Google Sign-In failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthState.Success -> {
                currentUser?.let { user ->
                    sharedPrefs.edit().putString("user_id", user.id).apply()

                    if (rememberMe) {
                        sharedPrefs.edit()
                            .putString("saved_email", email)
                            .putBoolean("remember_me", true)
                            .apply()
                    } else {
                        sharedPrefs.edit()
                            .remove("saved_email")
                            .putBoolean("remember_me", false)
                            .apply()
                    }

                    Toast.makeText(context, "Welcome back, ${user.name}!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(context, DashboardActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                    (context as BaseActivity).finish()
                }
                viewModel.resetAuthState()
            }
            is AuthState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                viewModel.resetAuthState()
            }
            else -> {}
        }
    }

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
                .padding(top = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "MedHome",
                style = TextStyle(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    letterSpacing = 1.sp
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Your health, our priority",
                style = TextStyle(
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp
                )
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .fillMaxHeight(0.75f),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            colors = CardDefaults.cardColors(containerColor = BackgroundCream),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 28.dp)
                    .padding(top = 40.dp, bottom = 24.dp)
            ) {
                Text(
                    text = "Sign in",
                    style = TextStyle(
                        color = TextDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Welcome! Please enter your details.",
                    style = TextStyle(
                        color = TextGray,
                        fontSize = 14.sp
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Username",
                    style = TextStyle(
                        color = TextDark,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("Enter your email", color = TextGray.copy(alpha = 0.6f)) },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        focusedIndicatorColor = SageGreen,
                        unfocusedIndicatorColor = LightSage,
                        cursorColor = SageGreen,
                        focusedTextColor = TextDark,
                        unfocusedTextColor = TextDark
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Password",
                    style = TextStyle(
                        color = TextDark,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("Enter your password", color = TextGray.copy(alpha = 0.6f)) },
                    enabled = !isLoading,
                    trailingIcon = {
                        IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                            Icon(
                                painter = if (passwordVisibility)
                                    painterResource(R.drawable.baseline_visibility_off_24)
                                else painterResource(R.drawable.baseline_visibility_24),
                                contentDescription = null,
                                tint = TextGray
                            )
                        }
                    },
                    visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        focusedIndicatorColor = SageGreen,
                        unfocusedIndicatorColor = LightSage,
                        cursorColor = SageGreen,
                        focusedTextColor = TextDark,
                        unfocusedTextColor = TextDark
                    )
                )

                Spacer(modifier = Modifier.height(5.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it },
                            enabled = !isLoading,
                            colors = CheckboxDefaults.colors(
                                checkedColor = SageGreen,
                                checkmarkColor = Color.White,
                                uncheckedColor = LightSage
                            )
                        )
                        Text(
                            text = "Remember me",
                            style = TextStyle(
                                color = TextDark,
                                fontSize = 13.sp
                            )
                        )
                    }
                    Text(
                        text = "Forgot password?",
                        style = TextStyle(
                            color = SageGreen,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.clickable(enabled = !isLoading) {
                            val intent = Intent(context, ForgotPasswordActivity::class.java)
                            context.startActivity(intent)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))
                Button(
                    onClick = {
                        if (email.isBlank()) {
                            Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            Toast.makeText(context, "Please enter a valid email", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (password.isBlank()) {
                            Toast.makeText(context, "Please enter your password", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        viewModel.login(email, password)
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
                    if (authState is AuthState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Sign up",
                            style = TextStyle(
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        thickness = 1.dp,
                        color = LightSage
                    )
                    Text(
                        text = "Or",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = TextStyle(
                            color = TextGray,
                            fontSize = 14.sp
                        )
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        thickness = 1.dp,
                        color = LightSage
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                OutlinedButton(
                    onClick = {
                        launcher.launch(googleSignInClient.signInIntent)
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, LightSage),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White
                    )
                ) {
                    if (isGoogleLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = SageGreen,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            painter = painterResource(R.drawable.google),
                            contentDescription = "Google Logo",
                            modifier = Modifier.size(22.dp),
                            tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Continue with Google",
                            style = TextStyle(
                                color = TextDark,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Don't have an account? ",
                        style = TextStyle(
                            color = TextGray,
                            fontSize = 14.sp
                        )
                    )
                    Text(
                        text = "Register",
                        style = TextStyle(
                            color = SageGreen,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.clickable(enabled = !isLoading) {
                            val intent = Intent(context, SignupInitialActivity::class.java)
                            context.startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}