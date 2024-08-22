package com.bangkas.version2

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Distance : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")

    lateinit var distCurrLocBtn: TextView
    lateinit var distPinLocBtn: TextView
    lateinit var confirmDestinationBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_distance)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        distCurrLocBtn = findViewById(R.id.distCurrLocBtn)
        distPinLocBtn = findViewById(R.id.distPinLocBtn)
        confirmDestinationBtn = findViewById(R.id.distConfirmBtn)

        val intent = intent
        val currentLocation = intent.getStringExtra("currentLocation")
        val destination = intent.getStringExtra("destination")

        distCurrLocBtn.text = currentLocation
        distPinLocBtn.text = destination

        val back = findViewById<ImageButton>(R.id.distBckBtn)
        back.setOnClickListener {
            val intent = Intent(this, SelectDestination::class.java)
            startActivity(intent)
        }

        confirmDestinationBtn.setOnClickListener {
            val intent = Intent(this, BangkaSelect::class.java).apply {
                putExtra("currentLocation", currentLocation)
                putExtra("destination", destination)
            }
            val options = ActivityOptions.makeCustomAnimation(this, 0, 0)
            startActivity(intent, options.toBundle())
        }
    }
}
