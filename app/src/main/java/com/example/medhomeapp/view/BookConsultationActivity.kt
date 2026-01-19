package com.example.medhomeapp.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medhomeapp.BaseActivity
import com.example.medhomeapp.model.TimeSlot
import com.example.medhomeapp.model.UserModel
import com.example.medhomeapp.repository.DoctorAvailabilityRepoImpl
import com.example.medhomeapp.repository.UserRepoImpl
import com.example.medhomeapp.utils.UiState
import com.example.medhomeapp.view.ui.theme.MedHomeAppTheme
import com.example.medhomeapp.viewmodel.DoctorSlotsViewModel
import com.example.medhomeapp.viewmodel.UserViewModel
import com.example.medhomeapp.viewmodel.UserViewModelFactory

class BookConsultationActivity : BaseActivity() {

    companion object {
        private const val EXTRA_USER = "extra_user"

        fun newIntent(context: Context, user: UserModel): Intent {
            return Intent(context, BookConsultationActivity::class.java)
                .putExtra(EXTRA_USER, user)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookConsultationRoute(currentUser: UserModel) {

    val userViewModel: UserViewModel = viewModel(
        factory = remember { UserViewModelFactory(UserRepoImpl()) }
    )

    val allUsersState by userViewModel.allUsers
    val loading by userViewModel.loading

    LaunchedEffect(Unit) {
        userViewModel.getAllUser()
    }

    val doctors: List<UserModel> = remember(allUsersState) {
        when (allUsersState) {
            is UiState.Success -> {
                (allUsersState as UiState.Success<List<UserModel>>)
                    .data
                    .filter { it.role == "doctor" }
            }
            else -> emptyList()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Book Consultation") })
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when {
                loading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )

                doctors.isEmpty() -> Text(
                    "No doctors available",
                    modifier = Modifier.align(Alignment.Center)
                )

                else -> BookConsultationScreen(
                    currentUser = currentUser,
                    doctors = doctors
                )
            }
        }
    }
}

@Composable
fun BookConsultationScreen(
    currentUser: UserModel,
    doctors: List<UserModel>
) {
    val context = LocalContext.current

    val slotViewModel: DoctorSlotsViewModel = viewModel(
        factory = remember {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return DoctorSlotsViewModel(
                        DoctorAvailabilityRepoImpl()
                    ) as T
                }
            }
        }
    )

    var selectedDoctor by remember { mutableStateOf<UserModel?>(null) }
    val slots by slotViewModel.slots.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Choose Doctor",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(doctors) { doctor ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable {
                            selectedDoctor = doctor
                            slotViewModel.observeSlots(doctor.id)
                        }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(doctor.name, fontWeight = FontWeight.Bold)
                        Text("Doctor")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        selectedDoctor?.let {
            Text(
                text = "Available Slots",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (slots.isEmpty()) {
                Text("No available slots")
            } else {
                LazyColumn {
                    items(slots) { slot ->
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
        Column(modifier = Modifier.padding(16.dp)) {
            Text(slot.day, fontWeight = FontWeight.Bold)
            Text("${slot.startTime} - ${slot.endTime}")
        }
    }
}
