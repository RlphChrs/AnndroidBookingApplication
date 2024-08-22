package com.bangkas.version2

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import java.util.Date

class HistoryAdapter(private val context: Context, private val receipts: List<Receipt>) : BaseAdapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int = receipts.size

    override fun getItem(position: Int): Receipt = receipts[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: inflater.inflate(R.layout.item_receipt, parent, false)

        val currentLocation = view.findViewById<TextView>(R.id.currentLocation)
        val destination = view.findViewById<TextView>(R.id.destination)
        val selectedVessel = view.findViewById<TextView>(R.id.selectedVessel)
        val paymentMethod = view.findViewById<TextView>(R.id.paymentMethod)
        val paymentAmount = view.findViewById<TextView>(R.id.paymentAmount)
        val timestamp = view.findViewById<TextView>(R.id.timestamp)


        val receipt = getItem(position)

        currentLocation.text = "From: ${receipt.currentLocation}"
        destination.text = "To: ${receipt.destination}"
        selectedVessel.text = "Vessel: ${receipt.selectedVessel}"
        paymentMethod.text = "Mode of Payment: ${receipt.paymentMethod}"
        paymentAmount.text = "Amount Paid: ₱${receipt.paymentAmount}.00"
        timestamp.text = "Date & Time: ${receipt.timestamp.toString()}"


        return view
    }
}

data class Receipt(
    val currentLocation: String,
    val destination: String,
    val selectedVessel: String,
    val paymentMethod: String,
    val paymentAmount: Int,
    val timestamp: Date?
)
