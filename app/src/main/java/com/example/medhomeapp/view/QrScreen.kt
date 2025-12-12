package com.example.medhomeapp.view

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medhomeapp.utils.generateQrBitmap
import com.google.firebase.auth.FirebaseAuth

@Composable
fun QrScreen() {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    if (uid == null) {
        Text("User not logged in")
        return
    }

    val qrBitmap = remember { generateQrBitmap(uid) }
    val imageBitmap = qrBitmap.asImageBitmap()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text("Your QR Code", fontSize = 22.sp, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(24.dp))

        Image(
            bitmap = imageBitmap,
            contentDescription = "QR Code",
            modifier = Modifier.size(250.dp)
        )

        Spacer(Modifier.height(16.dp))

        Text("UID: $uid")
    }
}
