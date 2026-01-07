package com.example.medhomeapp.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.Factory
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medhomeapp.BaseActivity
import com.example.medhomeapp.model.DoctorModel
import com.example.medhomeapp.model.TimeSlot
import com.example.medhomeapp.model.UserModel
import com.example.medhomeapp.repository.DoctorAvailabilityRepoImpl
import com.example.medhomeapp.repository.DoctorRepoImpl
import com.example.medhomeapp.view.ui.theme.MedHomeAppTheme
import com.example.medhomeapp.viewmodel.DoctorSlotsViewModel
import com.example.medhomeapp.viewmodel.DoctorViewModel
import com.example.medhomeapp.viewmodel.DoctorViewModelFactory

class BookConsultationActivity : BaseActivity() {

    companion object {
        private const val EXTRA_USER = "extra_user"

        fun newIntent(context: Context, user: UserModel): Intent =
            Intent(context, BookConsultationActivity::class.java)
                .putExtra(EXTRA_USER, user)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val user = intent.getParcelableExtra<UserModel>(EXTRA_USER) ?: return finish()

        setContent {
            MedHomeAppTheme {
                BookConsultationRoute(user)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookConsultationRoute(
    currentUser: UserModel
) {
    val doctorViewModel: DoctorViewModel = viewModel(
        factory = DoctorViewModelFactory(DoctorRepoImpl())
    )

    val doctors by doctorViewModel.doctors.collectAsStateWithLifecycle()
    val isLoading by doctorViewModel.isLoading.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        doctorViewModel.loadDoctors()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book Consultation") }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> CircularProgressIndicator()
                doctors.isEmpty() -> Text("No doctors available")
                else -> BookConsultationScreen(currentUser, doctors)
            }
        }
    }
}









@Composable
fun BookConsultationScreen(
    currentUser: UserModel,
    doctors: List<DoctorModel>
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val slotViewModel: DoctorSlotsViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DoctorSlotsViewModel(
                    DoctorAvailabilityRepoImpl()
                ) as T
            }
        }
    )

    var selectedDoctor by remember { mutableStateOf<DoctorModel?>(null) }
    val slots by slotViewModel.slots.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {

        Text(
            "Choose Doctor",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))

        doctors.forEach { doctor ->
            DoctorCard(
                doctor = doctor,
                isSelected = doctor == selectedDoctor
            ) {
                selectedDoctor = doctor
                slotViewModel.observeSlots(doctor.userId)
            }
        }

        Spacer(Modifier.height(20.dp))

        selectedDoctor?.let {
            Text(
                "Available Slots",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            if (slots.isEmpty()) {
                Text("No available slots")
            } else {
                slots.forEach { slot ->
                    TimeSlotPatientCard(slot) {
                        context.startActivity(
                            AppointmentBookingActivity.newIntent(
                                context,
                                currentUser,
                                slot
                            )
                        )
                    }
                }
            }
        }
    }
}




@Composable
fun DoctorCard(
    doctor: DoctorModel,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(doctor.name, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(doctor.specialization)
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
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(slot.day, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("${slot.startTime} - ${slot.endTime}")
        }
    }
}

