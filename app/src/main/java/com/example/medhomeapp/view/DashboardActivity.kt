package com.example.medhomeapp.view

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.medhomeapp.R
import com.example.medhomeapp.ui.theme.Blue10

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DashboardBody()
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardBody(){
    val context = LocalContext.current


    data class NavItem(val label: String, val icon: Int, val title: String)
    var selectedItem by remember { mutableStateOf(0) }

    var navList = listOf(
        NavItem("Home", R.drawable.baseline_home_24, "MedHome"),
        NavItem("Reminder", R.drawable.baseline_access_time_filled_24,"My Reminders"),
        NavItem("Notifications", R.drawable.baseline_notifications_24, "Notifications"),
        NavItem("Settings", R.drawable.baseline_settings_24, "App Settings"),
    )

    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Blue10,
                    titleContentColor = White,

                    ),
                title = {Text(navList[selectedItem].title,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 27.sp
                    ))},
            )
        },

        bottomBar = {
            NavigationBar {
                navList.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon ={
                            Icon(
                                painter = painterResource(item.icon),
                                contentDescription = null
                            )
                        },
                        label = {Text(item.label)},
                        onClick = {
                            selectedItem = index
                        },
                        selected = selectedItem == index

                    )
                }
            }
        }
    ){ padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ){
            when(selectedItem){
                0-> HomeScreen()
                1-> ReminderScreen()
                2-> NotificationScreen()
                3-> SettingsScreen()
            }

        }

    }
}




@Preview
@Composable
fun PreviewDashboard(){
    DashboardBody()
}