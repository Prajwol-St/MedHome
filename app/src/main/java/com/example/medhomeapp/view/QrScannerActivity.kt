package com.example.medhomeapp.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.medhomeapp.view.ui.theme.MedHomeAppTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

class QrScannerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MedHomeAppTheme {
                ScannerScreen()
            }
        }
    }
}

@Composable
fun ScannerScreen() {
    val context = LocalContext.current
    var scannedUid by remember { mutableStateOf<String?>(null) }
    var viewerRole by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(true) }

    // 🔐 Get logged-in user role
    LaunchedEffect(Unit) {
        val authUid = FirebaseAuth.getInstance().currentUser?.uid

        if (authUid == null) {
            loading = false
            viewerRole = "patient"
            return@LaunchedEffect
        }

        FirebaseDatabase.getInstance()
            .getReference("User")
            .child(authUid)
            .child("role")
            .get()
            .addOnSuccessListener {
                viewerRole = it.value?.toString() ?: "patient"
                loading = false
            }
            .addOnFailureListener {
                viewerRole = "patient"
                loading = false
            }
    }

    if (loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (scannedUid == null) {
        CameraPreview { uid ->
            scannedUid = uid
        }
    } else {
        UserInfoScreen(
            uid = scannedUid!!,
            viewerRole = viewerRole!!   // 🔥 THIS FIXES SECURITY
        )
    }
}

@OptIn(ExperimentalGetImage::class)
@Composable
fun CameraPreview(onResult: (String) -> Unit) {

    var isScanned by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)

            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener({

                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val scannerOptions = BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                    .build()

                val scanner = BarcodeScanning.getClient(scannerOptions)

                val analysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                analysis.setAnalyzer(cameraExecutor) { imageProxy ->

                    if (isScanned) {
                        imageProxy.close()
                        return@setAnalyzer
                    }

                    val mediaImage = imageProxy.image ?: run {
                        imageProxy.close()
                        return@setAnalyzer
                    }

                    val image = InputImage.fromMediaImage(
                        mediaImage,
                        imageProxy.imageInfo.rotationDegrees
                    )

                    scanner.process(image)
                        .addOnSuccessListener { barcodes ->
                            barcodes.firstOrNull()?.rawValue?.let { value ->
                                isScanned = true
                                onResult(value)
                            }
                        }
                        .addOnCompleteListener {
                            imageProxy.close()
                        }
                }

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    (context as ComponentActivity),
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    analysis
                )

            }, ContextCompat.getMainExecutor(ctx))

            previewView
        },
        modifier = Modifier.fillMaxSize()
    )

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }
}
