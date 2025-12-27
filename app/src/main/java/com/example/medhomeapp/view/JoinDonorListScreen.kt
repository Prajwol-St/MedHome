package com.example.medhomeapp.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.medhomeapp.viewmodel.BloodDonationViewModel

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
}