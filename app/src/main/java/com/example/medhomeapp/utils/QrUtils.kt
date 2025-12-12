package com.example.medhomeapp.utils
import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.google.firebase.auth.FirebaseAuth
import androidx.core.graphics.set

val uid = FirebaseAuth.getInstance().currentUser?.uid

fun generateQrBitmap(
    text: String?,
    size: Int = 512
): Bitmap {
    val bitMatrix: BitMatrix = MultiFormatWriter().encode(
        text,
        BarcodeFormat.QR_CODE,
        size,
        size
    )

    val bitmap = generateQrBitmap(uid)

    for (x in 0 until size) {
        for (y in 0 until size) {
            bitmap[x, y] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
        }
    }

    return bitmap
}

