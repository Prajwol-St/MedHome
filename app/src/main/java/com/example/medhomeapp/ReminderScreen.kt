package com.example.medhomeapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun ReminderScreen(){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ){
        Text("Records Screen")
    }

}