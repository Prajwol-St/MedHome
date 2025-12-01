package com.example.medhomeapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import com.example.medhomeapp.ui.theme.Blue10

@Composable
fun RecordsScreen(){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ){
        Text("Records Screen")
    }

}