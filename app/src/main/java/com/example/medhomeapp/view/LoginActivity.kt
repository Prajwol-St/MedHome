package com.example.medhomeapp.view

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medhomeapp.R


class LoginActivity : ComponentActivity() {
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

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }

    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }


    val focusManager = LocalFocusManager.current
    val context = LocalContext.current


    Scaffold() { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(White)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ){
                    focusManager.clearFocus()
                }
        ) {
            Spacer(modifier = Modifier.height(50.dp))

            Text(
                "Login",
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    color = Color(0xFF648DDB),
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ),
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 24.dp)


                )
            HorizontalDivider(
                thickness = 1.dp,
                color = Color(0xFF648DDB),
            )
            Spacer(modifier = Modifier.height(32.dp))



            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = ""
                },
                label = { Text("Email/Phone") },
                isError = emailError.isNotEmpty(),
                supportingText = {
                    if (emailError.isNotEmpty())
                        Text(
                            text = emailError,
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                },
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth(),
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
            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = ""
                                },
                label = { Text("Password") },
                isError = passwordError.isNotEmpty(),
                supportingText = {
                    if (passwordError.isNotEmpty())
                    {
                        Text(
                            text = passwordError,
                            color = Color.Red,
                            fontSize =  12.sp
                        )
                    }
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            passwordVisibility = !passwordVisibility
                        }
                    ) {
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
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth(),
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
            Text(
                text = "Forgot Password?",
                color = Color(0xFF648DDB),
                fontSize = 15.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 6.dp),
                textAlign = TextAlign.End

            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    var isValid = true


                    if (email.isEmpty()) {
                        emailError = "Email is required"
                        isValid = false
                    }

                    else if (!isValidEmail(email)) {
                        emailError = "Invalid Email Format"
                        isValid = false
                    }


                        if (isValid) {
                            Toast.makeText(
                                context,
                                "Login Successfull!", //firebasecnnectionnotdoneyet
                                Toast.LENGTH_SHORT
                            ).show()

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
                    text = "Continue",
                    color = White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalAlignment =Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier
                        .weight(1f),
                    thickness = 1.dp,
                    color = Color.LightGray
                )
                Text(
                    text = "or",
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    color = Color.Gray,
                    fontSize = 15.sp
                )
                HorizontalDivider(
                    modifier = Modifier
                        .weight(1f),
                    thickness = 1.dp,
                    color = Color.LightGray
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp )
                    .height(    50.dp),
                shape = RoundedCornerShape(15.dp),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_phone_24),
                    contentDescription = "Phone Icon",
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 8.dp),
                    tint = Color.Unspecified
                )
                Text(
                    text = "Login  with number",
                    color = Color.Black,
                    fontSize =  16.sp,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(15.dp),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Icon(
                    painter = painterResource(R.drawable.google),
                    contentDescription = "Google Logo",
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 8.dp ),
                    tint = Color.Unspecified
                )
                Text(
                    text = "Login With Google",
                    color = Color.Black,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Don't have an account?",
                    color = Color.Gray,
                    fontSize = 14.sp,
                )
                Text(
                    text = "Sign Up",
                    color = Color(0XFF648DDB),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable{
                        val intent = Intent(context, SignupActivity::class.java)
                        context.startActivity(intent)
                    }
                )
            }


        }
    }



}
fun isValidEmail(email: String): Boolean{
    return email.contains("@") &&
            email.indexOf("@")>0 &&
            email.indexOf("@") <email.length -1
}

fun isValidPassword(password: String): Boolean {
    return password.length >= 6
}

@Preview
@Composable
fun PreviewLogin() {
    LoginBody()
}
