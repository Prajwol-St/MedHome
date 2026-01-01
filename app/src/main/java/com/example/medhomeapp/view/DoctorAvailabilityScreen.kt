package com.example.medhomeapp.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.medhomeapp.viewmodel.DoctorViewModel

@Composable
fun DoctorAvailabilityScreen(viewModel: DoctorViewModel) {

    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }

    Column(Modifier.padding(16.dp)) {

        Text("Doctor Availability", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            label = { Text("Date (yyyy-mm-dd)") }
        )

        OutlinedTextField(
            value = time,
            onValueChange = { time = it },
            label = { Text("Time (e.g. 10:00)") }
        )

        Button(onClick = {
            viewModel.addAvailability(date, time)
        }) {
            Text("Add Available Time")
        }

        Spacer(Modifier.height(16.dp))

        Button(
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            onClick = {
                viewModel.addLeave(date)
            }
        ) {
            Text("Mark Leave")
        }
    }
}
