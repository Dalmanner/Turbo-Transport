package com.example.turbo_transport

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class ListReceiverPackage : AppCompatActivity() {
    lateinit var receiverRecylerView: RecyclerView
    lateinit var db : FirebaseFirestore
    lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_receiver_package)

        db = Firebase.firestore
        auth = Firebase.auth

        receiverRecylerView = findViewById(R.id.receiverRecyclerView)
        receiverRecylerView.layoutManager = LinearLayoutManager(this)
        loadPackageDb()
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

}
