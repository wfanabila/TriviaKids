package com.example.quizapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1. Matches your XML file name: signup_page.xml
        setContentView(R.layout.signup_page)

        // 2. Initializing views based on the IDs in your XML
        val btnClose = findViewById<ImageView>(R.id.btnClose)

        // Note: XML uses 'btnSignUp' (capital 'U'), not 'btnSignup'
        val btnSignup = findViewById<Button>(R.id.btnSignUp)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etEmail = findViewById<EditText>(R.id.etEmail)

        // Note: XML uses 'etPassword', not 'etCurrentPassword'
        val etPassword = findViewById<EditText>(R.id.etPassword)

        // 3. Setup Click Listeners
        btnClose.setOnClickListener {
            finish() // Closes the activity
        }

        btnSignup.setOnClickListener {
            // Add your signup logic here
            val username = etUsername.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
        }
    }
}