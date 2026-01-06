package com.example.quizapp

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.databinding.QuizScience1Binding

class QuizScience : AppCompatActivity() {
    private lateinit var binding: QuizScience1Binding

    private val questions = arrayOf("This is a picture of?",
        "What body part shown below?",
        "What do you call this?",
        "Name this sense organ"
    )

    private val options = arrayOf(arrayOf("Eye", "Mouth", "Ear", "Hand"),
        arrayOf("Eye", "Mouth", "Ear", "Hand"),
        arrayOf("Eye", "Mouth", "Ear", "Hand"),
        arrayOf("Eye", "Mouth", "Ear", "Hand"))

    private val photos = arrayOf(R.drawable.ear, R.drawable.mouth, R.drawable.hand, R.drawable.eye)
    private val correctAnswers = arrayOf(2, 1, 3, 0)

    private var currentQuestionIndex = 0
    private var score = 0

    private var timer: CountDownTimer? = null
    private var totalElapsedTime: Long = 0 // Total time for the entire quiz
    private var isTimerRunning = false
    private var startTime: Long = 0
    private var currentQuestionStartTime: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = QuizScience1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        totalQuestion()

        binding.closeButton.setOnClickListener {
            val intent = Intent(this, HomePage::class.java)
            startActivity(intent)
        }

        // Start the continuous stopwatch
        startContinuousStopwatch()

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
        binding.option4Button.setOnClickListener {
            checkAnswer(3)
        }
    }

    private fun startContinuousStopwatch() {
        // Record the overall start time
        startTime = SystemClock.elapsedRealtime()

        // Cancel any existing timer
        timer?.cancel()

        // Create and start a continuous timer that updates every 10ms
        timer = object : CountDownTimer(Long.MAX_VALUE, 10) {
            override fun onTick(millisUntilFinished: Long) {
                totalElapsedTime = SystemClock.elapsedRealtime() - startTime
                updateTimerText()
            }

            override fun onFinish() {
                // This won't be called since we're using Long.MAX_VALUE
            }
        }.start()

        isTimerRunning = true
    }

    private fun updateTimerText() {
        // Calculate minutes, seconds, and milliseconds
        val totalSeconds = (totalElapsedTime / 1000).toInt()
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        val milliseconds = (totalElapsedTime % 1000).toInt()

        // Format: MM:SS (no milliseconds for cleaner display)
        binding.timerText.text = String.format("%d:%02d", minutes, seconds)
    }

    private fun pauseStopwatch() {
        timer?.cancel()
        isTimerRunning = false
    }

    private fun resumeStopwatch() {
        if (!isTimerRunning) {
            // Adjust start time based on elapsed time so far
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
        resumeStopwatch() // Ensure timer is running when displaying question

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
        // Don't pause the stopwatch - let it continue running
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
            // Quiz finished - stop the stopwatch and pass total time
            stopStopwatch()
            binding.questionText.postDelayed({
                val intent = Intent(this, QuizScienceSB::class.java)
                intent.putExtra("CURRENT_SCORE", score)
                intent.putExtra("TOTAL_TIME", totalElapsedTime) // Pass total elapsed time
                intent.putExtra("QUESTIONS_COMPLETED", questions.size)
                startActivity(intent)
                finish()
            }, 1000)
        }
    }

    override fun onPause() {
        super.onPause()
        pauseStopwatch()
    }

    override fun onResume() {
        super.onResume()
        resumeStopwatch()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }

    private fun showResults() {
        val minutes = (totalElapsedTime / 60000).toInt()
        val seconds = ((totalElapsedTime % 60000) / 1000).toInt()

        Toast.makeText(this,
            "Quiz Finished! Score: $score/${questions.size}\nTime: ${minutes}m ${seconds}s",
            Toast.LENGTH_LONG).show()
    }

    private fun setOptionButtonsEnabled(enabled: Boolean) {
        binding.option1Button.isEnabled = enabled
        binding.option2Button.isEnabled = enabled
        binding.option3Button.isEnabled = enabled
        binding.option4Button.isEnabled = enabled
    }
}