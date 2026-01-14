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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginPage : AppCompatActivity() {

    private lateinit var binding: LoginPageBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page)

        auth = FirebaseAuth.getInstance()

        // check if the user is already signed up (if their details exist in SharedPreferences)
        val name = ProfilePrefs.getName(this)
        val email = ProfilePrefs.getEmail(this)

        // if no details are found, redirect to signup page
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
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Query Firestore to find the email by username
            val db = FirebaseFirestore.getInstance()
            db.collection("users")
                .whereEqualTo("username", username) // Find the document where the username matches
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful && !task.result.isEmpty) {
                        val document = task.result.documents[0] // Get the first document (should be only one)
                        val email = document.getString("email")

                        if (email != null) {
                            // Use the email to sign in with Firebase Authentication
                            auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(this) { authTask ->
                                    if (authTask.isSuccessful) {
                                        // Login successful
                                        val user = auth.currentUser
                                        Toast.makeText(this, "Login success!", Toast.LENGTH_SHORT).show()

                                        ProfilePrefs.saveName(this, username) // Save username
                                        ProfilePrefs.saveEmail(this, email) // Save email
                                        ProfilePrefs.setLoggedIn(this, true)


                                        startActivity(Intent(this, HomePage::class.java))  // Navigate to HomePage
                                        finish()
                                    } else {
                                        Toast.makeText(this, "Wrong password", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        } else {
                            Toast.makeText(this, "Username not found", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Username not found", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}