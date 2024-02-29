package com.example.turbo_transport

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.UUID

const val REQUEST_IMAGE_CAPTURE = 1




class PackageDeliveredActivity : AppCompatActivity() {
    private lateinit var storage: FirebaseStorage


    private lateinit var packageDeliveredProgressBar: LinearProgressIndicator
    private lateinit var imageViewPackageDelivered: ImageView
    private lateinit var failedDeliveryImageView : ImageView
    private lateinit var packageDeliveredTextView: TextView
    private lateinit var closeButtonDeliveredPackage: Button

    private lateinit var failedDeliveryPictureButton: Button

    //private lateinit var cameraProvider: ProcessCameraProvider
  //  private lateinit var cameraSelector: CameraSelector
   // private lateinit var preview: Preview
   // private lateinit var imageAnalysis: ImageAnalysis
   // private lateinit var imageCapture: ImageCapture


    private lateinit var documentId: String
    private var failedDelivery = false

    private lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_package_delivered)

        storage = com.google.firebase.ktx.Firebase.storage
        db = com.google.firebase.ktx.Firebase.firestore


        failedDelivery = intent.getBooleanExtra("failedDelivery", false)

        documentId = intent.getStringExtra("documentId").toString()

        if (failedDelivery == true){
            updateFirestoreDocument(documentId)
        }

        initializeViews()
        packageDeliveredProgressBar.visibility = View.GONE
        closeButtonDeliveredPackage.setOnClickListener {
            goBackToStartPage()
        }
        failedDeliveryPictureButton.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)

        }
        //val data = getBitmapData(bitmap)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            // Nu kan du använda imageBitmap som du vill.
           failedDeliveryImageView.setImageBitmap(imageBitmap)
            val data = getBitmapData(imageBitmap)
            uploadSignatureToFirebaseStorage(data)
        }
    }

    private fun getBitmapData(bitmap: Bitmap): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    private fun uploadSignatureToFirebaseStorage(data: ByteArray) {
        val storageReference = storage.reference
        val signatureRef = storageReference.child("failedPictures/${UUID.randomUUID()}.png")

        val uploadTask = signatureRef.putBytes(data)
        uploadTask.addOnSuccessListener { taskSnapshot ->
            //After successfully uploaded the image/signature, get the link and save it to firestore
            taskSnapshot.storage.downloadUrl.addOnSuccessListener { downloadUri ->
                val signatureUrl = downloadUri.toString()
                updateFirestoreDocument(signatureUrl) // Uppdatera med korrekt URL
            }
        }.addOnFailureListener {
            Log.e("Upload", "Error uploading signature", it)
        }
    }





    private fun goBackToStartPage(){
        //clear all running activites in the background before moving "back" in the pile, ie now
        //the "delivery loop" is finished
        val intent = Intent(this, ListDeliveries::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }
    private fun updateFirestoreDocument(signatureUrl: String) {
        val collectionPath = "packages"
        //Update database with link to signature
        db.collection(collectionPath).document(documentId)
            .update("failedPictureLink", signatureUrl,"deliveryStatus", false)
            .addOnSuccessListener {
                //display error message
                packageDeliveredTextView.text = "Error delivering package... "

            }
            .addOnFailureListener { e ->
                Log.w("!!!", "Error updating document", e)
            }
    }


    private fun initializeViews(){
        packageDeliveredProgressBar = findViewById(R.id.packageDeliveredProgressBar)
        imageViewPackageDelivered = findViewById(R.id.imageViewPackageDelivered)
        packageDeliveredTextView = findViewById(R.id.packageDeliveredTextView)
        closeButtonDeliveredPackage = findViewById(R.id.closeButtonDeliveredPackage)
        failedDeliveryPictureButton = findViewById(R.id.failedBt)
        failedDeliveryImageView = findViewById(R.id.failedImageView)

    }
}