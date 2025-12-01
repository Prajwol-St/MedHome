package com.example.medhomeapp

import android.app.Notification
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun NotificationScreen(){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Blue)
    ){
        Text("Notification Screen")
    }

}