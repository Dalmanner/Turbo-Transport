package com.example.turbo_transport

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore

class ListDeliveries : AppCompatActivity() {
    lateinit var deliveryRecycleView: RecyclerView
    lateinit var db : FirebaseFirestore
    lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listdeliveries)
        val MapButton= findViewById<ImageButton>(R.id.btnSubmit)


        db = Firebase.firestore
        auth = Firebase.auth

        deliveryRecycleView = findViewById(R.id.deliveriesRecycleView)
        deliveryRecycleView.layoutManager = LinearLayoutManager(this)
        loadPackageDb()

        MapButton.setOnClickListener {
            showLocation()
        }

        val addButton = findViewById<ImageButton>(R.id.addPackageBtn)
        addButton.setOnClickListener {
            val intent = Intent(this, AddPackageActivity::class.java)
            startActivity(intent)
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        auth.signOut()
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
    }
    fun showLocation() {
        val intent = Intent(this,MapsActivity::class.java)
        startActivity(intent)
    }

    fun loadPackageDb(){
        val packagesRef = db.collection("packages")
        packagesRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            if (snapshot != null && !snapshot.isEmpty) {
                Log.d("!!!", "Got snapshot: ${snapshot.documents}")
                val packageList = snapshot.documents.map { document ->
                    document.toObject(Package::class.java)
                }
                deliveryRecycleView.adapter = DeliveriesRecyclerAdapter(this@ListDeliveries, packageList.filterNotNull())
            }
        }
    }
}