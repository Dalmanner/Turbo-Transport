package com.example.turbo_transport

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class DeliveriesRecyclerAdapter(val context: Context, var lists: List<Package>) : RecyclerView.Adapter<DeliveriesRecyclerAdapter.ViewHolder>()  {
    
    var layoutInflater = LayoutInflater.from(context)

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var addressTextView = itemView.findViewById<TextView>(R.id.addressTextView)
        //var postCodeAddressTextView = itemView.findViewById<TextView>(R.id.etPostCodeAddress)
        var etaTimeTextView2 = itemView.findViewById<TextView>(R.id.etaTimeTextView)
        var userNameReceiverTextView = itemView.findViewById<TextView>(R.id.userNameReceiverTextView)
        var etaTimeTextView = itemView.findViewById<TextView>(R.id.etaTimeTextView)
        var itemPosistion = 0

////       init {
////            itemView.setOnClickListener {
////                val intent = Intent(context,PackageActivity::class.java)
////                intent.putExtra(ITEM_POSISTION_KEY,itemPosistion)
////                context.startActivity(intent)
////            }
//
//        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DeliveriesRecyclerAdapter.ViewHolder {
        val itemView = layoutInflater.inflate(R.layout.item_listdeliveries,parent,false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DeliveriesRecyclerAdapter.ViewHolder, position: Int) {
        var deliveryList = lists[position]
        holder.itemView.tag = deliveryList.documentId
        holder.addressTextView.text = deliveryList.address

        val timestamp = deliveryList.expectedDeliveryTime
        val date = timestamp?.toDate() // Konvertera till Date
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val dateString = format.format(date)

        //holder.postCodeAddressTextView.text = deliveryList.postCodeAddress
        holder.userNameReceiverTextView.text = deliveryList.nameOfReceiver
        holder.etaTimeTextView2.text= deliveryList.requestedDeliveryTime.toString()
        holder.etaTimeTextView.text= dateString
        holder.itemPosistion = position
        holder.itemView.setOnClickListener {

            val documentId = it.tag as String
            val intent = Intent(context,PackageActivity::class.java)
            intent.putExtra("documentId",documentId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return lists.size
    }
}