package com.example.medhomeapp

import android.app.Activity
import android.media.Image
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medhomeapp.ui.theme.Blue10
import com.example.medhomeapp.ui.theme.MedHomeAppTheme

class HealthRecords : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HealthRecordsBody()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthRecordsBody(){
    val context = LocalContext.current
    val activity = context as Activity
    var isSearching by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Blue10,
                        titleContentColor = Color.White,
                        navigationIconContentColor = White,
                        actionIconContentColor = White,
                    ),
                    title = {Text("My Records")},
                    navigationIcon = {
                        IconButton(onClick ={activity.finish()}) {
                          Icon(
                              painter = painterResource(R.drawable.baseline_arrow_back_ios_new_24),
                              contentDescription = null
                          )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            isSearching = !isSearching
                            if (!isSearching){
                                searchQuery = ""
                            }
                        }) {
                            Icon(
                                painter = painterResource(
                                    if (isSearching) R.drawable.baseline_close_24
                                    else R.drawable.baseline_search_24
                                ),
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                )
                AnimatedVisibility(
                    visible = isSearching,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically()+ fadeOut()
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = {searchQuery = it},
                        placeholder = {Text("Search ....")},
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.baseline_search_24),
                                contentDescription = null
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .background(White),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.White,
                            unfocusedIndicatorColor = Color.Gray,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White

                        ),
                    )
                }
            }
        }

    ){padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ){
                    Image(
                        painter = painterResource(R.drawable.medicalrecord),
                        contentDescription = null,
                        modifier = Modifier.size(120.dp),
                        colorFilter = ColorFilter.tint(Color.LightGray)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(text ="No Records Yet",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Tap + to add your medical records",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }

        }

    }
}