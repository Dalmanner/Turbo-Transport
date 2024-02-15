package com.example.turbo_transport

import android.content.Intent
import android.graphics.ImageFormat
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class BarCodeReaderActivity : AppCompatActivity() {
    private lateinit var viewFinder: PreviewView
    private lateinit var cameraExecutor: ExecutorService
    private var barcodeValue = ""
    private var barcodeProcessed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bar_code_reader)
        viewFinder = findViewById(R.id.viewFinder)
        cameraExecutor = Executors.newSingleThreadExecutor()

        barcodeValue = intent.getStringExtra("barcodeValue").toString()

        startCamera()

    }

    @OptIn(ExperimentalGetImage::class)
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            // Använd kameraprovidern
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) Analyzer@{ imageProxy ->

                        if (barcodeProcessed) {
                            imageProxy.close() // Stäng imageProxy om en streckkod redan har behandlats
                            return@Analyzer
                        }

                        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                        val mediaImage = imageProxy.image
                        if (mediaImage != null) {
                            val image = InputImage.fromMediaImage(mediaImage, rotationDegrees)
                            scanBarcode(image, imageProxy)
                        }
                    }
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Koppla från alla användningsfall innan de binds igen
                cameraProvider.unbindAll()

                // Bind användningsfallen till kameran
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalysis
                )
            } catch (exc: Exception) {
                Log.e("!!!", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun scanBarcode(image: InputImage, imageProxy: ImageProxy) {
        val scanner = BarcodeScanning.getClient()
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    val rawValue = barcode.rawValue

                    if (barcodeValue == rawValue) {


                        // Kontrollera om vi redan har behandlat en streckkod
                        if (!barcodeProcessed) {
                            barcodeProcessed = true // Uppdatera flaggan

                            // Kör på huvudtråden
                            runOnUiThread {
                                // Starta en ny aktivitet med intent och skicka med streckkodsvärdet som extra data
                                val intent = Intent(
                                    this@BarCodeReaderActivity,
                                    ListDeliveries::class.java
                                ).apply {
                                    putExtra("barcode_value", rawValue)
                                }
                                startActivity(intent)

                                Toast.makeText(
                                    this,
                                    "Scanning successful, $barcodeValue",
                                    Toast.LENGTH_LONG
                                ).show()

                            }
                            break // Bryt loop efter första framgångsrika avläsningen
                        }
                    }

                }
            }
            .addOnFailureListener {
                // Hantera fel här
            }
            .addOnCompleteListener {
                imageProxy.close() // Glöm inte att stänga ImageProxy
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
