package com.example.quizapp
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.R as res // This lets you use 'res' instead of 'R'

// 1. Data model for the handshake
data class Question(
    val displayWord: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val imageResId: Int
)

class QuizScienceSB : AppCompatActivity() {

    private lateinit var tvWordDisplay: TextView
    private lateinit var ivHintImage: ImageView
    private lateinit var buttons: List<Button>

    private var currentQuestionIndex = 0
    private val quizQuestions = listOf(
        Question("B _ E", listOf("R", "L", "E"), 2, res.drawable.bee),
        Question("H E A _", listOf("H", "D", "S"), 1, res.drawable.head),
        Question("G _ L D", listOf("E", "O", "S"), 1, res.drawable.gold),
        Question("S O I _", listOf("O", "P", "L"), 2, res.drawable.soil),
        Question("W A _ E R", listOf("T", "I", "O"), 0, res.drawable.water)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(res.layout.quiz_science_1)

        // Binding variables to XML IDs
        tvWordDisplay = findViewById(res.id.tvWordDisplay)
        ivHintImage = findViewById(res.id.ivHintImage)
        buttons = listOf(
            findViewById(res.id.btnOption1),
            findViewById(res.id.btnOption2),
            findViewById(res.id.btnOption3)
        )

        loadQuestion()
    }

    private fun loadQuestion() {
        if (currentQuestionIndex >= quizQuestions.size) {
            // GO TO SCORE PAGE HERE
            return
        }

        val question = quizQuestions[currentQuestionIndex]
        tvWordDisplay.text = question.displayWord
        ivHintImage.setImageResource(question.imageResId)

        // Reset buttons for new question
        for (i in buttons.indices) {
            buttons[i].text = question.options[i]
            buttons[i].setBackgroundColor(Color.parseColor("#C2C2FF"))
            buttons[i].isEnabled = true
            buttons[i].setOnClickListener { checkAnswer(i) }
        }
    }

    private fun checkAnswer(selectedIndex: Int) {
        val question = quizQuestions[currentQuestionIndex]

        // Disable all buttons so user can't click twice
        buttons.forEach { it.isEnabled = false }

        if (selectedIndex == question.correctAnswerIndex) {
            // Correct -> Green
            buttons[selectedIndex].setBackgroundColor(Color.GREEN)
        } else {
            // Wrong -> Red, and show the Correct one in Green
            buttons[selectedIndex].setBackgroundColor(Color.RED)
            buttons[question.correctAnswerIndex].setBackgroundColor(Color.GREEN)
        }

        // Timer: Wait 1.5 seconds before moving to next question
        Handler(Looper.getMainLooper()).postDelayed({
            currentQuestionIndex++
            loadQuestion()
        }, 1500)
    }
}