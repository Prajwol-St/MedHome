import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.example.medhomeapp.model.DoctorModel
import com.example.medhomeapp.viewmodel.AppointmentViewModel
import com.example.medhomeapp.viewmodel.DoctorViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun BookConsultationScreen(
    appointmentViewModel: AppointmentViewModel,
    doctorViewModel: DoctorViewModel
) {
    // State
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
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {

        Text(
            text = "Choose Doctor",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Show spinner if doctors are still loading
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
            // Show doctor list
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(doctors) { doctor ->
                    DoctorCard(doctor) { doctorId ->
                        selectedDoctorId = doctorId
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Show booking form only after selecting a doctor
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
                label = { Text("Reason") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val patientId = appointmentViewModel.getCurrentUserId() ?: return@Button
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
    onBookClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(doctor.name, style = MaterialTheme.typography.titleLarge)
            Text(doctor.specialization, color = MaterialTheme.colorScheme.primary)
            Text(doctor.type)

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onBookClick(doctor.userId) }
            ) {
                Text("Select Doctor")
            }
        }
    }
}
