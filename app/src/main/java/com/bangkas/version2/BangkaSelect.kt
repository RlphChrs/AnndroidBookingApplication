package com.bangkas.version2

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class BangkaSelect : AppCompatActivity() {

    private lateinit var currentLocation: String
    private lateinit var destination: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_bangka_select)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val intent = intent
        currentLocation = intent.getStringExtra("currentLocation") ?: "Unknown"
        destination = intent.getStringExtra("destination") ?: "Unknown"

        val back = findViewById<ImageButton>(R.id.bangkaBckBtn)
        val confirm = findViewById<Button>(R.id.bangkaSelConfirm)

        back.setOnClickListener {
            val intent = Intent(this, Distance::class.java)
            val options = ActivityOptions.makeCustomAnimation(this, 0, 0)
            startActivity(intent, options.toBundle())
        }

        confirm.setOnClickListener {
            val selectedVessel = getSelectedVessel()
            val charge = getVesselCharge(selectedVessel)
            val intent = Intent(this, PaymentMethod::class.java).apply {
                putExtra("currentLocation", currentLocation)
                putExtra("destination", destination)
                putExtra("SELECTED_VESSEL", selectedVessel)
                putExtra("CHARGE", charge)
            }
            val options = ActivityOptions.makeCustomAnimation(this, 0, 0)
            startActivity(intent, options.toBundle())
        }
    }

    private fun getSelectedVessel(): String {
        val vslOneBox = findViewById<CheckBox>(R.id.vslOneBox)
        val vslTwoBox = findViewById<CheckBox>(R.id.vslTwoBox)
        val vslThreeBox = findViewById<CheckBox>(R.id.vslThreeBox)
        val vslFourBox = findViewById<CheckBox>(R.id.vslFourBox)

        return when {
            vslOneBox.isChecked -> "CGM Palais Royal"
            vslTwoBox.isChecked -> "Ever Aria"
            vslThreeBox.isChecked -> "PB Acosta"
            vslFourBox.isChecked -> "PB Delgado"
            else -> "No Vessel Selected"
        }
    }

    private fun getVesselCharge(vessel: String): Int {
        return when (vessel) {
            "CGM Palais Royal" -> 500
            "Ever Aria" -> 400
            "PB Acosta" -> 700
            "PB Delgado" -> 500
            else -> 0
        }
    }
}
