package com.example.quizapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore

class signup_page : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_page)

        auth = FirebaseAuth.getInstance()

        val btnSignup = findViewById<Button>(R.id.btnSignUp)
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)


        // sign up button
        btnSignup.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                val db = FirebaseFirestore.getInstance()

                // check if email is already in the database
                db.collection("users")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnSuccessListener { emailDocs ->
                        if (!emailDocs.isEmpty) {
                            Toast.makeText(this, "Email is already linked to another account.", Toast.LENGTH_SHORT).show()
                        } else {
                            // check if username is already taken
                            db.collection("users")
                                .whereEqualTo("username", username)
                                .get()
                                .addOnCompleteListener { usernameTask ->
                                    if (usernameTask.isSuccessful) {
                                        if (!usernameTask.result.isEmpty) {
                                            // if username exists
                                            Toast.makeText(this, "Username already taken. Please choose another.", Toast.LENGTH_SHORT).show()
                                        } else {
                                            // username and email are unique, proceed with registration
                                            auth.createUserWithEmailAndPassword(email, password)
                                                .addOnCompleteListener(this) { task ->
                                                    if (task.isSuccessful) {
                                                        val user = auth.currentUser
                                                        val uid = user?.uid ?: ""

                                                        // store the username and email in Firestore with the user's UID as the document ID
                                                        val userData = hashMapOf(
                                                            "username" to username,
                                                            "email" to email
                                                        )

                                                        db.collection("users")
                                                            .document(uid)
                                                            .set(userData)
                                                            .addOnCompleteListener { firestoreTask ->
                                                                if (firestoreTask.isSuccessful) {
                                                                    // successfully saved to Firestore
                                                                    // save the data in SharedPreferences
                                                                    ProfilePrefs.saveName(this, username) // Save username
                                                                    ProfilePrefs.saveEmail(this, email) // Save email
                                                                    ProfilePrefs.saveAvatar(this, R.drawable.pfp_ava)
                                                                    ProfilePrefs.savePassword(this, password)

                                                                    Toast.makeText(this, "Account Created Successfully!", Toast.LENGTH_SHORT).show()
                                                                    startActivity(Intent(this, LoginPage::class.java))
                                                                    finish()
                                                                } else {
                                                                    Toast.makeText(this, "Error: ${firestoreTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                                                }
                                                            }
                                                    } else {
                                                        // catch invalid email format or email taken errors from Firebase Auth
                                                        val errorMessage = when (task.exception) {
                                                            is FirebaseAuthUserCollisionException -> "Email already in use."
                                                            else -> "Registration failed: ${task.exception?.message}"
                                                        }
                                                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                        }
                                    } else {
                                        Toast.makeText(this, "Error checking username: ${usernameTask.exception?.message}", Toast.LENGTH_LONG).show()
                                    }
                                }
                        }
                    }
            }
        }
    }
}