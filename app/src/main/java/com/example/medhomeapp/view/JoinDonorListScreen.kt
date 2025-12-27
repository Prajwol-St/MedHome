package com.example.medhomeapp.view


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medhomeapp.ui.theme.Blue10
import com.example.medhomeapp.viewmodel.BloodDonationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinDonorListScreen(
    viewModel: BloodDonationViewModel,
    onBackClick: () -> Unit
){
    var bloodGroup by remember { mutableStateOf("") }
    var isAvailable by remember { mutableStateOf(false) }
    var isEmergencyAvailable by remember { mutableStateOf(false) }

    val donorProfile by viewModel.donorProfile.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadDonorProfile()
    }

    LaunchedEffect(donorProfile) {
        donorProfile?.let {
            bloodGroup = it.bloodGroup
            isAvailable = it.isAvailable
            isEmergencyAvailable = it.isEmergencyAvailable
        }
    }

    LaunchedEffect(successMessage) {
        if (successMessage != null) {
            viewModel.clearSuccessMessage()
            onBackClick()
        }
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Blue10,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                ),
                title = { Text("Join Donor List",
                    fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ){padding ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFFFF5F5))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ){
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE3F2FD)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFF1976D2),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Join our donor community and help save lives. Your information will be visible to those in need.",
                        fontSize = 14.sp,
                        color = Color(0xFF1976D2),
                        modifier = Modifier.weight(1f)
                    )
                }
            }


        }
    }
}