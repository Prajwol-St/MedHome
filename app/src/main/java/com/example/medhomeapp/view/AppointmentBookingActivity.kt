package com.example.medhomeapp.view

import android.content.Context
import android.content.Intent
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medhomeapp.model.TimeSlot
import com.example.medhomeapp.model.UserModel
import com.example.medhomeapp.repository.AppointmentBookingRepoImpl
import com.example.medhomeapp.view.ui.theme.MedHomeAppTheme
import com.example.medhomeapp.viewmodel.AppointmentBookingViewModel
import com.example.medhomeapp.viewmodel.AppointmentBookingViewModelFactory

@Suppress("DEPRECATION")
class AppointmentBookingActivity : ComponentActivity() {

    companion object {
        private const val EXTRA_USER = "extra_user"
        private const val EXTRA_SLOT = "extra_slot"

        fun newIntent(
            context: Context,
            user: UserModel,
            slot: TimeSlot
        ): Intent {
            return Intent(context, AppointmentBookingActivity::class.java).apply {
                putExtra(EXTRA_USER, user)
                putExtra("slot_id", slot.id)
                putExtra("doctor_id", slot.doctorId)
            }
        }


        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val user = intent.getParcelableExtra<UserModel>(EXTRA_USER)
        val slot = TimeSlot(
            day = intent.getStringExtra("slot_day")!!,
            startTime = intent.getStringExtra("slot_start")!!,
            endTime = intent.getStringExtra("slot_end")!!
        )

        if (user == null || slot == null) {
            finish()
            return
        }

        setContent {
            MaterialTheme {
                Surface {
                    AppointmentBookingScreen(
                        user = user,
                        slot = slot,
                        onFinish = { finish() }
                    )
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentBookingScreen(
    user: UserModel,
    slot: TimeSlot,
    onFinish: () -> Unit
) {

    val bookingViewModel: AppointmentBookingViewModel = viewModel(
        factory = AppointmentBookingViewModelFactory(
            AppointmentBookingRepoImpl()
        )
    )

    val bookingState by bookingViewModel.bookingState.collectAsState()

    var reason by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    // ✅ React to booking result ONLY ONCE
    LaunchedEffect(bookingState.first) {
        if (bookingState.first != null) {
            showDialog = true
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Confirm Appointment") }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // 🔹 Slot Details
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Appointment Details",
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Day: ${slot.day}")
                    Text("Time: ${slot.startTime} - ${slot.endTime}")
                }
            }

            // 🔹 Patient Info
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Patient",
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(user.name)
                }
            }

            // 🔹 Reason Input
            OutlinedTextField(
                value = reason,
                onValueChange = { reason = it },
                label = { Text("Reason for visit") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.weight(1f))

            // 🔹 Book Button
            Button(
                onClick = {
                    bookingViewModel.book(
                        slot = slot,
                        patient = user,
                        reason = reason
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = reason.isNotBlank()
            ) {
                Text("Confirm Booking")
            }
        }
    }

    // 🔹 Result Dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                onFinish()
            },
            title = {
                Text(
                    if (bookingState.first == true) "Success" else "Failed"
                )
            },
            text = {
                Text(bookingState.second ?: "")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        onFinish()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}
