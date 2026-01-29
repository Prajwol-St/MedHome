package com.example.medhomeapp.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medhomeapp.model.DoctorModel
import com.example.medhomeapp.repository.DoctorRepoImpl
import com.example.medhomeapp.view.ui.theme.MedHomeAppTheme
import java.util.*

class AddDoctorActivity : ComponentActivity() {

    private val doctorRepo = DoctorRepoImpl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MedHomeAppTheme {
                Scaffold(
                    topBar = { SmallTopAppBar(title = { Text("Add Doctor") }) },
                    content = { padding ->
                        AddDoctorScreen(modifier = Modifier.padding(padding))
                    }
                )
            }
        }
    }

    @Composable
    fun AddDoctorScreen(modifier: Modifier = Modifier) {
        var name by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var contact by remember { mutableStateOf("") }
        var specialization by remember { mutableStateOf("") }

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Create a new doctor", fontSize = 20.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)

            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
            OutlinedTextField(value = contact, onValueChange = { contact = it }, label = { Text("Contact") })
            OutlinedTextField(value = specialization, onValueChange = { specialization = it }, label = { Text("Specialization") })

            Button(
                onClick = {
                    if (name.isBlank() || email.isBlank() || contact.isBlank() || specialization.isBlank()) {
                        Toast.makeText(this@AddDoctorActivity, "Fill all fields", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val doctor = DoctorModel(
                        id = UUID.randomUUID().toString(),
                        name = name,
                        email = email,
                        role = "doctor",
                        specialization = specialization
                    )

                    doctorRepo.addDoctor(doctor) { success, msg ->
                        Toast.makeText(this@AddDoctorActivity, msg, Toast.LENGTH_SHORT).show()
                        if (success) {
                            // Clear fields
                            name = ""
                            email = ""
                            contact = ""
                            specialization = ""
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Doctor")
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SmallTopAppBar(title: @Composable () -> Unit) {
        TopAppBar(title = title)
    }
}
