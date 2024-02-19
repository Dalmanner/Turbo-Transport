package com.example.turbo_transport

import android.content.Intent
import android.graphics.ImageFormat
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class BarCodeReaderActivity : AppCompatActivity() {
    private lateinit var viewFinder: PreviewView
    private lateinit var cameraExecutor: ExecutorService
    private var barcodeValue = ""
    private var barcodeProcessed = false
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var topAppBar: MaterialToolbar
    private lateinit var documentId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bar_code_reader)

        initializeViews()

        cameraExecutor = Executors.newSingleThreadExecutor()

        //Get the barcodevalue from last activity
        barcodeValue = intent.getStringExtra("barcodeValue").toString()
        documentId = intent.getStringExtra("documentId").toString()

        startCamera()
    }

    @OptIn(ExperimentalGetImage::class)
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {

            //Use cameraprovider
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
                //Unbind all
                cameraProvider.unbindAll()

               //Bind necessary stuff
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

                        playTaDaSound()

                        //Check if you have already proccessed the bar code
                        if (!barcodeProcessed) {
                            barcodeProcessed = true // Uppdatera flaggan

                            //Make sure to run the intent on main thread
                            runOnUiThread {

                                //Send to customerdelivery activity for further actions
                                val intent = Intent(
                                    this@BarCodeReaderActivity,
                                    CustomerDeliveryActivity::class.java
                                ).apply {
                                    putExtra("barcodeValue", rawValue)
                                    putExtra("documentId", documentId)
                                }
                                startActivity(intent)

                                //Lets toast!
                                Toast.makeText(
                                    this,
                                    "Scanning successful, $barcodeValue",
                                    Toast.LENGTH_LONG
                                ).show()

                            }
                            break //No need to repeat process
                        }
                    }
                    playErrorSound()

                }
            }
            .addOnFailureListener {
                //Any errors..
            }
            .addOnCompleteListener {
                imageProxy.close() //Close
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun showMenu(){
        topAppBar.setNavigationOnClickListener {
            finish()
        }
        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.user -> {
                    // Handle edit text press
                    true
                }
                R.id.help -> {
                    // Handle favorite icon press
                    true
                }
                else -> false
            }
        }
    }
    private fun playErrorSound() {

        val mediaPlayer = MediaPlayer.create(this, R.raw.error)
        mediaPlayer.setOnCompletionListener {
            //Release resources
            it.release()
        }
        mediaPlayer.start() //Play sound
    }
    private fun playTaDaSound() {

        val mediaPlayer = MediaPlayer.create(this, R.raw.tada)
        mediaPlayer.setOnCompletionListener {
            //Release resources
            it.release()
        }
        mediaPlayer.start() //Play sound
    }
    private fun initializeViews() {
        viewFinder = findViewById(R.id.viewFinder)
        appBarLayout = findViewById(R.id.appBarLayout)
        topAppBar = findViewById(R.id.topAppBar)
    }
}
