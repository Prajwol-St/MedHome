package com.example.medhomeapp.view

import AppointmentModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medhomeapp.view.ui.theme.MedHomeAppTheme

class DoctorAppointmentActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Hi()
//            DoctorAppointmentsScreen(viewModel = viewModel())
        }
    }
}

@Composable
fun Hi() {
    Text("Hwll")
}

//@Composable
//fun DoctorAppointmentsScreen(
////    viewModel: AppointmentViewModel
//) {
//    val appointments by viewModel.doctorAppointments.collectAsState()
//    val isLoading by viewModel.isLoading.collectAsState()
//
//    LaunchedEffect(Unit) {
//        viewModel.loadDoctorAppointments()
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//    ) {
//
//        Text(
//            text = "My Appointments",
//            style = MaterialTheme.typography.headlineMedium
//        )
//
//        Spacer(Modifier.height(12.dp))
//
//        if (isLoading) {
//            CircularProgressIndicator()
//        }
//
//        if (appointments.isEmpty() && !isLoading) {
//            Text("No appointments found")
//        }
//
//        appointments.forEach { appointment ->
//            AppointmentCard(appointment)
//        }
//    }
//}
//
//
//
//@Composable
//fun AppointmentCard(appointment: AppointmentModel) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 6.dp),
//        elevation = CardDefaults.cardElevation(6.dp)
//    ) {
//        Column(Modifier.padding(16.dp)) {
//
//            Text(
//                text = "Patient ID: ${appointment.patientId}",
//                style = MaterialTheme.typography.titleMedium
//            )
//
//            Spacer(Modifier.height(4.dp))
//
//            Text("Date: ${appointment.date}")
//            Text("Time: ${appointment.time}")
//            Text("Reason: ${appointment.reason}")
//        }
//    }
//}