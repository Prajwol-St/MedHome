package com.example.medhomeapp.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medhomeapp.BaseActivity
import com.example.medhomeapp.R
import com.example.medhomeapp.model.DoctorModel
import com.example.medhomeapp.repository.DoctorRepoImpl
import com.example.medhomeapp.view.ui.theme.MintGreen

class DoctorDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val doctorId = intent.getStringExtra("DOCTOR_ID") ?: ""

        setContent {
            DoctorDetailScreen(doctorId = doctorId)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorDetailScreen(doctorId: String) {
    val context = LocalContext.current
    val doctorRepo = DoctorRepoImpl()

    var doctor by remember { mutableStateOf<DoctorModel?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Fetch doctor details from Firebase
    LaunchedEffect(doctorId) {
        doctorRepo.getDoctorByUserId(doctorId) { success, message, fetchedDoctor ->
            isLoading = false
            if (success && fetchedDoctor != null) {
                doctor = fetchedDoctor
            } else {
                errorMessage = message
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
                title = { Text(stringResource(R.string.doctor_profile)) },
                navigationIcon = {
                    IconButton(onClick = { (context as? BaseActivity)?.finish() }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_arrow_back_ios_new_24),
                            contentDescription = stringResource(R.string.back),
                            tint = Color.White
                        )
                    }
                }
            )
        },
        bottomBar = {
            if (doctor != null) {
                Surface(
                    shadowElevation = 8.dp,
                    color = Color.White
                ) {
                    Button(
                        onClick = {
                            val intent = Intent(context, AppointmentBookingActivity::class.java)
                            intent.putExtra("DOCTOR_ID", doctorId)
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MintGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.book_appointment),
                            fontSize = 16.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MintGreen)
                }
            }
            errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Red
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = errorMessage ?: stringResource(R.string.failed_to_load_doctor),
                            color = Color.Red
                        )
                    }
                }
            }
            doctor != null -> {
                DoctorDetailContent(
                    doctor = doctor!!,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
fun DoctorDetailContent(
    doctor: DoctorModel,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(Color(0xFFF5F5F5))
    ) {
        // Doctor Header Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Image
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MintGreen.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        tint = MintGreen
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Name
                Text(
                    text = doctor.name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C3E50)
                )

                // Specialization
                Text(
                    text = doctor.specialization,
                    fontSize = 16.sp,
                    color = MintGreen,
                    fontWeight = FontWeight.Medium
                )

                if (doctor.subSpecialization.isNotBlank()) {
                    Text(
                        text = doctor.subSpecialization,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Rating
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color(0xFFFFB300)
                    )
                    Text(
                        text = String.format("%.1f", doctor.averageRating),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.ratings_count, doctor.totalRatings),
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(
                        icon = Icons.Default.Work,
                        label = stringResource(R.string.experience),
                        value = stringResource(R.string.years, doctor.experience)
                    )
                    StatItem(
                        icon = Icons.Default.People,
                        label = stringResource(R.string.patients),
                        value = stringResource(R.string.patients_count, doctor.totalRatings)
                    )
                    StatItem(
                        icon = Icons.Default.Verified,
                        label = stringResource(R.string.verified),
                        value = if (doctor.isVerified) stringResource(R.string.yes) else stringResource(R.string.no)
                    )
                }
            }
        }

        // About Section
        InfoSection(
            title = stringResource(R.string.about),
            icon = Icons.Default.Info
        ) {
            Text(
                text = doctor.about.ifBlank { stringResource(R.string.no_information_available) },
                fontSize = 14.sp,
                color = Color(0xFF2C3E50),
                lineHeight = 20.sp
            )
        }

        // Qualifications
        InfoSection(
            title = stringResource(R.string.qualifications),
            icon = Icons.Default.School
        ) {
            Text(
                text = doctor.qualifications.ifBlank { stringResource(R.string.not_specified) },
                fontSize = 14.sp,
                color = Color(0xFF2C3E50)
            )
        }

        // Clinic Info
        InfoSection(
            title = stringResource(R.string.clinic_information),
            icon = Icons.Default.LocationOn
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DetailRow(
                    icon = Icons.Default.LocalHospital,
                    label = stringResource(R.string.clinic_name),
                    value = doctor.clinicName
                )
                DetailRow(
                    icon = Icons.Default.Place,
                    label = stringResource(R.string.address),
                    value = doctor.clinicAddress
                )
            }
        }

        // Consultation Fee
        InfoSection(
            title = stringResource(R.string.consultation_fee),
            icon = Icons.Default.Payment
        ) {
            Text(
                text = stringResource(R.string.npr_amount, doctor.consultationFee.toInt()),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MintGreen
            )
        }

        // Languages
        if (doctor.languages.isNotBlank()) {
            InfoSection(
                title = stringResource(R.string.languages),
                icon = Icons.Default.Language
            ) {
                Text(
                    text = doctor.languages,
                    fontSize = 14.sp,
                    color = Color(0xFF2C3E50)
                )
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun StatItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MintGreen
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2C3E50)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun InfoSection(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MintGreen
                )
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C3E50)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = Color.Gray
        )
        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text(
                text = value,
                fontSize = 14.sp,
                color = Color(0xFF2C3E50)
            )
        }
    }
}