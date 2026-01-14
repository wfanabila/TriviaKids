package com.example.quizapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.databinding.HomepageBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomePage : AppCompatActivity() {
    private lateinit var binding: HomepageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomepageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // check if the user has logged in by checking their profile data
        val name = ProfilePrefs.getName(this)
        val email = ProfilePrefs.getEmail(this)

        // if no valid data exists, redirect the user to the LoginPage
        if (name == "Leehan" || email == "defaultEmail@example.com") {
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
            finish()  // Close the HomePage after redirecting to LoginPage
        }

        if (!ProfilePrefs.isLoggedIn(this)) {
            startActivity(Intent(this, LoginPage::class.java))
            finish()
            return
        }

        // continue with the HomePage setup if the user is logged in
        setContentView(R.layout.homepage)


        val bottomNav: BottomNavigationView = findViewById(com.example.quizapp.R.id.bottom_nav)
        bottomNav.selectedItemId = com.example.quizapp.R.id.home

        binding.button1.setOnClickListener {
            val intent = Intent(this, QuizEnglish::class.java)
            startActivity(intent)
        }

        binding.button2.setOnClickListener {
             val intent = Intent(this, QuizScience::class.java)
             startActivity(intent)
        }

        binding.button3.setOnClickListener {
             val intent = Intent(this, QuizMaths::class.java)
             startActivity(intent)
        }

        // nav bar
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                com.example.quizapp.R.id.home -> {
                    true
                }
                com.example.quizapp.R.id.profile -> {
                    navigateToProfileActivity()
                    true
                }
                com.example.quizapp.R.id.setting -> {
                    navigateToEditProfile()
                    true
                }
                else -> false
            }
        }
    }

    private fun navigateToProfileActivity() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToEditProfile() {
        val intent = Intent(this, EditProfile::class.java)
        startActivity(intent)
    }
}