package com.bangkas.version2

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class TripConfirmation : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_trip_confirmation)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val home = findViewById<ImageButton>(R.id.homeBtn)
        val history = findViewById<ImageButton>(R.id.historyBtn)
        val notif = findViewById<ImageButton>(R.id.notifBtn)
        val user = findViewById<ImageButton>(R.id.userProfBtn)
        val confirm = findViewById<Button>(R.id.arriveOk)

        confirm.setOnClickListener {
            val intent = Intent(this, Dashboard::class.java)
            startActivity(intent)
            val options = ActivityOptions.makeCustomAnimation(this, 0, 0)
            startActivity(intent, options.toBundle())
        }

        home.setOnClickListener {
            val intent = Intent(this, Dashboard::class.java)
            startActivity(intent)
            val options = ActivityOptions.makeCustomAnimation(this, 0, 0)
            startActivity(intent, options.toBundle())
        }
        history.setOnClickListener {
            val intent = Intent(this, HistoryFragment::class.java)
            startActivity(intent)
            val options = ActivityOptions.makeCustomAnimation(this, 0, 0)
            startActivity(intent, options.toBundle())
        }
        notif.setOnClickListener {
            val intent = Intent(this, NoficationFragment::class.java)
            startActivity(intent)
            val options = ActivityOptions.makeCustomAnimation(this, 0, 0)
            startActivity(intent, options.toBundle())
        }
        user.setOnClickListener {
            val intent = Intent(this, UserProfileFragment::class.java)
            startActivity(intent)
            val options = ActivityOptions.makeCustomAnimation(this, 0, 0)
            startActivity(intent, options.toBundle())
        }
    }
}