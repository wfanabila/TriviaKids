package com.example.quizapp

import android.R
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.databinding.HomepageBinding

class HomePage : AppCompatActivity() {
    private lateinit var binding: HomepageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomepageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button1.setOnClickListener {
            // Create an Intent to navigate to QuizEnglish activity
            val intent = Intent(this, QuizEnglish::class.java)
            startActivity(intent)
        }

        // Optional: If you want to add click listeners for other buttons too
        binding.button2.setOnClickListener {
            // Navigate to Science activity (create this class if needed)
            // val intent = Intent(this, QuizScience::class.java)
            // startActivity(intent)
        }

        binding.button3.setOnClickListener {
            // Navigate to Mathematics activity (create this class if needed)
            // val intent = Intent(this, QuizMathematics::class.java)
            // startActivity(intent)
        }

    }
}