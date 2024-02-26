package com.example.turbo_transport

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale


class DeliveriesRecyclerAdapter(val context: Context, var lists: List<Package>, val deliveryType: DeliveryType) : RecyclerView.Adapter<DeliveriesRecyclerAdapter.ViewHolder>() {
//  class DeliveriesRecyclerAdapter(val context: Context, var lists: List<Package>, val deliveryType: ViewHolder.DeliveryType) : RecyclerView.Adapter<DeliveriesRecyclerAdapter.ViewHolder>() {  ...
    enum class DeliveryType {
        ACTIVE, DONE, FAILED
    }

    var layoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DeliveriesRecyclerAdapter.ViewHolder {

        //Set correct layout for right tabs
        val layout = when (deliveryType) {
            DeliveryType.ACTIVE -> R.layout.item_listdeliveries
            DeliveryType.DONE -> R.layout.item_donelistdeliveries
            DeliveryType.FAILED -> R.layout.item_failedlistdeliveries
        }
        val itemView = layoutInflater.inflate(layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var deliveryList = lists[position]
        holder.itemView.tag = deliveryList.documentId
        holder.addressTextView.text = deliveryList.address
        holder.postCodeAddress.text = deliveryList.postCodeAddress
        holder.cityName.text = deliveryList.cityName
        val timestamp = deliveryList.expectedDeliveryTime
        val date = timestamp?.toDate() // Konvertera till Date
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val dateString = format.format(date)

        //If different setups in layout we can choose a when condition.
        //Right now it is just the same for each type.
        when (deliveryType) {
            DeliveryType.ACTIVE -> {

            holder.etaTimeTextView.text= dateString
            holder.itemPosition = position
            holder.cityName.text = deliveryList.cityName
            holder.addressTextView.text = deliveryList.address
            holder.userNameReceiverTextView.text = deliveryList.nameOfReceiver
            holder.postCodeAddress.text = deliveryList.postCodeAddress
            holder.etaTimeTextView2.text= deliveryList.requestedDeliveryTime
            holder.leaveAtTheDoor.text = deliveryList.leaveAtTheDoor.toString()
                if (deliveryList.leaveAtTheDoor == true){
                    holder.leaveAtTheDoor.text = " LATD "
                }
                else {
                    holder.leaveAtTheDoor.text = ""
                }
                    holder.itemView.setOnClickListener {

                        val documentId = it.tag as String
                        val intent = Intent(context,PackageActivity::class.java)
                        intent.putExtra("documentId",documentId)
                        context.startActivity(intent)
                    }
                }
                DeliveryType.DONE -> {

                    holder.etaTimeTextView.text= dateString
                    holder.itemPosition = position
                    holder.cityName.text = deliveryList.cityName
                    holder.addressTextView.text = deliveryList.address
                    holder.postCodeAddress.text = deliveryList.postCodeAddress
                    holder.userNameReceiverTextView.text = deliveryList.nameOfReceiver
                    holder.etaTimeTextView2.text= deliveryList.requestedDeliveryTime
                    holder.itemView.setOnClickListener {

                        val documentId = it.tag as String
                        val intent = Intent(context,PackageActivity::class.java)
                        intent.putExtra("documentId",documentId)
                        context.startActivity(intent)
                    }
                }
                DeliveryType.FAILED -> {

                    holder.etaTimeTextView.text= dateString
                    holder.itemPosition = position
                    holder.cityName.text = deliveryList.cityName
                    holder.addressTextView.text = deliveryList.address
                    holder.postCodeAddress.text = deliveryList.postCodeAddress
                    holder.userNameReceiverTextView.text = deliveryList.nameOfReceiver
                    holder.etaTimeTextView2.text= deliveryList.requestedDeliveryTime
                    holder.itemView.setOnClickListener {

                        val kolliId = it.tag as String
                        val intent = Intent(context,PackageActivity::class.java)
                        intent.putExtra("packageId",kolliId)
                        context.startActivity(intent)
                        lists.forEach {
                            Log.d("DeliveriesRecyclerAdapter", "onBindViewHolder: ${it.kolliId}")
                        }
                    }
                }
            }
    }

    override fun getItemCount(): Int {
        return lists.size
    }

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var addressTextView = itemView.findViewById<TextView>(R.id.addressTextView)
        var etaTimeTextView = itemView.findViewById<TextView>(R.id.etaTimeTextView)
        var cityName = itemView.findViewById<TextView>(R.id.cityName)
        var postCodeAddress = itemView.findViewById<TextView>(R.id.postCodeAddress)
        var etaTimeTextView2 = itemView.findViewById<TextView>(R.id.etaTimeTextView2)
        var userNameReceiverTextView = itemView.findViewById<TextView>(R.id.userNameReceiverTextView)
        var leaveAtTheDoor = itemView.findViewById<TextView>(R.id.latdTimeTextView)
        var itemPosition = 0
    }
}