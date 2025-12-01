package com.example.medhomeapp


import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medhomeapp.R
import com.example.medhomeapp.ui.theme.Blue10
import com.example.medhomeapp.ui.theme.MedHomeAppTheme

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
    val activity = context as Activity

    data class NavItem(val label: String, val icon: Int)
    var selectedItem by remember { mutableStateOf(0) }

    var navList = listOf(
        NavItem("Home", R.drawable.baseline_home_24),
        NavItem("Records", R.drawable.baseline_receipt_long_24),
        NavItem("Notifications", R.drawable.baseline_notifications_24),
        NavItem("Settings", R.drawable.baseline_settings_24),
    )

    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Blue10,
                    titleContentColor = Color.White,

                ),
                title = {Text("MedHome", style = TextStyle(
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
                0->HomeScreen()
                1->RecordsScreen()
                2->NotificationScreen()
                3->SettingsScreen()
            }

        }

    }
}




@Preview
@Composable
fun PreviewDashboard(){
    DashboardBody()
}