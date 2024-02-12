package com.example.turbo_transport

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DeliveriesRecyclerAdapter(val context: Context, var lists: List<Package>) : RecyclerView.Adapter<DeliveriesRecyclerAdapter.ViewHolder>()  {
    
    var layoutInflater = LayoutInflater.from(context)

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var addressTextView = itemView.findViewById<TextView>(R.id.addressTextView)
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
        holder.etaTimeTextView.text=deliveryList.expectedDeliveryTime
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