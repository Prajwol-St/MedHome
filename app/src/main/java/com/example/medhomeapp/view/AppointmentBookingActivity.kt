package com.example.medhomeapp.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medhomeapp.BaseActivity
import com.example.medhomeapp.R
import com.example.medhomeapp.model.DoctorModel
import com.example.medhomeapp.model.TimeSlot
import com.example.medhomeapp.model.UserModel
import com.example.medhomeapp.repository.*
import com.example.medhomeapp.utils.DateTimeUtils
import com.example.medhomeapp.utils.LanguageManager
import com.example.medhomeapp.view.ui.theme.MintGreen
import com.example.medhomeapp.viewmodel.AppointmentBookingViewModel
import com.example.medhomeapp.viewmodel.AppointmentBookingViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class AppointmentBookingActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val doctorId = intent.getStringExtra("DOCTOR_ID") ?: ""

        // Get current language and use it as a key to force recomposition
        val language = LanguageManager.getLanguage(this)

        setContent {
            // The key ensures the entire composition is recreated when language changes
            key(language) {
                BookAppointmentScreen(doctorId = doctorId)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Recreate the activity when returning from settings if language changed
        val currentLanguage = LanguageManager.getLanguage(this)
        val savedLanguage = intent.getStringExtra("current_language")

        if (savedLanguage != null && savedLanguage != currentLanguage) {
            recreate()
        } else if (savedLanguage == null) {
            intent.putExtra("current_language", currentLanguage)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookAppointmentScreen(doctorId: String) {
    val context = LocalContext.current
    val activity = context as? BaseActivity

    val doctorRepo = DoctorRepoImpl()
    val availabilityRepo = DoctorAvailabilityRepoImpl()

    val userRepo = UserRepoImpl()

    val bookingViewModel: AppointmentBookingViewModel = viewModel(
        factory = AppointmentBookingViewModelFactory(context)
    )

    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    var doctor by remember { mutableStateOf<DoctorModel?>(null) }
    var patient by remember { mutableStateOf<UserModel?>(null) }
    var selectedDate by remember { mutableStateOf(DateTimeUtils.getCurrentDate()) }
    var selectedSlot by remember { mutableStateOf<TimeSlot?>(null) }
    var availableSlots by remember { mutableStateOf<List<TimeSlot>>(emptyList()) }
    var patientNotes by remember { mutableStateOf("") }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val bookingState by bookingViewModel.bookingState.collectAsState()

    // Fetch doctor details
    LaunchedEffect(doctorId) {
        doctorRepo.getDoctorByUserId(doctorId) { success, _, fetchedDoctor ->
            if (success && fetchedDoctor != null) {
                doctor = fetchedDoctor
            }
        }
    }

    // Fetch patient details
    LaunchedEffect(currentUserId) {
        userRepo.getUserByID(currentUserId) { success, _, fetchedPatient ->
            if (success && fetchedPatient != null) {
                patient = fetchedPatient
            }
        }
    }

    // Fetch available slots for selected date
    LaunchedEffect(selectedDate) {
        availabilityRepo.getAvailableSlots(doctorId, selectedDate) { slots ->
            availableSlots = slots.filter { !it.isBooked && it.isAvailable }
        }
    }

    // Handle booking result
    LaunchedEffect(bookingState) {
        bookingState.first?.let { success ->
            isLoading = false
            val message = bookingState.second ?: ""
            if (success) {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                activity?.finish()
            } else {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MintGreen,
                    titleContentColor = Color.White
                ),
                title = { Text(stringResource(R.string.title_book_appointment)) },
                navigationIcon = {
                    IconButton(onClick = { activity?.finish() }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_arrow_back_ios_new_24),
                            contentDescription = stringResource(R.string.cd_back),
                            tint = Color.White
                        )
                    }
                }
            )
        },
        bottomBar = {
            if (selectedSlot != null && doctor != null) {
                Surface(
                    shadowElevation = 8.dp,
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.consultation_fee),
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = stringResource(R.string.currency_npr, doctor!!.consultationFee.toInt()),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MintGreen
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { showConfirmDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MintGreen),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White
                                )
                            } else {
                                Text(
                                    text = stringResource(R.string.confirm_booking),
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
                .verticalScroll(rememberScrollState())
        ) {
            // Doctor Summary Card
            doctor?.let { doc ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(MintGreen.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(35.dp),
                                tint = MintGreen
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = doc.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = doc.specialization,
                                fontSize = 14.sp,
                                color = MintGreen
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = Color(0xFFFFB300)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = String.format("%.1f", doc.averageRating),
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }

            // Date Selection
            Text(
                text = stringResource(R.string.select_date),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            DateSelector(
                selectedDate = selectedDate,
                onDateSelected = { selectedDate = it }
            )

            // Time Slots
            Text(
                text = stringResource(R.string.available_time_slots),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            if (availableSlots.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.EventBusy,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.no_slots_available),
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((availableSlots.size / 3 + 1) * 60.dp)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(availableSlots) { slot ->
                        TimeSlotCard(
                            slot = slot,
                            isSelected = selectedSlot?.id == slot.id,
                            onClick = { selectedSlot = slot }
                        )
                    }
                }
            }

            // Notes Section
            Text(
                text = stringResource(R.string.reason_for_visit_optional),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            OutlinedTextField(
                value = patientNotes,
                onValueChange = { if (it.length <= 500) patientNotes = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(120.dp),
                placeholder = { Text(stringResource(R.string.notes_placeholder)) },
                maxLines = 4,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Text(
                text = stringResource(R.string.notes_char_count, patientNotes.length),
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    // Confirmation Dialog
    if (showConfirmDialog && selectedSlot != null && doctor != null && patient != null) {
        ConfirmBookingDialog(
            doctor = doctor!!,
            slot = selectedSlot!!,
            date = selectedDate,
            patientNotes = patientNotes,
            onConfirm = {
                isLoading = true
                showConfirmDialog = false
                bookingViewModel.book(selectedSlot!!, patient!!, patientNotes)
            },
            onDismiss = { showConfirmDialog = false }
        )
    }
}

@Composable
fun DateSelector(
    selectedDate: String,
    onDateSelected: (String) -> Unit
) {
    val dates = remember {
        (0..13).map { daysAhead ->
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_MONTH, daysAhead)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
            val dayOfMonth = SimpleDateFormat("dd", Locale.getDefault())

            Triple(
                dateFormat.format(calendar.time),
                dayFormat.format(calendar.time),
                dayOfMonth.format(calendar.time)
            )
        }
    }

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(dates) { (date, dayName, dayNum) ->
            DateCard(
                dayName = dayName,
                dayNumber = dayNum,
                isSelected = selectedDate == date,
                onClick = { onDateSelected(date) }
            )
        }
    }
}

@Composable
fun DateCard(
    dayName: String,
    dayNumber: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(70.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MintGreen else Color.White
        ),
        border = if (!isSelected) BorderStroke(1.dp, Color.LightGray) else null,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = dayName,
                fontSize = 12.sp,
                color = if (isSelected) Color.White else Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = dayNumber,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else Color(0xFF2C3E50)
            )
        }
    }
}

@Composable
fun TimeSlotCard(
    slot: TimeSlot,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isSelected -> MintGreen
                slot.isBooked -> Color.LightGray
                else -> Color.White
            }
        ),
        border = if (!isSelected && !slot.isBooked) BorderStroke(1.dp, Color.LightGray) else null,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = slot.startTime,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = when {
                    isSelected -> Color.White
                    slot.isBooked -> Color.Gray
                    else -> Color(0xFF2C3E50)
                }
            )
            Text(
                text = stringResource(R.string.duration_min, slot.duration),
                fontSize = 11.sp,
                color = when {
                    isSelected -> Color.White.copy(alpha = 0.8f)
                    slot.isBooked -> Color.Gray
                    else-> Color.Gray
                }
            )
        }
    }
}

@Composable
fun ConfirmBookingDialog(
    doctor: DoctorModel,
    slot: TimeSlot,
    date: String,
    patientNotes: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.EventAvailable,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MintGreen
            )
        },
        title = {
            Text(
                text = stringResource(R.string.confirm_booking_title),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DetailItem(stringResource(R.string.doctor), doctor.name)
                DetailItem(stringResource(R.string.specialization), doctor.specialization)
                DetailItem(stringResource(R.string.date), DateTimeUtils.formatDateForDisplay(date))
                DetailItem(stringResource(R.string.time), slot.startTime)
                DetailItem(
                    stringResource(R.string.duration),
                    stringResource(R.string.duration_minutes, slot.duration)
                )
                DetailItem(
                    stringResource(R.string.fee),
                    stringResource(R.string.currency_npr, doctor.consultationFee.toInt())
                )
                if (patientNotes.isNotBlank()) {
                    DetailItem(stringResource(R.string.notes), patientNotes)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MintGreen)
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun DetailItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            fontSize = 14.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = Color(0xFF2C3E50),
            fontWeight = FontWeight.SemiBold
        )
    }
}