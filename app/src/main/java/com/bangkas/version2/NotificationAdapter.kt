package com.bangkas.version2

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class NotificationAdapter(private val context: Context, private val receipts: MutableList<Notif>) :
    BaseAdapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int = receipts.size

    override fun getItem(position: Int): Notif = receipts[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: inflater.inflate(R.layout.item_notification, parent, false)

        val currentLocation = view.findViewById<TextView>(R.id.notifFrom)
        val destination = view.findViewById<TextView>(R.id.notifDest)


        val receipt = getItem(position)

        currentLocation.text = "From: ${receipt.currentLocation}"
        destination.text = "To: ${receipt.destination}"



        return view
    }
}

data class Notif(
    val currentLocation: String,
    val destination: String
)
