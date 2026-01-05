package com.example.quizapp

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.databinding.QuizEnglish3Binding

class QuizScienceSB : AppCompatActivity() {
    private lateinit var binding: QuizEnglish3Binding

    // Data mapped from your screenshots
    private val words = arrayOf("BEE", "HEAD", "SOIL", "WATER")
    private val maskedWords = arrayOf("B _ E", "H E A _", "S O I _", "WA _ ER")
    private val options = arrayOf(
        arrayOf("R", "L", "E"),
        arrayOf("H", "D", "S"),
        arrayOf("O", "P", "L"),
        arrayOf("T", "I", "O")
    )
    private val photos = arrayOf(R.drawable.bee, R.drawable.head, R.drawable.soil, R.drawable.water)
    private val correctAnswers = arrayOf(2, 1, 2, 0) // Indices of correct options

    private var currentIdx = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = QuizEnglish3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        displayQuestion()

        binding.option1Button.setOnClickListener { checkAnswer(0) }
        binding.option2Button.setOnClickListener { checkAnswer(1) }
        binding.option3Button.setOnClickListener { checkAnswer(2) }
        binding.closeButton.setOnClickListener { finish() }
    }

    private fun displayQuestion() {
        binding.totalQuestion.text = "${currentIdx + 7} / 10" // Matching your 7/10 screenshot start
        binding.questionText.text = maskedWords[currentIdx]
        binding.imageView.setImageResource(photos[currentIdx])

        binding.option1Button.text = options[currentIdx][0]
        binding.option2Button.text = options[currentIdx][1]
        binding.option3Button.text = options[currentIdx][2]

        resetButtonColors()
        setButtonsEnabled(true)
    }

    private fun checkAnswer(selectedIdx: Int) {
        if (selectedIdx == correctAnswers[currentIdx]) {
            // CORRECT: Green, Reveal, and Advance
            setButtonsEnabled(false)
            updateButtonColor(selectedIdx, "#61E547") // Green
            binding.questionText.text = words[currentIdx].replace("", " ").trim()

            Handler(Looper.getMainLooper()).postDelayed({
                if (currentIdx < words.size - 1) {
                    currentIdx++
                    displayQuestion()
                }
            }, 1000)
        } else {
            // WRONG: Just turn this button red (don't advance)
            updateButtonColor(selectedIdx, "#FF4A4C") // Red
        }
    }

    private fun updateButtonColor(btnIdx: Int, colorHex: String) {
        val tint = ColorStateList.valueOf(Color.parseColor(colorHex))
        when(btnIdx) {
            0 -> binding.option1Button.backgroundTintList = tint
            1 -> binding.option2Button.backgroundTintList = tint
            2 -> binding.option3Button.backgroundTintList = tint
        }
    }

    private fun resetButtonColors() {
        val defaultTint = ColorStateList.valueOf(Color.parseColor("#AEB8FE"))
        binding.option1Button.backgroundTintList = defaultTint
        binding.option2Button.backgroundTintList = defaultTint
        binding.option3Button.backgroundTintList = defaultTint
    }

    private fun setButtonsEnabled(enabled: Boolean) {
        binding.option1Button.isEnabled = enabled
        binding.option2Button.isEnabled = enabled
        binding.option3Button.isEnabled = enabled
    }
}