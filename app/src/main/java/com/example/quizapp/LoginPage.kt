package com.example.quizapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page)

        // 1. Initialize the Views
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvSignUpLink = findViewById<TextView>(R.id.tvSignUpLink)
        val btnGoogleLogin = findViewById<LinearLayout>(R.id.btnGoogleLogin)

        // 2. Open SignupActivity
        tvSignUpLink.setOnClickListener {
            val intent = Intent(this, signup_page::class.java)
            startActivity(intent)
        }

        // 3. Google Login Function
        btnGoogleLogin.setOnClickListener {
            // This is where you would normally call the Google Sign-In API
            Toast.makeText(this, "Connecting to Google...", Toast.LENGTH_SHORT).show()
        }

        // 4. Standard Login Button Logic
        btnLogin.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()

            // THIS LINKS TO HOME PAGE
            val intent = Intent(this, HomePage::class.java)
            startActivity(intent)

            if (username.isNotEmpty() && password.isNotEmpty()) {
                Toast.makeText(this, "Logging in...", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}