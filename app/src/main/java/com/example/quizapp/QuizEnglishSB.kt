package com.example.quizapp

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.databinding.QuizEnglish2Binding

class QuizEnglishSB : AppCompatActivity() {
    private lateinit var binding: QuizEnglish2Binding

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

    private var previousQuestionsCompleted = 0

    private var timer: CountDownTimer? = null
    private var totalElapsedTime: Long = 0 // Total time from previous quiz + current quiz
    private var isTimerRunning = false
    private var startTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = QuizEnglish2Binding.inflate(layoutInflater)
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

    private fun showResults() {
        val totalQuestionCount = questions.size + 3
        Toast.makeText(this, "Quiz Finished! Your score: $score / $totalQuestionCount", Toast.LENGTH_LONG).show()
    }

    private fun setOptionButtonsEnabled(enabled: Boolean) {
        binding.option1Button.isEnabled = enabled
        binding.option2Button.isEnabled = enabled
        binding.option3Button.isEnabled = enabled
    }
}
