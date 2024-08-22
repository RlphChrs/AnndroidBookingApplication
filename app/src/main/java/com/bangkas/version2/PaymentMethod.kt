package com.bangkas.version2

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class PaymentMethod : AppCompatActivity() {

    private lateinit var selectedPaymentMethod: String
    private var paymentAmount: Int = 0
    private lateinit var selectedVessel: String
    private lateinit var currentLocation: String
    private lateinit var destination: String
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_payment_method)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val back = findViewById<ImageButton>(R.id.paymenyBckBtn)  // Corrected typo
        val continueButton = findViewById<Button>(R.id.paymentContinue)
        val transPMValue = findViewById<TextView>(R.id.transPMValue)
        val amountValue = findViewById<TextView>(R.id.amountValue)

        // Retrieve data from intent
        currentLocation = intent.getStringExtra("currentLocation") ?: "Unknown"
        destination = intent.getStringExtra("destination") ?: "Unknown"
        selectedVessel = intent.getStringExtra("SELECTED_VESSEL") ?: "No Vessel Selected"
        paymentAmount = intent.getIntExtra("CHARGE", 0)

        // Set default payment method
        selectedPaymentMethod = "GCash"

        // Initialize views with default values
        transPMValue.text = selectedPaymentMethod
        amountValue.text = "₱$paymentAmount.00"

        // Payment method checkboxes
        val paymBox1 = findViewById<CheckBox>(R.id.paymBox1)
        val paymBox2 = findViewById<CheckBox>(R.id.paymBox2)
        val paymBox3 = findViewById<CheckBox>(R.id.paymBox3)

        paymBox1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                paymBox2.isChecked = false
                paymBox3.isChecked = false
                selectedPaymentMethod = "GCash"
                updateTransactionDetails(transPMValue, amountValue)
            }
        }

        paymBox2.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                paymBox1.isChecked = false
                paymBox3.isChecked = false
                selectedPaymentMethod = "Visa"
                updateTransactionDetails(transPMValue, amountValue)
            }
        }

        paymBox3.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                paymBox1.isChecked = false
                paymBox2.isChecked = false
                selectedPaymentMethod = "Cash"
                updateTransactionDetails(transPMValue, amountValue)
            }
        }

        back.setOnClickListener {
            val intent = Intent(this, BangkaSelect::class.java)
            val options = ActivityOptions.makeCustomAnimation(this, 0, 0)
            startActivity(intent, options.toBundle())
        }

        continueButton.setOnClickListener {
            showReceiptPopup()
        }
    }

    private fun updateTransactionDetails(transPMValue: TextView, amountValue: TextView) {
        transPMValue.text = selectedPaymentMethod
        amountValue.text = "₱$paymentAmount.00"
    }

    private fun showReceiptPopup() {
        val inflater = LayoutInflater.from(this)
        val popupView = inflater.inflate(R.layout.receipt_popup, null)

        // Set up receipt details
        val receiptCurrentLocation = popupView.findViewById<TextView>(R.id.receiptCurrentLocation)
        val receiptUserDestination = popupView.findViewById<TextView>(R.id.receiptUserDestination)
        val receiptVessel = popupView.findViewById<TextView>(R.id.receiptVessel)
        val receiptAmount = popupView.findViewById<TextView>(R.id.receiptAmount)
        val receiptPaymentMethod = popupView.findViewById<TextView>(R.id.receiptPaymentMethod)

        receiptCurrentLocation.text = "Current Location: $currentLocation"
        receiptUserDestination.text = "Destination: $destination"
        receiptVessel.text = "Selected Vessel: $selectedVessel"
        receiptAmount.text = "Amount: ₱$paymentAmount.00"
        receiptPaymentMethod.text = "Payment Method: $selectedPaymentMethod"

        val builder = AlertDialog.Builder(this)
            .setView(popupView)
            .setCancelable(true)

        val dialog = builder.create()
        popupView.findViewById<Button>(R.id.receiptCloseButton).setOnClickListener {
            dialog.dismiss()
            sendNotification()
            sendReceipt()
            navigateToBangkaWaiting()
        }
        dialog.show()
    }

    private fun sendNotification() {
        val sharedPref = getSharedPreferences("notifications", Context.MODE_PRIVATE) ?: return
        val notifications = sharedPref.getStringSet("notifications_list", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        notifications.add("Your booking is approved, the vessel is waiting.")
        with(sharedPref.edit()) {
            putStringSet("notifications_list", notifications)
            apply()
        }
        // Log the notifications to check if they are being saved correctly
        Log.d("PaymentMethod", "Notifications updated: $notifications")
    }

    private fun navigateToBangkaWaiting() {
        val intent = Intent(this, BangkaWaiting::class.java)
        val options = ActivityOptions.makeCustomAnimation(this, 0, 0)
        startActivity(intent, options.toBundle())

        // Reload or refresh NotificationFragment if necessary
        val fragment = supportFragmentManager.findFragmentById(R.id.notificationFragment)
        fragment?.let {
            supportFragmentManager.beginTransaction().detach(it).attach(it).commit()
        }
    }

    private fun sendReceipt() {
        val user = auth.currentUser
        if (user != null) {
            val currentLoc = currentLocation
            val dest = destination
            val payment = selectedPaymentMethod
            val vessel = selectedVessel
            val amount = paymentAmount

            val userProfile = hashMapOf(
                "currentLocation" to currentLoc,
                "destination" to dest,
                "paymentMethod" to payment,
                "selectedVessel" to vessel,
                "paymentAmount" to amount,
                "serverDate" to FieldValue.serverTimestamp()
            )

            // Log userProfile data for debugging
            Log.d("PaymentMethod", "User Profile Data: $userProfile")

            db.collection("users").document(user.uid).collection("receipts")
                .add(userProfile)
                .addOnSuccessListener {
                    Toast.makeText(this, "Receipt sent successfully", Toast.LENGTH_SHORT).show()
                    Log.d("PaymentMethod", "Receipt sent successfully")
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to send receipt: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("PaymentMethod", "Failed to send receipt", e)
                }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            Log.e("PaymentMethod", "User not logged in")
        }
    }
}
