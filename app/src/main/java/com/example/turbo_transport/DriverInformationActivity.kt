package com.example.turbo_transport

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DriverInformationActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var driverNameTextView: TextView
    private lateinit var driverNumberTextView: TextView
    private lateinit var driverEmailTextView: TextView
    private lateinit var vehicleModelTextView: TextView
    private lateinit var vehicleRegistrationTextView: TextView
    private lateinit var logOutButton: Button
    private lateinit var topAppBarReceiver: MaterialToolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_information)
        db = Firebase.firestore
        auth = Firebase.auth
        initializeViews()
        getUserInforamtion()
        logOutButton.setOnClickListener {
            logOut()
        }
        showMenu()
    }
    private fun logOut() {
        auth.signOut()
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
    }

    private fun showMenu(){
        topAppBarReceiver.setNavigationOnClickListener {
            finish()
        }

        topAppBarReceiver.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {

                else -> false
            }
        }
    }
    private fun getUserInforamtion() {
        val user = auth.uid
        if (user != null) {
            db.collection("users").document(user).get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val userInfo = documentSnapshot.toObject(User::class.java)

                    userInfo?.let {

                        driverNameTextView.text = userInfo.name
                        driverEmailTextView.text = userInfo.Email
                        driverNumberTextView.text = userInfo.userPhoneNumber
                        vehicleModelTextView.text = userInfo.vehicleModel
                        vehicleRegistrationTextView.text = userInfo.vehicleId
                        topAppBarReceiver.setTitle(userInfo.name)

                    }
                } else {
                    Log.d("!!!", "Document does not exist")
                }
            }.addOnFailureListener { exception ->
                Log.w("!!!", "Error getting documents: ", exception)
            }
        }
    }
    private fun initializeViews() {
        driverNameTextView = findViewById(R.id.driverNameTextView)
        driverEmailTextView = findViewById(R.id.driverEmailTextView)
        driverNumberTextView = findViewById(R.id.driverNumberTextView)
        vehicleModelTextView = findViewById(R.id.vehicleModelTextView)
        vehicleRegistrationTextView = findViewById(R.id.vehicleRegistrationTextView)
        logOutButton = findViewById(R.id.logOutBt)
        topAppBarReceiver = findViewById(R.id.topAppBarReceiver3)




    }
}