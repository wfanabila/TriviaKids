package com.example.triviakids

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class SignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val btnClose = findViewById<ImageView>(R.id.btnClose)
        val btnSignup = findViewById<MaterialButton>(R.id.btnSignup)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etCurrentPassword = findViewById<EditText>(R.id.etCurrentPassword)
        val etNewPassword = findViewById<EditText>(R.id.etNewPassword)

        btnClose.setOnClickListener {
            finish()
        }

        btnSignup.setOnClickListener {

            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val currentPass = etCurrentPassword.text.toString().trim()
            val newPass = etNewPassword.text.toString().trim()

            if (username.isEmpty() ||
                email.isEmpty() ||
                currentPass.isEmpty() ||
                newPass.isEmpty()
            ) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Signup Successful!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
