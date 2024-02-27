package com.example.turbo_transport

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReceiverRecyclerAdapter(val context: Context, var lists: List<Package>, val deliveryType : DeliveryType) : RecyclerView.Adapter<ReceiverRecyclerAdapter.ViewHolder>()  {



    enum class DeliveryType {
        ACTIVE, DONE
    }
    var layoutInflater = LayoutInflater.from(context)



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReceiverRecyclerAdapter.ViewHolder {

        //Set correct layout for right tabs
        val layout = when (deliveryType) {
            DeliveryType.ACTIVE -> R.layout.item_listreceiverpackage
            DeliveryType.DONE -> R.layout.item_deliveredreceiver
        }
        val itemView = layoutInflater.inflate(layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ReceiverRecyclerAdapter.ViewHolder, position: Int) {
        var receiverDeliveryList = lists[position]
        holder.itemView.tag = receiverDeliveryList.documentId
        holder.senderTextView.text = receiverDeliveryList.senderName

        if (receiverDeliveryList.transit == true) {
            holder.deliveryStatusTextView.text = "In Transit"
        } else if (receiverDeliveryList.banankaka == false) {
            holder.deliveryStatusTextView.text = "Out for Delivery"
        } else {
            holder.deliveryStatusTextView.text = "Delivered"
        }
        holder.itemPosistion = position


        /*
        holder.itemView.setOnClickListener {

            val documentId = it.tag as String
            val intent = Intent(context,ReceiverPackageActivity::class.java)
            intent.putExtra("documentId",documentId)
            context.startActivity(intent)
        }

         */


        when (deliveryType) {
            DeliveryType.ACTIVE -> {
                holder.itemView.tag = receiverDeliveryList.documentId
                holder.senderTextView.text = receiverDeliveryList.senderName
                if (receiverDeliveryList.transit == true) {
                    holder.deliveryStatusTextView.text = "In Transit"
                } else if (receiverDeliveryList.banankaka == false) {
                    holder.deliveryStatusTextView.text = "Out for Delivery"
                } else {
                    holder.deliveryStatusTextView.text = "Delivered"
                }
                holder.itemPosistion = position


                holder.itemView.setOnClickListener {

                    val documentId = it.tag as String
                    val intent = Intent(context, ReceiverPackageActivity::class.java)
                    intent.putExtra("documentId", documentId)
                    context.startActivity(intent)
                }
            }

           DeliveryType.DONE -> {
                holder.itemView.tag = receiverDeliveryList.documentId
                holder.senderTextView.text = receiverDeliveryList.senderName
                if (receiverDeliveryList.transit == true) {
                    holder.deliveryStatusTextView.text = "In Transit"
                } else if (receiverDeliveryList.banankaka == false) {
                    holder.deliveryStatusTextView.text = "Out for Delivery"
                } else {
                    holder.deliveryStatusTextView.text = "Delivered"
                }
                holder.itemPosistion = position

                holder.itemView.setOnClickListener {
                    val documentId = it.tag as String
                    val intent = Intent(context, ReceiverPackageActivity::class.java)
                    intent.putExtra("documentId", documentId)
                    context.startActivity(intent)
                }

            }
        }

    }

    override fun getItemCount(): Int {
        return lists.size
    }
    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var senderTextView = itemView.findViewById<TextView>(R.id.senderTextView)
        var deliveryStatusTextView = itemView.findViewById<TextView>(R.id.deliveryStatusTimeTextView)
        var itemPosistion = 0


    }
}