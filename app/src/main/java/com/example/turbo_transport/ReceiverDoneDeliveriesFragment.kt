package com.example.turbo_transport

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ReceiverDoneDeliveriesFragment : Fragment() {
    private lateinit var receiverDoneRecyclerView: RecyclerView
    private lateinit var db: FirebaseFirestore
    private lateinit var auth : FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_receiver_done_deliveries, container, false)


        db = Firebase.firestore
        receiverDoneRecyclerView = view.findViewById(R.id.receiverRecyclerView)
        receiverDoneRecyclerView.layoutManager = LinearLayoutManager(context)
        auth = com.google.firebase.Firebase.auth
        loadPackageDb()

        return view
    }
    fun loadPackageDb() {
        val user = auth.currentUser
        val packagesRef = db.collection("packages")
        if (user != null) {
            packagesRef.whereEqualTo("userIdReceiver", user.uid)
                .whereEqualTo("banankaka", true)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        return@addSnapshotListener
                    }
                    if (snapshot != null && !snapshot.isEmpty) {
                        val packageList = snapshot.documents.map { document ->
                            document.toObject(Package::class.java)
                        }
                        receiverDoneRecyclerView.adapter = ReceiverRecyclerAdapter(
                            requireContext(),
                            packageList.filterNotNull(),
                            ReceiverRecyclerAdapter.DeliveryType.DONE
                        )
                    }
                }
        }
    }

}