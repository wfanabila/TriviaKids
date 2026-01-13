package com.example.quizapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class signup_page : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_page)

        val btnClose = findViewById<ImageView>(R.id.btnClose)
        val btnSignup = findViewById<Button>(R.id.btnSignUp)
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)

        // Close button: Goes back to the previous screen
        btnClose.setOnClickListener {
            finish()
        }

        // SIGN UP button logic
        btnSignup.setOnClickListener {
            val username = etUsername.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            // Check if fields are empty before proceeding
            if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {

                Toast.makeText(this, "Account Created Successfully!", Toast.LENGTH_SHORT).show()

                // THIS LINKS TO LOGIN PAGE
                val intent = Intent(this, LoginPage::class.java)
                startActivity(intent)

                // Finish this activity so the user doesn't go back to signup when pressing back
                finish()

            } else {
                Toast.makeText(this, "Please fill in all details", Toast.LENGTH_SHORT).show()
            }
        }
    }
}