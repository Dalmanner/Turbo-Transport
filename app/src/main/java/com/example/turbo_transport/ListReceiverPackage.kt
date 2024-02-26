package com.example.turbo_transport

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessaging

class ListReceiverPackage : AppCompatActivity() {
    lateinit var receiverRecylerView: RecyclerView
    lateinit var db : FirebaseFirestore
    lateinit var auth : FirebaseAuth
    private lateinit var topAppBar: MaterialToolbar
    private lateinit var bottomBar: NavigationBarView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_receiver_package)

        db = Firebase.firestore
        auth = Firebase.auth
        initializeViews()

        receiverRecylerView = findViewById(R.id.receiverRecyclerView)
        receiverRecylerView.layoutManager = LinearLayoutManager(this)
        loadPackageDb()
        setTokenDb()
        topBar()
        bottomMenu()
    }
    override fun onBackPressed() {
        super.onBackPressed()
        auth.signOut()
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
    }
    fun loadPackageDb(){
        val user = auth.currentUser
        val packagesRef = db.collection("packages")
        if (user != null) {
            packagesRef.whereEqualTo("userIdReceiver", user.uid).addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    val packageList = snapshot.documents.map { document ->
                        document.toObject(Package::class.java)
                    }
                    receiverRecylerView.adapter = ReceiverRecyclerAdapter(this@ListReceiverPackage, packageList.filterNotNull())
                }
            }
        }
    }
    private fun topBar(){
        topAppBar.setNavigationOnClickListener {
            auth.signOut()
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
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
    private fun bottomMenu(){

        bottomBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_1 -> {

                    true
                }

                R.id.item_2 -> {

                    true
                }

                R.id.item_3 -> {

                    true
                }
                else -> false
            }
        }

       bottomBar.setOnItemReselectedListener { item ->
            when (item.itemId) {
                R.id.item_1 -> {

                }
                R.id.item_2 -> {


                }
                R.id.item_3 -> {

                    true
                }

            }
        }
    }

    fun setTokenDb() {
        val user = auth.currentUser
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "getToken failed", task.exception)
                    return@OnCompleteListener
                }
                val token = task.result
                Log.d("!!!", "this user token:$token")

                // Flytta uppdateringslogiken här inne
                if (user != null) {
                    db.collection("users").document(user.uid)
                        .set(
                            mapOf("notificationToken" to token),
                            SetOptions.merge()
                        )
                }
            })
    }
    private fun initializeViews() {

        topAppBar = findViewById(R.id.topAppBar1)
        bottomBar = findViewById(R.id.bottom_navigation1)



    }

}
