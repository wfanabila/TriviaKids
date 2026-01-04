package com.example.quizapp

import android.content.res.ColorStateList
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.databinding.QuizEnglish1Binding
import com.example.quizapp.databinding.QuizEnglish3Binding

class QuizEnglishSB : AppCompatActivity() {
    private lateinit var binding: QuizEnglish3Binding

    private val questions = arrayOf("_ P P L E",
        "P I N E A P P _ E",
        "G _ A P E",)

    private val options = arrayOf(arrayOf("P", "A", "C"),
        arrayOf("O", "S", "L"),
        arrayOf("B", "R", "F"))

    private val photos = arrayOf(R.drawable.apple, R.drawable.pineapple, R.drawable.grape)
    private val correctAnswers = arrayOf(1, 2, 1)

    private var currentQuestionIndex = 0
    private var score = 0

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = QuizEnglish3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        score = intent.getIntExtra("CURRENT_SCORE", 0)

        totalQuestion()

        mediaPlayer = MediaPlayer.create(this, R.raw.sound)
        mediaPlayer?.isLooping = true

        binding.soundonButton.setOnClickListener {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
            } else {
                mediaPlayer?.start()
            }
        }

        displayQuestion()

        binding.closeButton.setOnClickListener {
            mediaPlayer?.stop()
            finish()
        }

        binding.option1Button.setOnClickListener {
            checkAnswer(0)
        }
        binding.option2Button.setOnClickListener {
            checkAnswer(1)
        }
        binding.option3Button.setOnClickListener {
            checkAnswer(2)
        }
    }

    private fun totalQuestion() {
        val currentNumber = currentQuestionIndex + 4
        val totalNumber = questions.size + 3
        binding.totalQuestion.text = "$currentNumber / $totalNumber"
    }


    private fun correctButtonColors(buttonIndex: Int) {
        val greenTint = ColorStateList.valueOf(Color.parseColor("#61E547"))
        when(buttonIndex) {
            0 -> binding.option1Button.backgroundTintList = greenTint
            1 -> binding.option2Button.backgroundTintList = greenTint
            2 -> binding.option3Button.backgroundTintList = greenTint
        }
    }

    private fun wrongButtonColors(correctAnswerIndex: Int) {
        val redTint = ColorStateList.valueOf(Color.parseColor("#FF4A4C"))

        for (i in 0..3) {
            if (i != correctAnswerIndex) {
                when(i) {
                    0 -> binding.option1Button.backgroundTintList = redTint
                    1 -> binding.option2Button.backgroundTintList = redTint
                    2 -> binding.option3Button.backgroundTintList = redTint
                }
            }
        }
    }

    private fun resetButtonColors() {
        val defaultTint = ColorStateList.valueOf(Color.parseColor("#AEB8FE"))
        binding.option1Button.backgroundTintList = defaultTint
        binding.option2Button.backgroundTintList = defaultTint
        binding.option3Button.backgroundTintList = defaultTint
    }

    private fun displayQuestion() {
        totalQuestion()

        binding.questionText.text = questions[currentQuestionIndex]
        binding.option1Button.text = options[currentQuestionIndex][0]
        binding.option2Button.text = options[currentQuestionIndex][1]
        binding.option3Button.text = options[currentQuestionIndex][2]

        binding.imageView.setImageResource(photos[currentQuestionIndex])

        resetButtonColors()
        setOptionButtonsEnabled(true)
    }

    private fun checkAnswer(selectedAnswerIndex: Int) {
        setOptionButtonsEnabled(false)

        val correctAnswerIndex = correctAnswers[currentQuestionIndex]

        if (selectedAnswerIndex == correctAnswerIndex) {
            score++
            correctButtonColors(selectedAnswerIndex)
        } else {
            wrongButtonColors(correctAnswerIndex)
            correctButtonColors(correctAnswerIndex)
        }

        if (currentQuestionIndex < questions.size - 1) {
            currentQuestionIndex++
            binding.questionText.postDelayed({
                displayQuestion()
            }, 1000)
        } else {
            showResults()
        }
    }

    private fun showResults() {
        val totalQuestionCount = questions.size + 3
        Toast.makeText(this, "Quiz Finished! Your score: $score / ${questions.size}", Toast.LENGTH_LONG).show()
    }

    private fun setOptionButtonsEnabled(enabled: Boolean) {
        binding.option1Button.isEnabled = enabled
        binding.option2Button.isEnabled = enabled
        binding.option3Button.isEnabled = enabled
    }
}
