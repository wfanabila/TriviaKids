package com.example.quizapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Delay for 3 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            // Move from Splash to Login Page
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)

            // Kill SplashActivity so the user can't go "back" to it
            finish()
        }, 3000)
    }
}