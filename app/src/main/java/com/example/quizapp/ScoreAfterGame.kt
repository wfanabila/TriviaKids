package com.example.quizapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.databinding.ScoreAfterGameBinding

class ScoreAfterGame : AppCompatActivity() {

    private lateinit var binding: ScoreAfterGameBinding

    // pass score dari game, test test ~~
    private var score: String = "90/100"
    private var scoreNumber: Int = 90 // total score, test test ~~

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ScoreAfterGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textScore.text = "You Scored"
        binding.scoreText.text = score

        binding.playAgain.setOnClickListener {
            Toast.makeText(this, "Restarting Game", Toast.LENGTH_SHORT).show()
        }

        binding.save.setOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra("newScore", scoreNumber)

            setResult(RESULT_OK, resultIntent)
            finish()
        }

        binding.closeButton.setOnClickListener {
            finish()
        }
    }
}
