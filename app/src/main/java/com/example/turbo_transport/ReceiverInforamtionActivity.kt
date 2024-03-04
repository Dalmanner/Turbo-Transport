package com.example.turbo_transport

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Locale

class ReceiverInforamtionActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var receiverTextViewAddress: TextView
    private lateinit var receiverPostCodeTextView: TextView
    private lateinit var receiverTextViewCityName: TextView
    private lateinit var receiverTextViewName: TextView
    private lateinit var receiverTelePhoneNumberTextView: TextView
    private lateinit var receiveEmailTextView: TextView
    private lateinit var logOutButton: Button
    private lateinit var topAppBarReceiver: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receiver_inforamtion)
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

                       receiverTextViewAddress.text = userInfo.userAddress
                       receiverPostCodeTextView.text = userInfo.postCode
                       receiverTextViewCityName.text = userInfo.cityName
                       receiverTextViewName.text = userInfo.name
                       receiverTelePhoneNumberTextView.text= userInfo.userPhoneNumber
                       receiveEmailTextView.text = userInfo.Email
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
        receiverTextViewAddress = findViewById(R.id.receiverTextViewAddress)
        receiverPostCodeTextView = findViewById(R.id.receiverPostCodeTextView)
        receiverTextViewCityName = findViewById(R.id.receiverTextViewCityName)
        receiverTextViewName = findViewById(R.id.receiverTextViewName)
        receiverTelePhoneNumberTextView = findViewById(R.id.receiverTelePhoneNumberTextView)
        receiveEmailTextView = findViewById(R.id.receiveEmailTextView)
        logOutButton = findViewById(R.id.logOutButton)
        topAppBarReceiver = findViewById(R.id.topAppBarReceiver)


    }
}