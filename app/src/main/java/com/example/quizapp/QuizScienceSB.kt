package com.example.quizapp

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class QuizScienceSB : AppCompatActivity() {

    // Views
    private lateinit var totalQuestion: TextView
    private lateinit var questionText: TextView
    private lateinit var imageView: ImageView
    private lateinit var option1Button: Button
    private lateinit var option2Button: Button
    private lateinit var option3Button: Button
    private lateinit var closeButton: ImageButton

    // Data
    private val words = arrayOf("BEE", "HEAD", "SOIL", "WATER")
    private val maskedWords = arrayOf("B _ E", "H E A _", "S O I _", "WA _ ER")
    private val options = arrayOf(
        arrayOf("R", "L", "E"),
        arrayOf("H", "D", "S"),
        arrayOf("O", "P", "L"),
        arrayOf("T", "I", "O")
    )
    private val photos = arrayOf(
        R.drawable.bee,
        R.drawable.head,
        R.drawable.soil,
        R.drawable.water
    )
    private val correctAnswers = arrayOf(2, 1, 2, 0)

    private var currentIdx = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.quiz_science_1)

        // Init views
        totalQuestion = findViewById(R.id.totalQuestion)
        questionText = findViewById(R.id.questionText)
        imageView = findViewById(R.id.imageView)
        option1Button = findViewById(R.id.option1Button)
        option2Button = findViewById(R.id.option2Button)
        option3Button = findViewById(R.id.option3Button)
        closeButton = findViewById(R.id.closeButton)

        displayQuestion()

        option1Button.setOnClickListener { checkAnswer(0) }
        option2Button.setOnClickListener { checkAnswer(1) }
        option3Button.setOnClickListener { checkAnswer(2) }
        closeButton.setOnClickListener { finish() }
    }

    private fun displayQuestion() {
        totalQuestion.text = "${currentIdx + 7} / 10"
        questionText.text = maskedWords[currentIdx]
        imageView.setImageResource(photos[currentIdx])

        option1Button.text = options[currentIdx][0]
        option2Button.text = options[currentIdx][1]
        option3Button.text = options[currentIdx][2]

        resetButtonColors()
        setButtonsEnabled(true)
    }

    private fun checkAnswer(selectedIdx: Int) {
        if (selectedIdx == correctAnswers[currentIdx]) {
            setButtonsEnabled(false)
            updateButtonColor(selectedIdx, "#61E547")

            questionText.text = words[currentIdx].replace("", " ").trim()

            Handler(Looper.getMainLooper()).postDelayed({
                if (currentIdx < words.size - 1) {
                    currentIdx++
                    displayQuestion()
                }
            }, 1000)
        } else {
            updateButtonColor(selectedIdx, "#FF4A4C")
        }
    }

    private fun updateButtonColor(btnIdx: Int, colorHex: String) {
        val tint = ColorStateList.valueOf(Color.parseColor(colorHex))
        when (btnIdx) {
            0 -> option1Button.backgroundTintList = tint
            1 -> option2Button.backgroundTintList = tint
            2 -> option3Button.backgroundTintList = tint
        }
    }

    private fun resetButtonColors() {
        val defaultTint = ColorStateList.valueOf(Color.parseColor("#AEB8FE"))
        option1Button.backgroundTintList = defaultTint
        option2Button.backgroundTintList = defaultTint
        option3Button.backgroundTintList = defaultTint
    }

    private fun setButtonsEnabled(enabled: Boolean) {
        option1Button.isEnabled = enabled
        option2Button.isEnabled = enabled
        option3Button.isEnabled = enabled
    }
}
