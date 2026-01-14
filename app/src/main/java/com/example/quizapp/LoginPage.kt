package com.example.quizapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.databinding.LoginPageBinding

class LoginPage : AppCompatActivity() {

    private lateinit var binding: LoginPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page)

        // Check if the user is already signed up (if their details exist in SharedPreferences)
        val name = ProfilePrefs.getName(this)
        val email = ProfilePrefs.getEmail(this)

        // If no details are found, redirect to signup page
        if (name == "Leehan" || email == "defaultEmail@example.com") {
            val intent = Intent(this, signup_page::class.java)
            startActivity(intent)
            finish()  // Close the login page after redirecting to signup
        }

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
            val inputUsername = etUsername.text.toString().trim()
            val inputPassword = etPassword.text.toString().trim()

            // Check if the username or password fields are empty
            if (inputUsername.isEmpty() || inputPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if no account has been created yet (account doesn't exist in SharedPreferences)
            if (!ProfilePrefs.hasAccount(this)) {
                // If no account exists, redirect to SignupPage
                Toast.makeText(this, "No account found. Please sign up first.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, signup_page::class.java))  // Redirect to Signup page
                finish()  // Close the LoginPage after redirecting
                return@setOnClickListener
            }

            // If account exists, compare entered credentials with stored credentials
            val savedUsername = ProfilePrefs.getName(this)
            val savedPassword = ProfilePrefs.getPassword(this)

            // If credentials match
            if (inputUsername == savedUsername && inputPassword == savedPassword) {
                ProfilePrefs.setLoggedIn(this, true)  // Set the user as logged in
                Toast.makeText(this, "Login success!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, HomePage::class.java))  // Navigate to HomePage
                finish()  // Close the LoginPage
            } else {
                // If credentials don't match, just redirect to SignupPage
                Toast.makeText(this, "Account not found. Please Sign Up", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, signup_page::class.java))  // Redirect to Signup page
                finish()  // Close the LoginPage after redirecting
            }
        }

    }
}