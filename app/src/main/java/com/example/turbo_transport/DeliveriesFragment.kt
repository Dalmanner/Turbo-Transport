package com.example.turbo_transport

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class DeliveriesFragment : Fragment() {

    private lateinit var deliveryRecyclerView: RecyclerView
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_delivieries, container, false)

        db = Firebase.firestore
        deliveryRecyclerView = view.findViewById(R.id.deliveriesRecycleView)
        deliveryRecyclerView.layoutManager = LinearLayoutManager(context)
        loadPackageDb()

        return view


    }

    class DragCallback(private val adapter: DeliveriesRecyclerAdapter) : ItemTouchHelper.Callback() {

        override fun isLongPressDragEnabled(): Boolean = true

        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            return makeMovementFlags(dragFlags, 0)
        }

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            adapter.moveItem(viewHolder.adapterPosition, target.adapterPosition)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            // Handle swipe if needed
        }

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            super.onSelectedChanged(viewHolder, actionState)
            // Handle the start of the drag (actionState == ItemTouchHelper.ACTION_STATE_DRAG)
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)
            // This is called when the drag is completed.
            adapter.notifyDataSetChanged() // Refresh the entire list to update displayed numbers.
        }
    }


    private fun loadPackageDb() {
        val packagesRef = db.collection("packages").whereEqualTo("banankaka", false).whereEqualTo("deliveryStatus",true)
        packagesRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("DeliveriesFragment", "Listen failed.", e)
                return@addSnapshotListener
            }
            if (snapshot != null && !snapshot.isEmpty) {
                val packageList = snapshot.documents.mapNotNull { it.toObject(Package::class.java) }
                deliveryRecyclerView.adapter = DeliveriesRecyclerAdapter(requireContext(),
                    packageList as MutableList<Package>, DeliveriesRecyclerAdapter.DeliveryType.ACTIVE)
                if (snapshot != null && !snapshot.isEmpty) {
                    for (document in snapshot.documents) {
                        val documentId = document.id
                        println("Document ID: $documentId")
                        db.collection("packages").document(documentId)
                            .set(
                                mapOf("transit" to false),
                                SetOptions.merge()
                            )
                    }
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    val packageList = snapshot.documents.mapNotNull { it.toObject(Package::class.java) }.toMutableList()
                    val adapter = DeliveriesRecyclerAdapter(requireContext(), packageList, DeliveriesRecyclerAdapter.DeliveryType.ACTIVE)
                    deliveryRecyclerView.adapter = adapter

                    // Attach ItemTouchHelper here
                    val callback = DragCallback(adapter)
                    val itemTouchHelper = ItemTouchHelper(callback)
                    itemTouchHelper.attachToRecyclerView(deliveryRecyclerView)
                }


            }
        }
    }
}
