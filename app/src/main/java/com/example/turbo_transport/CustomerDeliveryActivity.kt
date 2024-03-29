package com.example.turbo_transport

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.properties.Delegates

class CustomerDeliveryActivity : AppCompatActivity() {

    private lateinit var appBarLayout: AppBarLayout
    private lateinit var topAppBar: MaterialToolbar
    private lateinit var signatureButton: Button
    private lateinit var leaveAtTheDoorButton: Button
    private lateinit var documentId: String
    private lateinit var textViewNameReciver: TextView
    private lateinit var textViewAddressReciver: TextView

    private var leaveAtTheDoor = false

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_delivery)

        db = com.google.firebase.ktx.Firebase.firestore

        initializeViews()
        showMenu()


        documentId = intent.getStringExtra("documentId").toString()
        getPackage(documentId)

        signatureButton.setOnClickListener {
            sendToSignatureActivity()
        }
        leaveAtTheDoorButton.setOnClickListener {
            if (leaveAtTheDoor) {
                updateFirestoreDocument()
            }
            else {
                //nope
                Toast.makeText(
                    this,
                    "You are not allowed to leave this package at the door, select another delivery option",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    private fun showMenu(){
        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.user -> {
                    // Handle edit text press
                    true
                }
                R.id.help -> {

                    //we can add an alert here soon, right now hidden back button
                    val intent = Intent(this, ListDeliveries::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
    private fun sendToSignatureActivity() {

        val intent = Intent(this, SignatureActivity::class.java)
        intent.putExtra("documentId", documentId)
        startActivity(intent)
    }
    private fun updateFirestoreDocument() {
        val collectionPath = "packages"
        //Update database with link to signature
        db.collection(collectionPath).document(documentId)
            .update("banankaka", true)
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
    private fun getPackage(documentId: String) {
        db.collection("packages").document(documentId).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("!!!", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val thisPackage = snapshot.toObject(Package::class.java)

                if (thisPackage != null) {

                    textViewNameReciver.text = thisPackage.nameOfReceiver
                    textViewAddressReciver.text = thisPackage.address

                    if (thisPackage.leaveAtTheDoor == true){
                       leaveAtTheDoor = true
                    }
                }
            } else {
                Log.d("!!!", "Current data: null")
            }
        }
    }
    private fun initializeViews() {
        appBarLayout = findViewById(R.id.appBarLayout)
        topAppBar = findViewById(R.id.topAppBar)
        signatureButton = findViewById(R.id.signaturebutton)
        leaveAtTheDoorButton = findViewById(R.id.leavatthedoorbutton)
        textViewAddressReciver = findViewById(R.id.textViewAddressReciever)
        textViewNameReciver = findViewById(R.id.textViewNameReciver)

    }
}