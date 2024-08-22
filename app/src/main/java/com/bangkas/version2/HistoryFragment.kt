package com.bangkas.version2

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HistoryFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var listView: ListView
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: HistoryAdapter
    private var receipts = mutableListOf<Receipt>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        listView = view.findViewById(R.id.histList)
        adapter = HistoryAdapter(requireContext(), receipts)
        listView.adapter = adapter
        val user = auth.currentUser
        user?.let {
            val query = db.collection("users").document(user.uid).collection("receipts")
            query.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("Firestore", "Error getting documents.", error)
                    return@addSnapshotListener
                }

                receipts.clear()
                for (document in snapshot!!) {
                    val receipt = Receipt(
                        document.getString("currentLocation") ?: "",
                        document.getString("destination") ?: "",
                        document.getString("selectedVessel") ?: "",
                        document.getString("paymentMethod") ?: "",
                        document.getLong("paymentAmount")?.toInt() ?: 0,
                        document.getTimestamp("serverDate")?.toDate()
                    )
                    receipts.add(receipt)
                }
                adapter.notifyDataSetChanged()
            }
        }

        return view
    }
}
