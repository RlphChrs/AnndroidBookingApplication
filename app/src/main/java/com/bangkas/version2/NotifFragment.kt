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

class NotifFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var listView: ListView
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: NotificationAdapter
    private var notifs = mutableListOf<Notif>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notif, container, false)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        listView = view.findViewById(R.id.notificationList)
        adapter = NotificationAdapter(requireContext(), notifs)
        listView.adapter = adapter
        val user = auth.currentUser
        user?.let {
            val query = db.collection("users").document(user.uid).collection("receipts")
            query.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("Firestore", "Error getting documents.", error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    notifs.clear()
                    for (document in snapshot) {
                        val notif = Notif(
                            document.getString("currentLocation") ?: "",
                            document.getString("destination") ?: ""
                        )
                        notifs.add(notif)
                    }
                    adapter.notifyDataSetChanged()
                }
            }
        }

        return view
    }
}
