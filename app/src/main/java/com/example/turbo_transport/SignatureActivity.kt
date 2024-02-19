package com.example.turbo_transport

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.storage
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.util.UUID

class SignatureActivity : AppCompatActivity() {

    private lateinit var documentId: String
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signature)

        auth = com.google.firebase.ktx.Firebase.auth
        db = com.google.firebase.ktx.Firebase.firestore
        storage = com.google.firebase.ktx.Firebase.storage
        documentId = intent.getStringExtra("documentId").toString()

        val clearButton = findViewById<Button>(R.id.clear_button)
        val signatureView = findViewById<SignatureView>(R.id.signature_view)
        val confirmButton = findViewById<Button>(R.id.confirmsignaturebutton)



        clearButton.setOnClickListener {
            signatureView.clearSignature()
        }
        confirmButton.setOnClickListener {

            val bitmap = signatureView.getSignatureBitmap()
            val data = getBitmapData(bitmap)
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
        val signatureRef = storageReference.child("signatures/${UUID.randomUUID()}.png")

        val uploadTask = signatureRef.putBytes(data)
        uploadTask.addOnSuccessListener { taskSnapshot ->
            // Efter lyckad uppladdning, hämta den nedladdningsbara URL:en
            taskSnapshot.storage.downloadUrl.addOnSuccessListener { downloadUri ->
                val signatureUrl = downloadUri.toString()
                updateFirestoreDocument(signatureUrl) // Uppdatera med korrekt URL
            }
        }.addOnFailureListener {
            Log.e("Upload", "Error uploading signature", it)
        }
    }

    private fun updateFirestoreDocument(signatureUrl: String) {
        val collectionPath = "packages"
        //Update database with link to signature
        db.collection(collectionPath).document(documentId)
            .update("signatureLink", signatureUrl, "isDelivered", true)
            .addOnSuccessListener {
                //Send to deliveredpackage activity for further actions
                val intent = Intent(
                    this,
                    PackageDeliveredActivity::class.java
                ).apply {
                    putExtra("documentId", documentId)
                }
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Log.w("!!!", "Error updating document", e)
            }
    }

}
