package com.mab.buwisbuddyph.home

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.IsReadyToPayRequest
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.Wallet
import com.google.android.gms.wallet.WalletConstants
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_JPEG
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_PDF
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.SCANNER_MODE_FULL
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import com.mab.buwisbuddyph.GuideActivity
import com.mab.buwisbuddyph.ProfileActivity
import com.mab.buwisbuddyph.R
import com.mab.buwisbuddyph.SignInActivity
import com.mab.buwisbuddyph.TaxCalculatorActivity
import com.mab.buwisbuddyph.accountant.AccountantHelpActivity
import com.mab.buwisbuddyph.calendar.CalendarFragment
import com.mab.buwisbuddyph.forum.ForumFragment
import com.mab.buwisbuddyph.messages.MessagesFragment
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var auth: FirebaseAuth

    private lateinit var paymentsClient: PaymentsClient

    private val db = FirebaseFirestore.getInstance()
    private lateinit var storageRef: StorageReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth = FirebaseAuth.getInstance()
        storageRef = FirebaseStorage.getInstance().reference

        paymentsClient = createPaymentsClient()

        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        updateProfileInfo()


        val userProfileImage = findViewById<ImageView>(R.id.userProfileImage)
        userProfileImage.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        val calendarIcon = findViewById<ImageView>(R.id.calendarIcon)
        calendarIcon.setOnClickListener {
            Log.d("calendar", "calendar icon clicked")
            toCalendar()
        }

        val forumIcon = findViewById<ImageView>(R.id.forumIcon)
        forumIcon.setOnClickListener {
            Log.d("forum", "forum icon clicked")
            toForum()
        }

        val homeIcon = findViewById<ImageView>(R.id.homeIcon)
        homeIcon.setOnClickListener {
            toHome()
        }


        val messageIcon = findViewById<ImageView>(R.id.messagesIcon)
        messageIcon.setOnClickListener {
            toMessages()
        }

        val options = GmsDocumentScannerOptions.Builder()
            .setScannerMode(SCANNER_MODE_FULL)
            .setGalleryImportAllowed(true)
            .setResultFormats(RESULT_FORMAT_JPEG, RESULT_FORMAT_PDF)
            .build()

        val scanner = GmsDocumentScanning.getClient(options)

        val scannerLauncher = registerForActivityResult(StartIntentSenderForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val scanningResult = GmsDocumentScanningResult.fromActivityResultIntent(result.data)
                if (scanningResult != null) {
                    saveScannedDocumentToFirebase(scanningResult)
                    savePdfLocally(scanningResult)
                }
            }
        }
        val cameraIcon = findViewById<ImageView>(R.id.cameraIcon)
        cameraIcon.setOnClickListener {
            scanner.getStartScanIntent(this@HomeActivity)
                .addOnSuccessListener { intentSender ->
                    scannerLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
                }
                .addOnFailureListener { exception ->
                    Log.e("HomeActivity", "Error starting scanner", exception)
                }
        }
    }

    private fun toCalendar() {
        val calendarFragment = CalendarFragment()
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frameLayout, calendarFragment)
            commit()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_profile -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }

            R.id.nav_create_budget -> {
                // Handle create budget navigation
            }

            R.id.nav_professional_help -> {
                val intent = Intent(this, AccountantHelpActivity::class.java)
                startActivity(intent)
            }

            R.id.nav_tax_calculator -> {
                val intent = Intent(this, TaxCalculatorActivity::class.java)
                startActivity(intent)
            }

            R.id.nav_document_list -> {
                val intent = Intent(this, DocumentListActivity::class.java)
                startActivity(intent)
            }

            R.id.nav_purchase -> {
                // Handle purchase navigation
            }

            R.id.nav_guides -> {
                val intent = Intent(this, GuideActivity::class.java)
                startActivity(intent)
            }

            R.id.nav_settings -> {
                // Handle settings navigation
            }

            R.id.nav_logout -> {
                showLogoutConfirmationDialog()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun toForum() {
        val forumFragment = ForumFragment()
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frameLayout, forumFragment)
            commit()
        }
    }

    private fun toHome() {
        val homeFragment = HomeFragment()
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frameLayout, homeFragment)
            commit()
        }
    }

    private fun toMessages() {
        val messagesFragment = MessagesFragment()
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frameLayout, messagesFragment)
            commit()
        }
    }

    private fun updateProfileInfo() {
        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid
            val db = FirebaseFirestore.getInstance()
            val userDocRef = db.collection("users").document(userId)

            userDocRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val img = document.getString("userProfileImage")
                        val name = document.getString("userFullName")
                        val email = document.getString("userEmail")
                        val navigationView = findViewById<NavigationView>(R.id.navigation_view)
                        val headerView = navigationView.getHeaderView(0)
                        val profImageView =
                            headerView.findViewById<ImageView>(R.id.userProfileImage)
                        val nameTextView = headerView.findViewById<TextView>(R.id.user_name)
                        val emailTextView = headerView.findViewById<TextView>(R.id.user_email)

                        if (!img.isNullOrEmpty()) {
                            Glide.with(this).load(img).into(profImageView)
                        } else {
                            profImageView.setImageResource(R.drawable.default_profile_img)
                        }
                        nameTextView.text = name
                        emailTextView.text = email
                    } else {
                        Log.d("HomeActivity", "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("HomeActivity", "get failed with ", exception)
                }
        }
    }

    private fun createPaymentsClient(): PaymentsClient {
        val walletOptions = Wallet.WalletOptions.Builder()
            .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
            .build()
        return Wallet.getPaymentsClient(this, walletOptions)
    }

    private fun isGooglePayAvailable() {
        val isReadyToPayRequestJson =
            JSONObject()
                .put("apiVersion", 2)
                .put("apiVersionMinor", 0)
                .put(
                    "allowedPaymentMethods", JSONArray().put(
                        JSONObject()
                            .put("type", "CARD")
                            .put(
                                "parameters", JSONObject()
                                    .put(
                                        "allowedAuthMethods",
                                        JSONArray().put("PAN_ONLY").put("CRYPTOGRAM_3DS")
                                    )
                                    .put(
                                        "allowedCardNetworks",
                                        JSONArray().put("AMEX").put("DISCOVER").put("JCB")
                                            .put("MASTERCARD").put("VISA")
                                    )
                            )
                    )
                )

        val isReadyToPayRequest = IsReadyToPayRequest.fromJson(isReadyToPayRequestJson.toString())
        val task: Task<Boolean> = paymentsClient.isReadyToPay(isReadyToPayRequest)
        task.addOnCompleteListener { completedTask ->
            if (completedTask.isSuccessful) {
                val isAvailable = completedTask.result == true
                Log.d("GooglePay", "Google Pay is available: $isAvailable")
            } else {
                Log.w("GooglePay", "Google Pay is not available.")
            }
        }
    }

    private fun saveScannedDocumentToFirebase(scanningResult: GmsDocumentScanningResult) {
        scanningResult.pages?.forEachIndexed { index, page ->
            val imageUri = page.imageUri
            uploadImageToFirebaseStorage(imageUri, index)
        }
    }

    private fun uploadImageToFirebaseStorage(uri: Uri, index: Int) {
        val imageRef = storageRef.child("image_${UUID.randomUUID()}.jpg")
        val uploadTask = imageRef.putFile(uri)

        uploadTask.addOnSuccessListener { taskSnapshot ->
            Log.d("HomeActivity", "Image uploaded successfully")
            imageRef.downloadUrl.addOnSuccessListener { imageUrl ->

                val userId = auth.currentUser?.uid
                if (userId != null) {
                    val documentId =
                        db.collection("users").document().id // Generate a unique document ID
                    saveImageToDatabase(documentId, imageUrl.toString())
                }
            }
                .addOnFailureListener { e ->
                    Log.e("HomeActivity", "Error uploading image to Firebase Storage", e)
                }
        }
    }

    private fun saveImageToDatabase(documentId: String, imageUrl: String) {
        val userId = auth.currentUser?.uid ?: return
        val timestamp = System.currentTimeMillis()

        val documentData = hashMapOf(
            "documentID" to documentId,
            "timestamp" to timestamp,
            "documentImgLink" to imageUrl
        )

        db.collection("users").document(userId).collection("userDocuments").document(documentId)
            .set(documentData)
            .addOnSuccessListener {
                Log.d("HomeActivity", "Document successfully added to Firestore")
            }
            .addOnFailureListener { e ->
                Log.e("HomeActivity", "Error adding document to Firestore", e)
            }
    }

    private fun savePdfLocally(scanningResult: GmsDocumentScanningResult) {
        scanningResult.pdf?.let { pdf ->
            val pdfUri = pdf.uri
            val pageCount = pdf.pageCount
            val inputStream: InputStream? = contentResolver.openInputStream(pdfUri)
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "scanned_document.pdf")
                // Adjust MIME type if needed
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "Documents/Scanned Documents")
                }
            }
            val newPdfUri =
                contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
            val outputStream: OutputStream? =
                newPdfUri?.let { contentResolver.openOutputStream(it) }
            if (inputStream != null && outputStream != null) {
                inputStream.copyTo(outputStream)
                inputStream.close()
                outputStream.close()
            }
        }
    }

    private fun requestPayment() {
        val paymentDataRequestJson = JSONObject()
            .put("apiVersion", 2)
            .put("apiVersionMinor", 0)
            .put(
                "allowedPaymentMethods", JSONArray().put(
                    JSONObject()
                        .put("type", "CARD")
                        .put(
                            "parameters", JSONObject()
                                .put(
                                    "allowedAuthMethods",
                                    JSONArray().put("PAN_ONLY").put("CRYPTOGRAM_3DS")
                                )
                                .put(
                                    "allowedCardNetworks",
                                    JSONArray().put("AMEX").put("DISCOVER").put("JCB")
                                        .put("MASTERCARD").put("VISA")
                                )
                        )
                        .put(
                            "tokenizationSpecification", JSONObject()
                                .put("type", "PAYMENT_GATEWAY")
                                .put(
                                    "parameters", JSONObject()
                                        .put("gateway", "acceptblue")
                                        .put("gatewayMerchantId", "BCR2DN4TS7JZJLD2")
                                )
                        )
                )
            )
            .put(
                "transactionInfo", JSONObject()
                    .put("totalPriceStatus", "FINAL")
                    .put("totalPrice", "10.00")
                    .put("currencyCode", "USD")
            )
            .put(
                "merchantInfo", JSONObject()
                    .put("merchantName", "BuwisBuddyPH")
            )

        val paymentDataRequest = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())
        val task: Task<PaymentData> = paymentsClient.loadPaymentData(paymentDataRequest)
        task.addOnCompleteListener { completedTask ->
            if (completedTask.isSuccessful) {
                val paymentData = completedTask.result
                Log.d("GooglePay", "PaymentData: ${paymentData?.toJson()}")
            } else {
                Log.w("GooglePay", "Payment failed.")
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }




    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to log out?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            dialog.dismiss()
            performLogout()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun performLogout() {
        FirebaseAuth.getInstance().signOut()

        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        val intent = Intent(this, SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
