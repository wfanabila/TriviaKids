package com.example.quizapp

import android.R
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


        val bottomNav: BottomNavigationView = findViewById(com.example.quizapp.R.id.bottom_nav) // Use the correct package reference
        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                com.example.quizapp.R.id.home -> {
                    val intent = Intent(this, HomePage::class.java)
                    startActivity(intent)
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