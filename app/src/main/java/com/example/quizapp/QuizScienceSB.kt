package com.example.quizapp

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.widget.Toast
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.databinding.QuizScience2Binding

class QuizScienceSB : AppCompatActivity() {
    private lateinit var binding: QuizScience2Binding

    private val questions = arrayOf("B _ E",
        "H E A _",
        "G _ L D",
        "S O I _",
        "W A _ E R")

    private val options = arrayOf(arrayOf("R", "E", "L"),
        arrayOf("H", "S", "D"),
        arrayOf("E", "O", "S"),
        arrayOf("O", "P", "L"),
        arrayOf("T", "I", "O"))

    private val photos = arrayOf(R.drawable.bee, R.drawable.head, R.drawable.gold, R.drawable.soil, R.drawable.water)
    private val correctAnswers = arrayOf(1, 2, 1, 2, 0)


    private var currentQuestionIndex = 0
    private var score = 0

    private var previousQuestionsCompleted = 0

    private var timer: CountDownTimer? = null
    private var totalElapsedTime: Long = 0 // Total time from previous quiz + current quiz
    private var isTimerRunning = false
    private var startTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = QuizScience2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        score = intent.getIntExtra("CURRENT_SCORE", 0)
        totalElapsedTime = intent.getLongExtra("TOTAL_TIME", 0L)
        previousQuestionsCompleted = intent.getIntExtra("QUESTIONS_COMPLETED", 0)

        totalQuestion()

        displayQuestion()

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

    private fun startContinuousStopwatch() {
        // Start from the elapsed time passed from previous quiz
        startTime = SystemClock.elapsedRealtime() - totalElapsedTime

        // Cancel any existing timer
        timer?.cancel()

        // Create and start a continuous timer
        timer = object : CountDownTimer(Long.MAX_VALUE, 10) {
            override fun onTick(millisUntilFinished: Long) {
                totalElapsedTime = SystemClock.elapsedRealtime() - startTime
                updateTimerText()
            }

            override fun onFinish() {
                // This won't be called
            }
        }.start()

        isTimerRunning = true
    }

    private fun updateTimerText() {
        val totalSeconds = (totalElapsedTime / 1000).toInt()
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        val milliseconds = (totalElapsedTime % 1000).toInt()

        binding.timerText.text = String.format("%d:%02d", minutes, seconds)
    }

    private fun pauseStopwatch() {
        timer?.cancel()
        isTimerRunning = false
    }

    private fun resumeStopwatch() {
        if (!isTimerRunning) {
            startTime = SystemClock.elapsedRealtime() - totalElapsedTime

            timer = object : CountDownTimer(Long.MAX_VALUE, 10) {
                override fun onTick(millisUntilFinished: Long) {
                    totalElapsedTime = SystemClock.elapsedRealtime() - startTime
                    updateTimerText()
                }

                override fun onFinish() {
                    // This won't be called
                }
            }.start()

            isTimerRunning = true
        }
    }

    private fun stopStopwatch() {
        timer?.cancel()
        isTimerRunning = false
        totalElapsedTime = SystemClock.elapsedRealtime() - startTime
    }

    private fun totalQuestion() {
        // Current question number = completed from previous quiz + current index + 1
        val currentNumber = previousQuestionsCompleted + currentQuestionIndex + 1

        // Total questions = previous quiz questions + current quiz questions
        val totalNumber = previousQuestionsCompleted + questions.size

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
        resumeStopwatch()

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

            stopStopwatch()
            showResults()
        }
    }

    // score after game
    private fun showResults() {

        val totalQuestionCount = questions.size + previousQuestionsCompleted

        // send data to score after game page
        val intent = Intent(this, ScoreAfterGame::class.java).apply {
            putExtra(ScoreAfterGame.EXTRA_SCORE, score)  // Pass the current score
            putExtra(ScoreAfterGame.EXTRA_TOTAL_QUESTIONS, totalQuestionCount)
            putExtra(ScoreAfterGame.EXTRA_TOTAL_TIME, totalElapsedTime)
            putExtra(ScoreAfterGame.EXTRA_QUIZ_TYPE, "Science")

            // next button ( will bring to homepage )
            putExtra(ScoreAfterGame.EXTRA_PLAY_AGAIN_ACTIVITY, QuizScienceSB::class.java.name)
            putExtra(ScoreAfterGame.EXTRA_NEXT_GAME_ACTIVITY, QuizMaths::class.java.name)
        }

        startActivity(intent)
        finish()
    }


    private fun setOptionButtonsEnabled(enabled: Boolean) {
        binding.option1Button.isEnabled = enabled
        binding.option2Button.isEnabled = enabled
        binding.option3Button.isEnabled = enabled
    }
}
