package com.example.quizapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.databinding.ScoreAfterGameBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlin.math.roundToInt

class ScoreAfterGame : AppCompatActivity() {

    private lateinit var binding: ScoreAfterGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ScoreAfterGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomNav: BottomNavigationView = findViewById(com.example.quizapp.R.id.bottom_nav)
        bottomNav.selectedItemId = com.example.quizapp.R.id.home

        // read data from quiz
        val score = intent.getIntExtra(EXTRA_SCORE, 0)
        val total = intent.getIntExtra(EXTRA_TOTAL_QUESTIONS, 0)
        val totalTime = intent.getLongExtra(EXTRA_TOTAL_TIME, 0L)
        val quizType = intent.getStringExtra(EXTRA_QUIZ_TYPE)

        // display score
        binding.scoreText.text = "$score / $total"

        binding.closeButton.setOnClickListener { finish() }

        // play again based on prev subject
        binding.playAgain.setOnClickListener {
            when (quizType) {
                "English" -> launchActivity(QuizEnglish::class.java)
                "Maths" -> launchActivity(QuizMaths::class.java)
//                "Science" -> launchActivity(QuizScience::class.java)
                else -> Toast.makeText(this, "Invalid quiz type", Toast.LENGTH_SHORT).show()
            }
        }

        // navigate to homepage ( next button )
        binding.save.setOnClickListener {
            launchActivity(HomePage::class.java)
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

    private fun launchActivity(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
        finish()
    }

    companion object {
        const val EXTRA_SCORE = "EXTRA_SCORE"
        const val EXTRA_TOTAL_QUESTIONS = "EXTRA_TOTAL_QUESTIONS"
        const val EXTRA_TOTAL_TIME = "EXTRA_TOTAL_TIME"
        const val EXTRA_PLAY_AGAIN_ACTIVITY = "EXTRA_PLAY_AGAIN_ACTIVITY"
        const val EXTRA_NEXT_GAME_ACTIVITY = "EXTRA_NEXT_GAME_ACTIVITY"
        const val EXTRA_QUIZ_TYPE = "EXTRA_QUIZ_TYPE"
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
