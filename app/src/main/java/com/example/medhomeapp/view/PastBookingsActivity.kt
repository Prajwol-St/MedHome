package com.example.medhomeapp.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medhomeapp.BaseActivity
import com.example.medhomeapp.R
import com.example.medhomeapp.model.AppointmentModel
import com.example.medhomeapp.repository.AppointmentManagementRepoImpl
import com.example.medhomeapp.repository.RatingRepoImpl
import com.example.medhomeapp.utils.AppConstants
import com.example.medhomeapp.utils.DateTimeUtils
import com.example.medhomeapp.view.ui.theme.MintGreen
import com.example.medhomeapp.viewmodel.PatientAppointmentsViewModel
import com.example.medhomeapp.viewmodel.PatientAppointmentsViewModelFactory
import com.example.medhomeapp.viewmodel.RatingViewModel
import com.example.medhomeapp.viewmodel.RatingViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class PastBookingsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PastBookingsScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PastBookingsScreen() {
    val context = LocalContext.current
    val activity = context as? BaseActivity

    val patientId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val appointmentRepo = AppointmentManagementRepoImpl()
    val ratingRepo = RatingRepoImpl()

    val appointmentsViewModel: PatientAppointmentsViewModel = viewModel(
        factory = PatientAppointmentsViewModelFactory(appointmentRepo, patientId)
    )

    val ratingViewModel: RatingViewModel = viewModel(
        factory = RatingViewModelFactory(ratingRepo)
    )

    val pastAppointments by appointmentsViewModel.pastAppointments.collectAsState()
    val completedAppointments by appointmentsViewModel.pastAppointments.collectAsState()
    val isLoading by appointmentsViewModel.isLoading.collectAsState()

    var showRatingDialog by remember { mutableStateOf(false) }
    var selectedAppointment by remember { mutableStateOf<AppointmentModel?>(null) }

    val ratingValue by ratingViewModel.rating.collectAsState()
    val reviewText by ratingViewModel.review.collectAsState()
    val ratingResult by ratingViewModel.operationResult.collectAsState()

    // Handle rating result
    LaunchedEffect(ratingResult) {
        ratingResult?.let { (success, message) ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            if (success) {
                showRatingDialog = false
                ratingViewModel.clearForm()
            }
            ratingViewModel.clearOperationResult()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MintGreen,
                    titleContentColor = Color.White
                ),
                title = { Text("Past Bookings") },
                navigationIcon = {
                    IconButton(onClick = { activity?.finish() }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_arrow_back_ios_new_24),
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MintGreen)
                }
            } else if (pastAppointments.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            Icons.Default.History,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Text(
                            text = "No past bookings",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "Your completed appointments will appear here",
                            fontSize = 14.sp,
                            color = Color.LightGray
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(pastAppointments) { appointment ->
                        PastBookingCard(
                            appointment = appointment,
                            onRateClick = {
                                // Check if already rated
                                ratingViewModel.checkIfAlreadyRated(appointment.appointmentId)
                                selectedAppointment = appointment
                                showRatingDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    // Rating Dialog
    if (showRatingDialog && selectedAppointment != null) {
        val canRate by ratingViewModel.canRate.collectAsState()

        AlertDialog(
            onDismissRequest = {
                showRatingDialog = false
                ratingViewModel.clearForm()
            },
            title = {
                Text(
                    text = if (canRate) "Rate Your Experience" else "Already Rated",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                if (canRate) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Doctor Info
                        Column {
                            Text(
                                text = selectedAppointment!!.doctorName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = selectedAppointment!!.specialization,
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }

                        // Star Rating
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Rate your experience",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                for (i in 1..5) {
                                    IconButton(
                                        onClick = { ratingViewModel.setRating(i.toFloat()) }
                                    ) {
                                        Icon(
                                            if (i <= ratingValue) Icons.Default.Star else Icons.Default.StarBorder,
                                            contentDescription = "$i stars",
                                            modifier = Modifier.size(32.dp),
                                            tint = if (i <= ratingValue) Color(0xFFFFB300) else Color.Gray
                                        )
                                    }
                                }
                            }
                            if (ratingValue > 0) {
                                Text(
                                    text = "${ratingValue.toInt()} star${if (ratingValue > 1) "s" else ""}",
                                    fontSize = 14.sp,
                                    color = MintGreen,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Review Text
                        OutlinedTextField(
                            value = reviewText,
                            onValueChange = { if (it.length <= 500) ratingViewModel.setReview(it) },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Write a review (optional)") },
                            placeholder = { Text("Share your experience...") },
                            maxLines = 4,
                            supportingText = { Text("${reviewText.length}/500") }
                        )
                    }
                } else {
                    Text("You have already rated this appointment.")
                }
            },
            confirmButton = {
                if (canRate) {
                    Button(
                        onClick = {
                            val currentUser = FirebaseAuth.getInstance().currentUser
                            if (currentUser != null && ratingValue > 0) {
                                ratingViewModel.submitRating(
                                    appointmentId = selectedAppointment!!.appointmentId,
                                    patientId = selectedAppointment!!.patientId,
                                    patientName = selectedAppointment!!.patientName,
                                    doctorId = selectedAppointment!!.doctorId
                                )
                            }
                        },
                        enabled = ratingValue > 0,
                        colors = ButtonDefaults.buttonColors(containerColor = MintGreen)
                    ) {
                        Text("Submit Rating")
                    }
                } else {
                    TextButton(
                        onClick = {
                            showRatingDialog = false
                            ratingViewModel.clearForm()
                        }
                    ) {
                        Text("Close")
                    }
                }
            },
            dismissButton = {
                if (canRate) {
                    TextButton(
                        onClick = {
                            showRatingDialog = false
                            ratingViewModel.clearForm()
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun PastBookingCard(
    appointment: AppointmentModel,
    onRateClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = appointment.doctorName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C3E50)
                    )
                    Text(
                        text = appointment.specialization,
                        fontSize = 13.sp,
                        color = MintGreen,
                        fontWeight = FontWeight.Medium
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = when (appointment.status) {
                        AppConstants.STATUS_COMPLETED -> Color(0xFFC8E6C9)
                        AppConstants.STATUS_NO_SHOW -> Color(0xFFE0E0E0)
                        else -> Color.LightGray
                    }
                ) {
                    Text(
                        text = when (appointment.status) {
                            AppConstants.STATUS_COMPLETED -> "Completed"
                            AppConstants.STATUS_NO_SHOW -> "No Show"
                            else -> appointment.status.replaceFirstChar { it.uppercase() }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = when (appointment.status) {
                            AppConstants.STATUS_COMPLETED -> Color(0xFF388E3C)
                            AppConstants.STATUS_NO_SHOW -> Color(0xFF616161)
                            else -> Color.Gray
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Date & Time
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CalendarMonth,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MintGreen
                    )
                    Text(
                        text = DateTimeUtils.formatDateForDisplay(appointment.date),
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.AccessTime,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MintGreen
                    )
                    Text(
                        text = appointment.time,
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }

            // Consultation Fee
            if (appointment.consultationFee > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Payment,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MintGreen
                    )
                    Text(
                        text = "NPR ${appointment.consultationFee.toInt()}",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }

            // Doctor Notes (if available)
            if (appointment.doctorNotes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF5F5F5)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Doctor's Notes:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF2C3E50)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = appointment.doctorNotes,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            // Rate Button (only for completed appointments)
            if (appointment.status == AppConstants.STATUS_COMPLETED) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onRateClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MintGreen
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Rate Doctor")
                }
            }
        }
    }
}