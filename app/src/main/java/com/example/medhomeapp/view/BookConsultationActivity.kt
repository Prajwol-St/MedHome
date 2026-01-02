package com.example.medhomeapp.view

import AppointmentModel
import BookConsultationScreen
import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medhomeapp.BaseActivity
import com.example.medhomeapp.R
import com.example.medhomeapp.model.DoctorModel
import com.example.medhomeapp.repository.AppointmentRepoImpl
import com.example.medhomeapp.repository.DoctorRepoImpl
import com.example.medhomeapp.ui.theme.Blue10
import com.example.medhomeapp.view.ui.theme.MedHomeAppTheme
import com.example.medhomeapp.viewmodel.AppointmentViewModel
import com.example.medhomeapp.viewmodel.AppointmentViewModelFactory
import com.example.medhomeapp.viewmodel.DoctorViewModel

class BookConsultationActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appointmentViewModel: AppointmentViewModel = viewModel(
                factory = AppointmentViewModelFactory(
                    AppointmentRepoImpl(),
                    DoctorRepoImpl()
                )
            )
            val doctorViewModel: DoctorViewModel = viewModel()

            BookConsultationScreen(
                appointmentViewModel = appointmentViewModel,
                doctorViewModel = doctorViewModel
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookConsultationBody(){
    val context = LocalContext.current
    val activity = context as Activity

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Blue10,
                        titleContentColor = White,
                        navigationIconContentColor = White,
                        actionIconContentColor = White,
                    ),
                    title = { Text("Book Consultation") },
                    navigationIcon = {
                        IconButton(onClick = { activity.finish() }) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_arrow_back_ios_new_24),
                                contentDescription = null
                            )
                        }
                    },
                )
            }
        }

    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

        }

    }
}

@Composable
fun BookConsultationScreen(
    appointmentViewModel: AppointmentViewModel,
    doctorViewModel: DoctorViewModel
) {
    val doctors by doctorViewModel.doctors.collectAsStateWithLifecycle()

    var selectedDoctorId by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }

    // Load doctors when screen opens
    LaunchedEffect(Unit) {
        doctorViewModel.loadDoctors()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Choose Doctor",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (doctors.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(8.dp))
                Text("Loading doctors...")
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(doctors) { doctor ->
                    DoctorCard(
                        doctor = doctor,
                        onSelect = { selectedDoctorId = it }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Booking form
        if (selectedDoctorId.isNotEmpty()) {

            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                label = { Text("Date (yyyy-MM-dd)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = time,
                onValueChange = { time = it },
                label = { Text("Time (HH:mm)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = reason,
                onValueChange = { reason = it },
                label = { Text("Reason for Visit") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val patientId =
                        appointmentViewModel.getCurrentUserId() ?: return@Button

                    val appointment = AppointmentModel(
                        patientId = patientId,
                        doctorId = selectedDoctorId,
                        date = date,
                        time = time,
                        reason = reason
                    )

                    appointmentViewModel.addAppointment(appointment)
                }
            ) {
                Text("Confirm Booking")
            }
        }
    }
}

@Composable
fun DoctorCard(
    doctor: DoctorModel,
    onSelect: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = doctor.name,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = doctor.specialization,
                color = MaterialTheme.colorScheme.primary
            )

            Text(text = doctor.type)

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onSelect(doctor.userId) }
            ) {
                Text("Select Doctor")
            }
        }
    }
}
