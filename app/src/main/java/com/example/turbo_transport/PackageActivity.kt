package com.example.turbo_transport

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
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
        db.collection("packages").document(documentId).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("!!!", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val thisPackage = snapshot.toObject(Package::class.java)

                if (thisPackage != null) {

                    //Start setting values from Firebase
                    textViewAddress.text = thisPackage.address
                    textViewName.text = thisPackage.nameOfReceiver
                    textViewPhonenumber.text = thisPackage.telephoneNumber
                    textViewDeliveryInstructions.text = thisPackage.deliveryNote
                    textViewPackageInfoWeight.text = thisPackage.packageWeight.toString() + " kg"
                    textViewPackageInfoDimensions.text = "${thisPackage.packageHeight} cm x ${thisPackage.packageLength} cm x ${thisPackage.packageDepth} cm"

                    if (thisPackage.identityCheck == true){
                        textViewSign.text = "Yes"
                    }
                    else {
                        textViewSign.text = "No"
                    }

                    textViewKolliId.text = thisPackage.kolliId

                    val timestamp = thisPackage.expectedDeliveryTime
                    val date = timestamp?.toDate() // Konvertera till Date
                    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val dateString = format.format(date)
                    textViewETA.text = dateString

                }
            } else {
                Log.d("!!!", "Current data: null")
            }
        }
    }

    private fun initializeViews() {
        appBarLayout = findViewById(R.id.appBarLayout)
        topAppBar = findViewById(R.id.topAppBar)
        imageView = findViewById(R.id.imageView)
        textViewAddress = findViewById(R.id.textViewAddress)
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