package com.bangkas.version2

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class Dashboard : AppCompatActivity() {
    private var selectedButton: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        supportActionBar?.hide()

        val homeBtn = findViewById<ImageButton>(R.id.homeBtn)
        homeBtn.setOnClickListener {
            navigateToFragment(HomeFragment())
            updateSelectedNavigation(homeBtn)
        }

        val notificationBtn = findViewById<ImageButton>(R.id.notifBtn)
        notificationBtn.setOnClickListener {
            navigateToFragment(NotifFragment())
            updateSelectedNavigation(notificationBtn)
        }

        val historyBtn = findViewById<ImageButton>(R.id.historyBtn)
        historyBtn.setOnClickListener {
            navigateToFragment(HistoryFragment())
            updateSelectedNavigation(historyBtn)
        }

        val userProfileBtn = findViewById<ImageButton>(R.id.userProfBtn)
        userProfileBtn.setOnClickListener {
            navigateToFragment(UserProfileFragment())
            updateSelectedNavigation(userProfileBtn)
        }

        if (savedInstanceState == null) {
            navigateToFragment(HomeFragment())
            updateSelectedNavigation(homeBtn)
        }
    }

    private fun navigateToFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun updateSelectedNavigation(button: ImageButton) {
        selectedButton?.isSelected = false // Deselect the previously selected item
        button.isSelected = true // Select the current item
        selectedButton = button // Update the currently selected button
    }
}
