package com.example.quizapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.databinding.ScoreAfterGameBinding

class ScoreAfterGame : AppCompatActivity() {

    private lateinit var binding: ScoreAfterGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ScoreAfterGameBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.profileIcon.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.scoreIcon.setOnClickListener {
            val intent = Intent(this, ScoreAfterGame::class.java)
            startActivity(intent)
        }
    }
}
