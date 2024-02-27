package com.example.turbo_transport

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Locale

class ReceiverPackageActivity : AppCompatActivity() {

    private lateinit var topBarrA:MaterialToolbar
    private lateinit var AppBarLayout:AppBarLayout
    private lateinit var imageView: ImageView
    private lateinit var deliveryA: TextView
    private lateinit var adressA: TextView
    private lateinit var nameAndAdress: TextView
    private lateinit var nameA: TextView
    private lateinit var phonenumberA: TextView
    private lateinit var senderNameA: TextView
    private lateinit var packageInfoA: TextView
    private lateinit var packageInfoWeight: TextView
    private lateinit var packageInfoDimensions: TextView
    private lateinit var SignA: TextView
    private lateinit var textViewLATD: TextView
    private lateinit var textViewSignA: TextView
    private lateinit var trackingN: TextView
    private lateinit var trackingNumber: TextView
    private lateinit var packageDeliveryStatus : TextView
    private lateinit var db:FirebaseFirestore



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receiver_package)

        db = Firebase.firestore

        //Initialize all of our views
        initializeViews()

        val documentId = intent.getStringExtra("documentId")
        if (documentId != null) {
            getPackage(documentId)
        }
        topBar()
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

                    adressA.text = thisPackage.address
                    nameA.text = thisPackage.nameOfReceiver
                    phonenumberA.text = thisPackage.telephoneNumber
                    senderNameA.text = thisPackage.senderName
                    //textViewLATD = findViewById(R.id.textViewLATD)
                    if (thisPackage.leaveAtTheDoor == true){
                        textViewLATD.text = "Yes"
                    }
                    else {
                        textViewLATD.text = "No"
                    }

                    packageInfoWeight.text = thisPackage.packageWeight.toString() + " kg"
                    packageInfoDimensions.text = "${thisPackage.packageHeight} cm x ${thisPackage.packageLength} cm x ${thisPackage.packageDepth} cm"
                    if (thisPackage.identityCheck == true){
                        textViewSignA.text = "Yes"
                    }
                    else {
                        textViewSignA.text = "No"
                    }
                    trackingNumber.text = thisPackage.kolliId

                    if (thisPackage.transit == true) {
                        packageDeliveryStatus.text = "In Transit"
                    } else if (thisPackage.banankaka == false) {
                        packageDeliveryStatus.text = "Out for Delivery"
                    } else {
                        packageDeliveryStatus.text = "Delivered"
                    }
                } else {
                    Log.d("!!!", "Current data: null")
                }
            }
        }
    }
    private fun topBar(){
        topBarrA.setNavigationOnClickListener {
          finish()
        }
        topBarrA.setOnMenuItemClickListener { menuItem ->
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
    fun initializeViews(){

        AppBarLayout = findViewById(R.id.appBarLayout)
        topBarrA = findViewById(R.id.topBarrA)
        imageView = findViewById(R.id.imageView)
        deliveryA = findViewById(R.id.deliveryA)
        adressA = findViewById(R.id.adressA)
        nameAndAdress = findViewById(R.id.nameAndAdress)
        nameA = findViewById(R.id.nameA)
        phonenumberA = findViewById(R.id.phonenumberA)
        senderNameA = findViewById(R.id.senderNameA)
        packageInfoA = findViewById(R.id.packageInfoA)
        packageInfoWeight = findViewById(R.id.packageInfoWeight)
        packageInfoDimensions = findViewById(R.id.packageInfoDimensions)
        SignA = findViewById(R.id.SignA)
        textViewSignA = findViewById(R.id.textViewSignA)
        trackingN = findViewById(R.id.trackingN)
        trackingNumber = findViewById(R.id.trackingNumber)
        packageDeliveryStatus = findViewById(R.id.packageStatusTextView)
        textViewLATD = findViewById(R.id.textViewLATD)
    }
}