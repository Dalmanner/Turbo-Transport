package com.example.turbo_transport

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore

class PackageDeliveredActivity : AppCompatActivity() {

    private lateinit var packageDeliveredProgressBar: LinearProgressIndicator
    private lateinit var imageViewPackageDelivered: ImageView
    private lateinit var packageDeliveredTextView: TextView
    private lateinit var closeButtonDeliveredPackage: Button

    private lateinit var documentId: String
    private var failedDelivery = false

    private lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_package_delivered)

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
            .update("deliveryStatus", false)
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

    }
}