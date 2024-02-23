package com.example.turbo_transport

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.Locale

class PackageActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage

    private lateinit var appBarLayout: AppBarLayout
    private lateinit var topAppBar: MaterialToolbar
    private lateinit var imageView: ImageView
    private lateinit var textViewAddress: TextView
    private lateinit var postCodeAddress: TextView
    private lateinit var textViewLeaveAtTheDoor: TextView
    private lateinit var textViewCityName: TextView
    private lateinit var textViewName: TextView
    private lateinit var textViewPhonenumber: TextView
    private lateinit var headlineDeliveryInstructions: TextView
    private lateinit var textViewDeliveryInstructions: TextView
    private lateinit var headlinePackageInfo: TextView
    private lateinit var textViewPackageInfoWeight: TextView
    private lateinit var textViewPackageInfoDimensions: TextView
    private lateinit var headlineSign: TextView
    private lateinit var textViewSign: TextView
    private lateinit var headlineKolliId: TextView
    private lateinit var textViewKolliId: TextView
    private lateinit var headlineETA: TextView
    private lateinit var textViewETA: TextView
    private lateinit var button: Button
    //private lateinit var textViewPostCodeAddress: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_package)

        //Setup firebase variables
        db = Firebase.firestore
        auth = Firebase.auth
        storage = Firebase.storage

        //Initialize all of our views
        initializeViews()

        showMenu()

        //Get documentId
        val documentId = intent.getStringExtra("documentId")
        if (documentId != null) {
            getPackage(documentId)
        }
        button.setOnClickListener {
            if (documentId != null) {
                sendToRoute(documentId)
            }
        }
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
    private fun sendToRoute(documentId: String){
        val progressBar = findViewById<ProgressBar>(R.id.packageProgressBar)
        progressBar.visibility = View.VISIBLE
        //Send to route activity
        val intent = Intent(this, RouteActivity::class.java)
        intent.putExtra("documentId", documentId)
        startActivity(intent)
        Handler(Looper.getMainLooper()).postDelayed({
            progressBar.visibility = View.GONE
        }, 2000)

    }



    private fun getPackage(documentId: String) {
        db.collection("packages").document(documentId).get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val thisPackage = documentSnapshot.toObject(Package::class.java)
               // db.collection("packages").document(documentId).get().addOnSuccessListener { documentSnapshot ->
                    val isDelivered = documentSnapshot.getBoolean("isDelivered")
                   Log.d("PackageDeliveryCheck", "isDelivered: $isDelivered")
                //}
                thisPackage?.let {
                    Log.d("!!!","isdeliv ${thisPackage.banankaka}")
                    //Start setting values from Firebase
                    textViewCityName.text = thisPackage.cityName
                    postCodeAddress.text = thisPackage.postCodeAddress
                    textViewAddress.text = it.address
                    textViewName.text = it.nameOfReceiver
                    textViewPhonenumber.text = it.telephoneNumber
                    textViewDeliveryInstructions.text = it.deliveryNote
                    textViewPackageInfoWeight.text = it.packageWeight.toString() + " kg"
                    textViewPackageInfoDimensions.text = "${it.packageHeight} cm x ${it.packageLength} cm x ${it.packageDepth} cm"
                     
                    if (thisPackage.leaveAtTheDoor == true){
                        
                        textViewLeaveAtTheDoor.text = "Yes"
                    }
                    else {
                        textViewLeaveAtTheDoor.text = "No"
                    }
                    
                    if (it.identityCheck == true){

                        textViewSign.text = "Yes"
                    }
                    else {
                        textViewSign.text = "No"
                    }

                    textViewKolliId.text = it.kolliId


                    if (thisPackage.banankaka == true){
                        button.visibility = View.GONE
                        textViewETA.text = "Package was delivered at xx:xx"
                    }
                    else {
                        val timestamp = it.expectedDeliveryTime
                        val date = timestamp?.toDate() //Convert to date
                        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        val dateString = format.format(date)
                        textViewETA.text = dateString
                    }
                }
            } else {
                Log.d("!!!", "Document does not exist")
            }
        }.addOnFailureListener { exception ->
            Log.w("!!!", "Error getting documents: ", exception)
        }
    }


    private fun initializeViews() {
        textViewLeaveAtTheDoor = findViewById(R.id.textViewLeaveAtTheDoor)
        postCodeAddress = findViewById(R.id.textViewPostCodeAddress)
        appBarLayout = findViewById(R.id.appBarLayout)
        topAppBar = findViewById(R.id.topAppBar)
        imageView = findViewById(R.id.imageView)
        textViewAddress = findViewById(R.id.textViewAddress)
        textViewCityName = findViewById(R.id.textViewCityName)
        textViewName = findViewById(R.id.textViewName)
        textViewPhonenumber = findViewById(R.id.textViewPhonenumber)
        headlineDeliveryInstructions = findViewById(R.id.headlineDeliveryInstructions)
        textViewDeliveryInstructions = findViewById(R.id.textViewDeliveryInstructions)
        headlinePackageInfo = findViewById(R.id.headlinePackageInfo)
        textViewPackageInfoWeight = findViewById(R.id.textViewPackageInfoWeight)
        textViewPackageInfoDimensions = findViewById(R.id.textViewPackageInfoDimensions)
        headlineSign = findViewById(R.id.headlineSign)
        textViewSign = findViewById(R.id.textViewSign)
        headlineKolliId = findViewById(R.id.headlineKolliId)
        textViewKolliId = findViewById(R.id.textViewKolliId)
        headlineETA = findViewById(R.id.headlineETA)
        textViewETA = findViewById(R.id.textViewETA)
        button = findViewById(R.id.button)
    }
}