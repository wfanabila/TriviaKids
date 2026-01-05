package com.example.triviakids

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.R
class Firstpage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.firstpage)

        val btnGuest = findViewById<Button>(R.id.btnGuest)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvSignUp = findViewById<TextView>(R.id.tvSignUp)

        btnGuest.setOnClickListener {
            // TODO: Navigate as guest
        }

        btnLogin.setOnClickListener {
            // TODO: Navigate to LoginActivity
        }

        tvSignUp.setOnClickListener {
            // TODO: Navigate to SignupActivity
        }
    }
}
