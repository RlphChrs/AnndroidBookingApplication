package com.bangkas.version2

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.google.api.ResourceDescriptor.History

class Dashboard : AppCompatActivity() {
    private var selectedImage: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        supportActionBar?.hide()

        val homeBtn = findViewById<ImageView>(R.id.homeBtn)
        homeBtn.setOnClickListener {
            navigateToFragment(HomeFragment())
            updateSelectedNavigation(homeBtn)
        }

        val notificationBtn = findViewById<ImageView>(R.id.notifBtn)
        notificationBtn.setOnClickListener {
            navigateToFragment(NoficationFragment())
            updateSelectedNavigation(notificationBtn)
        }

        val historyBtn = findViewById<ImageView>(R.id.historyBtn)
        historyBtn.setOnClickListener {
            navigateToFragment(HistoryFragment())
            updateSelectedNavigation(historyBtn)
        }

        val userProfileBtn = findViewById<ImageView>(R.id.userProfBtn)
        userProfileBtn.setOnClickListener {
            navigateToFragment(UserProfileFragment())
            updateSelectedNavigation(userProfileBtn)
        }

        if (savedInstanceState == null) {
            navigateToFragment(HomeFragment())
            updateSelectedNavigation(findViewById(R.id.homeBtn) as ImageView)
        }
    }

    private fun navigateToFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun updateSelectedNavigation(image: ImageView) {
        selectedImage?.isSelected = false // Deselect the previously selected item
        image.isSelected = true // Select the current item
        selectedImage = image // Update the currently selected image
    }
}