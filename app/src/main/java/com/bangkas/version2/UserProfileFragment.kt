package com.bangkas.version2

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val user = auth.currentUser
        if (user != null) {
            db.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val username = document.getString("name")
                        val email = document.getString("email")
                        val phoneNumber = document.getString("phone")


                        val usernameTextView = view.findViewById<TextView>(R.id.usernameTextView)
                        usernameTextView.text = username

                        val emailTextView = view.findViewById<TextView>(R.id.userEmail)
                        emailTextView.text = email

                        val phoneNumTextView = view.findViewById<TextView>(R.id.phoneNum)
                        phoneNumTextView.text = phoneNumber
                    }
                }
                .addOnFailureListener { exception ->
                }
        }

        val editProfileButton = view.findViewById<Button>(R.id.updateProf)
        editProfileButton.setOnClickListener {
            replaceFragment(EditUserProfileFragment())
        }
    }
    private fun replaceFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment) // Replace 'fragment_container' with your container id
        transaction.addToBackStack(null) // Add this transaction to the back stack
        transaction.commit()
    }

}
