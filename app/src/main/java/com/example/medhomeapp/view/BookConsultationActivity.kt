package com.example.medhomeapp.view

import BookConsultationScreen
import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medhomeapp.BaseActivity
import com.example.medhomeapp.R
import com.example.medhomeapp.repository.AppointmentRepoImpl
import com.example.medhomeapp.repository.DoctorRepoImpl
import com.example.medhomeapp.ui.theme.Blue10
import com.example.medhomeapp.view.ui.theme.MedHomeAppTheme
import com.example.medhomeapp.viewmodel.AppointmentViewModel
import com.example.medhomeapp.viewmodel.AppointmentViewModelFactory
import com.example.medhomeapp.viewmodel.DoctorViewModel

class BookConsultationActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appointmentViewModel: AppointmentViewModel = viewModel(
                factory = AppointmentViewModelFactory(
                    AppointmentRepoImpl(),
                    DoctorRepoImpl()
                )
            )
            val doctorViewModel: DoctorViewModel = viewModel()

            BookConsultationScreen(
                appointmentViewModel = appointmentViewModel,
                doctorViewModel = doctorViewModel
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookConsultationBody(){
    val context = LocalContext.current
    val activity = context as Activity

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Blue10,
                        titleContentColor = White,
                        navigationIconContentColor = White,
                        actionIconContentColor = White,
                    ),
                    title = { Text("Book Consultation") },
                    navigationIcon = {
                        IconButton(onClick = { activity.finish() }) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_arrow_back_ios_new_24),
                                contentDescription = null
                            )
                        }
                    },
                )
            }
        }

    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

        }

    }
}