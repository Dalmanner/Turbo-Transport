package com.example.turbo_transport

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DoneDeliveriesFragment : Fragment() {

    private lateinit var deliveryRecyclerView: RecyclerView
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_done_delivieries, container, false)

        db = Firebase.firestore
        deliveryRecyclerView = view.findViewById(R.id.deliveriesRecycleView)
        deliveryRecyclerView.layoutManager = LinearLayoutManager(context)
        loadPackageDb()

        return view
    }

    private fun loadPackageDb() {
        val packagesRef = db.collection("packages").whereEqualTo("banankaka", true)
        packagesRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("ActiveDeliveriesFragment", "Listen failed.", e)
                return@addSnapshotListener
            }
            if (snapshot != null && !snapshot.isEmpty) {
                val packageList = snapshot.documents.mapNotNull { it.toObject(Package::class.java) }
                deliveryRecyclerView.adapter = DeliveriesRecyclerAdapter(requireContext(),
                    packageList.toMutableList(), DeliveriesRecyclerAdapter.DeliveryType.DONE)
            }
        }
    }
}
