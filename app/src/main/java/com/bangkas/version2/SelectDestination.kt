package com.bangkas.version2

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SelectDestination : AppCompatActivity() {

    lateinit var currLocBtn: EditText
    lateinit var pinLocBtn: EditText
    lateinit var submitDesBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_select_destination)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val back = findViewById<ImageButton>(R.id.selDistBckBtn)
        currLocBtn = findViewById(R.id.currLocBtn)
        pinLocBtn = findViewById(R.id.pinLocBtn)
        submitDesBtn = findViewById(R.id.submitDes)

        submitDesBtn.setOnClickListener {
            val currentLocation = currLocBtn.text.toString()
            val destination = pinLocBtn.text.toString()

            // Start DestinationConfirmation Activity and pass data
            val intent = Intent(this, Distance::class.java)
            intent.putExtra("currentLocation", currentLocation)
            intent.putExtra("destination", destination)
            startActivity(intent)
        }

        back.setOnClickListener {
            val intent = Intent(this, Dashboard::class.java)
            startActivity(intent)
            val options = ActivityOptions.makeCustomAnimation(this, 0, 0)
            startActivity(intent, options.toBundle())
        }

    }
}