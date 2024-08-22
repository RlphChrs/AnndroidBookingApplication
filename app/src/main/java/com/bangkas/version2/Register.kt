package com.bangkas.version2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class Register : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    lateinit var etEmail: EditText
    lateinit var etPass: EditText
    lateinit var etName: EditText
    lateinit var etPhone: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val lgn = findViewById<TextView>(R.id.lgnTxt)
        val reg = findViewById<Button>(R.id.regBtn)
        etEmail = findViewById(R.id.regEmailInput)
        etPass = findViewById(R.id.regPassInput)
        etName = findViewById(R.id.userInput)
        etPhone = findViewById(R.id.phoneNumber)
        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()

        lgn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        reg.setOnClickListener {
            val email = etEmail.text.toString()
            val pass = etPass.text.toString()
            val name = etName.text.toString()
            val phone = etPhone.text.toString()

            if (email.isEmpty() || pass.isEmpty() || name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "All fields must be filled out", Toast.LENGTH_SHORT).show()
            } else {
                createAccount(email, pass, name, phone)
            }
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            reload()
        }
    }

    private fun createAccount(email: String, password: String, name: String, phone: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    saveUserProfile(user, email, name, phone)
                    Toast.makeText(baseContext, "Registered Successfully", Toast.LENGTH_SHORT).show()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUserProfile(user: FirebaseUser?, email: String, name: String, phone: String) {
        if (user == null) return

        val userProfile = hashMapOf(
            "email" to email,
            "name" to name,
            "phone" to phone
        )

        db.collection("users").document(user.uid)
            .set(userProfile)
            .addOnSuccessListener {
                Log.d(TAG, "User profile successfully written!")
                updateUI(user)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error writing user profile", e)
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("USER_ID", user.uid)
            startActivity(intent)
            finish()
        }
    }

    private fun reload() {}

    companion object {
        private const val TAG = "EmailPassword"
    }
}
