package com.example.medhomeapp.view

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.medhomeapp.R
import com.example.medhomeapp.ui.theme.Blue10

class BloodDonationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BloodDonationBody()

            }
        }
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloodDonationBody() {
    val context = LocalContext.current
    val activity = context as Activity
    var currentScreen by remember { mutableStateOf("main") }

    when (currentScreen) {
        "main" -> {
            MainDonationScreen(
                activity = activity,
                onPostRequestClick = { currentScreen = "post_request" },
                onJoinDonorClick = { currentScreen = "join_donor" }
            )
        }

        "post_request" -> {
            PostBloodRequestScreen(
                onBackClick = { currentScreen = "main" }
            )
        }

        "join_donor" -> {
            JoinDonorListScreen(
                onBackClick = { currentScreen = "main" }
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDonationScreen(
    activity : Activity,
    onPostRequestClick: () -> Unit,
    onJoinDonorClick: () -> Unit
){
    val bloodGroups = listOf("All","A+","A-","B+","B-","0+","O-","AB+","AB-")
    var selectedGroup by remember { mutableStateOf("All") }


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

                    title = { Text("Blood Donation") },
                    navigationIcon = {

                        IconButton(onClick = { activity.finish() }) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_arrow_back_ios_new_24),
                                contentDescription = null
                            )
                        }
                    },
                    actions = {
                        Button(
                            onClick = { },
                            colors = ButtonDefaults.buttonColors(
                                contentColor = Color.White,
                                containerColor = Color.White.copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.outline_history_24),
                                contentDescription = null
                            )
                        }
                    }


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






