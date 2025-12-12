package com.example.quizapp

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.databinding.QuizEnglish1Binding

class QuizEnglish : AppCompatActivity() {
    private lateinit var binding: QuizEnglish1Binding

    private val questions = arrayOf("Hello! I am a _______",
        "Guess my occupation!",
        "What job do you think I am?",)

    private val options = arrayOf(arrayOf("Gardener", "Postman", "Fireman", "Policeman"),
        arrayOf("Policeman", "Fireman", "Doctor", "Gardener"),
        arrayOf("Gardener", "Doctor", "Policeman", "Postman"))

    private val photos = arrayOf(R.drawable.fireman, R.drawable.policeman, R.drawable.doctor)
    private val correctAnswers = arrayOf(2, 0, 1)

    private var currentQuestionIndex = 0
    private var score = 0

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = QuizEnglish1Binding.inflate(layoutInflater)
        setContentView(binding.root)

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
        binding.option4Button.setOnClickListener {
            checkAnswer(3)
        }
    }

    private fun totalQuestion() {
        val currentNumber = currentQuestionIndex + 1
        val totalNumber = questions.size
        binding.totalQuestion.text = "$currentNumber / $totalNumber"
    }


    private fun correctButtonColors(buttonIndex: Int) {
        val greenTint = ColorStateList.valueOf(Color.parseColor("#61E547"))
        when(buttonIndex) {
            0 -> binding.option1Button.backgroundTintList = greenTint
            1 -> binding.option2Button.backgroundTintList = greenTint
            2 -> binding.option3Button.backgroundTintList = greenTint
            3 -> binding.option4Button.backgroundTintList = greenTint
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
                    3 -> binding.option4Button.backgroundTintList = redTint
                }
            }
        }
    }

    private fun resetButtonColors() {
        val defaultTint = ColorStateList.valueOf(Color.parseColor("#AEB8FE"))
        binding.option1Button.backgroundTintList = defaultTint
        binding.option2Button.backgroundTintList = defaultTint
        binding.option3Button.backgroundTintList = defaultTint
        binding.option4Button.backgroundTintList = defaultTint
    }

    private fun displayQuestion() {
        totalQuestion()

        binding.questionText.text = questions[currentQuestionIndex]
        binding.option1Button.text = options[currentQuestionIndex][0]
        binding.option2Button.text = options[currentQuestionIndex][1]
        binding.option3Button.text = options[currentQuestionIndex][2]
        binding.option4Button.text = options[currentQuestionIndex][3]

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
            val intent = Intent(this, QuizEnglishSB::class.java)
            intent.putExtra("CURRENT_SCORE", score)
            startActivity(intent)
            finish()
        }
    }

    private fun showResults() {
        Toast.makeText(this, "Quiz Finished! Your score: $score / ${questions.size}", Toast.LENGTH_LONG).show()
    }

    private fun setOptionButtonsEnabled(enabled: Boolean) {
        binding.option1Button.isEnabled = enabled
        binding.option2Button.isEnabled = enabled
        binding.option3Button.isEnabled = enabled
        binding.option4Button.isEnabled = enabled
    }
}
