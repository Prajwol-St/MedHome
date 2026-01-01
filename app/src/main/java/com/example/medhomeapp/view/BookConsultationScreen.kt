import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.medhomeapp.model.AppointmentModel
import com.example.medhomeapp.model.DoctorModel
import com.example.medhomeapp.viewmodel.AppointmentViewModel

@Composable
fun BookConsultationScreen(viewModel: AppointmentViewModel) {

    val doctors by viewModel.doctors.collectAsState()
    var selectedDoctorId by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }

    Column(Modifier.padding(16.dp)) {

        Text("Choose Doctor", style = MaterialTheme.typography.headlineMedium)

        doctors.forEach { doctor ->
            DoctorCard(doctor) {
                selectedDoctorId = it
            }
        }

        if (selectedDoctorId.isNotEmpty()) {

            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                label = { Text("Date") }
            )

            OutlinedTextField(
                value = time,
                onValueChange = { time = it },
                label = { Text("Time") }
            )

            OutlinedTextField(
                value = reason,
                onValueChange = { reason = it },
                label = { Text("Reason") }
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    viewModel.bookAppointment(
                        selectedDoctorId,
                        date,
                        time,
                        reason
                    )
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
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(Modifier.padding(16.dp)) {

            Text(doctor.name, style = MaterialTheme.typography.titleLarge)
            Text(doctor.specialization, color = MaterialTheme.colorScheme.primary)

            Spacer(Modifier.height(8.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onBookClick(doctor.id.toString()) }
            ) {
                Text("Book Now")
            }
        }
    }
}
