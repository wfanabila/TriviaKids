package com.example.quizapp

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.databinding.ActivityMainBinding
import com.google.android.material.button.MaterialButton

class QuizMath : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val optionData = arrayOf(
        listOf(
            R.drawable.number8, // correct
            "Four",
            "Eight",
            R.drawable.number4
        )
    )

    private val correctAnswers = arrayOf(0)

    private var currentQuestionIndex = 0
    private var score = 7

    private lateinit var optionButtons: List<MaterialButton>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        optionButtons = listOf(
            binding.option0,
            binding.option1,
            binding.option2,
            binding.option3
        )

        displayQuestion()
        startTimer()

        optionButtons.forEachIndexed { index, button ->
            button.setOnClickListener { checkAnswer(index) }
        }

        binding.btnClose.setOnClickListener { finish() }
    }

    private fun displayQuestion() {
        val currentOptions = optionData[currentQuestionIndex]

        for (i in 0..3) {
            val btn = optionButtons[i]
            val data = currentOptions[i]

            btn.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#FFE082"))
            btn.isEnabled = true

            if (data is Int) {
                btn.setIconResource(data)
                btn.iconTint = null
                btn.text = ""
            } else {
                btn.text = data
                btn.icon = null
            }
        }

        binding.tvScore.text = "$score/10"
    }

    private fun checkAnswer(selectedIndex: Int) {
        optionButtons.forEach { it.isEnabled = false }

        val correctIndex = correctAnswers[currentQuestionIndex]

        if (selectedIndex == correctIndex) {
            score++
            setGreen(optionButtons[selectedIndex])
        } else {
            setRed(optionButtons[selectedIndex])
            setGreen(optionButtons[correctIndex])
        }

        binding.tvScore.text = "$score/10"

        binding.root.postDelayed({
            Toast.makeText(this, "Quiz Finished!", Toast.LENGTH_SHORT).show()
        }, 1000)
    }

    private fun setGreen(btn: MaterialButton) {
        btn.backgroundTintList =
            ColorStateList.valueOf(Color.parseColor("#61E547"))
    }

    private fun setRed(btn: MaterialButton) {
        btn.backgroundTintList =
            ColorStateList.valueOf(Color.parseColor("#FF4A4C"))
    }

    private fun startTimer() {
        object : CountDownTimer(63000, 1000) {
            override fun onTick(ms: Long) {
                binding.tvTime.text =
                    "01:${String.format("%02d", ms / 1000)}"
            }

            override fun onFinish() {
                binding.tvTime.text = "00:00"
                optionButtons.forEach { it.isEnabled = false }
            }
        }.start()
    }
}
