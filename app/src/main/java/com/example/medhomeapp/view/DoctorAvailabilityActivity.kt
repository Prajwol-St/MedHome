package com.example.medhomeapp.view

import android.app.Activity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.medhomeapp.BaseActivity
import com.example.medhomeapp.R
import com.example.medhomeapp.view.ui.theme.MintGreen

class DoctorAvailabilityActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DoctorAvailabilityTopBarOnly()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorAvailabilityTopBarOnly() {
    val context = LocalContext.current
    val activity = context as Activity

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MintGreen,
                    titleContentColor = Color.White
                ),
                title = {
                    Text("Doctor Availability")
                },
                navigationIcon = {
                    IconButton(onClick = { activity.finish() }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_arrow_back_ios_new_24),
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) {}
}
