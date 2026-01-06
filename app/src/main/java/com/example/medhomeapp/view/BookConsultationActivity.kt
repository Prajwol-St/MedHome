package com.example.medhomeapp.view

import AppointmentModel
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medhomeapp.BaseActivity
import com.example.medhomeapp.R
import com.example.medhomeapp.model.DoctorModel
import com.example.medhomeapp.model.TimeSlot
import com.example.medhomeapp.model.UserModel
import com.example.medhomeapp.repository.DoctorAvailabilityRepoImpl
import com.example.medhomeapp.repository.DoctorRepoImpl
import com.example.medhomeapp.ui.theme.Blue10
import com.example.medhomeapp.view.ui.theme.MedHomeAppTheme
import com.example.medhomeapp.viewmodel.DoctorSlotsViewModel
import com.example.medhomeapp.viewmodel.DoctorViewModel

class BookConsultationActivity : BaseActivity() {

    companion object {
        private const val EXTRA_USER = "extra_user"

        fun newIntent(context: Context, user: UserModel): Intent {
            return Intent(context, BookConsultationActivity::class.java).apply {
                putExtra(EXTRA_USER, user)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val user = intent.getParcelableExtra<UserModel>(EXTRA_USER)
        if (user == null) {
            finish()
            return
        }

        setContent {
            MedHomeAppTheme {
                BookConsultationRoute(currentUser = user)
            }
        }
    }
}


@Composable
fun BookConsultationRoute(
    currentUser: UserModel
) {
    val doctorViewModel: DoctorViewModel = viewModel(
        factory = ViewModelProvider.Factory {
            DoctorViewModel(DoctorRepoImpl())
        }
    )

    val doctors by doctorViewModel.doctors.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        doctorViewModel.loadDoctors()
    }

    when {
        doctors.isEmpty() -> {
            CircularProgressIndicator()
        }
        else -> {
            BookConsultationScreen(
                currentUser = currentUser,
                doctors = doctors
            )
        }
    }
}







@Composable
fun BookConsultationScreen(
    currentUser: UserModel,
    doctors: List<DoctorModel>
) {
    val context = LocalContext.current

    val slotViewModel: DoctorSlotsViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DoctorSlotsViewModel(DoctorAvailabilityRepoImpl()) as T
            }
        }
    )

    var selectedDoctor by remember { mutableStateOf<DoctorModel?>(null) }
    val slots by slotViewModel.slots.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Text("Choose Doctor", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(8.dp))

        doctors.forEach { doctor ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable {
                        selectedDoctor = doctor
                        slotViewModel.observeSlots(doctor.userId)
                    }
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(doctor.name, fontWeight = FontWeight.Bold)
                    Text(doctor.specialization)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        selectedDoctor?.let {
            Text(
                "Available Slots",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            if (slots.isEmpty()) {
                Text("No available slots")
            } else {
                slots.forEach { slot ->
                    TimeSlotPatientCard(
                        slot = slot,
                        onSelect = {
                            context.startActivity(
                                AppointmentBookingActivity.newIntent(
                                    context = context,
                                    user = currentUser,
                                    slot = slot
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TimeSlotPatientCard(
    slot: TimeSlot,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(slot.day, fontWeight = FontWeight.Bold)
            Text("${slot.startTime} - ${slot.endTime}")
        }
    }
}
