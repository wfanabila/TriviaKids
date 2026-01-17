package com.example.quizapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.databinding.LoginPageBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class LoginPage : AppCompatActivity() {

    private lateinit var binding: LoginPageBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page)

        auth = FirebaseAuth.getInstance()

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Check if user is already logged in via Firebase
        if (auth.currentUser != null) {
            startActivity(Intent(this, HomePage::class.java))
            finish()
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
        val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    Toast.makeText(this, "Google Sign-In failed. Code: ${e.statusCode}", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Sign-In Cancelled or Failed (Result Code: ${result.resultCode})", Toast.LENGTH_LONG).show()
            }
        }

        btnGoogleLogin.setOnClickListener {
            googleSignInClient.signOut().addOnCompleteListener {
                val signInIntent = googleSignInClient.signInIntent
                googleSignInLauncher.launch(signInIntent)
            }
        }

        // 4. Standard Login Button Logic (Username OR Email)
        btnLogin.setOnClickListener {
            val input = etUsername.text.toString().trim()
            val password = etPassword.text.toString() // Do NOT trim password

            if (input.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val db = FirebaseFirestore.getInstance()
            
            // Try to find by Username first
            db.collection("users")
                .whereEqualTo("username", input)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful && !task.result.isEmpty) {
                        // Found a user with this username
                        val document = task.result.documents[0]
                        val emailFromDb = document.getString("email")

                        if (emailFromDb != null) {
                            // Login with the email found associated with the username
                            performLogin(emailFromDb, password, input)
                        } else {
                            // Fallback: Try input as email directly
                            performLogin(input, password, null)
                        }
                    } else {
                        // Username not found, try input as email directly
                        performLogin(input, password, null)
                    }
                }
        }
    }

    private fun performLogin(email: String, password: String, username: String?) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { authTask ->
                if (authTask.isSuccessful) {
                    Toast.makeText(this, "Login success!", Toast.LENGTH_SHORT).show()
                    val user = auth.currentUser
                    
                    if (username != null) {
                        // We already know the username from the earlier DB lookup
                        finalizeLogin(username, email)
                    } else {
                        // We logged in via email, need to fetch username from DB
                         val uid = user?.uid
                         if (uid != null) {
                             FirebaseFirestore.getInstance().collection("users").document(uid).get()
                                .addOnSuccessListener { document ->
                                    val fetchedUsername = document.getString("username") ?: "User"
                                    finalizeLogin(fetchedUsername, email)
                                }
                                .addOnFailureListener {
                                    finalizeLogin("User", email)
                                }
                         } else {
                             finalizeLogin("User", email)
                         }
                    }
                } else {
                    val msg = authTask.exception?.message ?: "Login failed"
                    if (msg.contains("badly formatted", ignoreCase = true)) {
                         // This usually means we tried to login with a "Username" as an email and it failed.
                         Toast.makeText(this, "Login failed. Check your email/username.", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Error: $msg", Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    private fun finalizeLogin(username: String, email: String) {
        ProfilePrefs.saveName(this, username)
        ProfilePrefs.saveEmail(this, email)
        ProfilePrefs.setLoggedIn(this, true)
        startActivity(Intent(this, HomePage::class.java))
        finish()
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val uid = user?.uid ?: return@addOnCompleteListener
                    val email = user.email ?: ""
                    val displayName = user.displayName ?: "User"

                    val db = FirebaseFirestore.getInstance()
                    val userRef = db.collection("users").document(uid)

                    userRef.get().addOnSuccessListener { document ->
                        if (document.exists()) {
                            val username = document.getString("username") ?: displayName
                            finalizeLogin(username, email)
                        } else {
                            val userData = hashMapOf(
                                "username" to displayName,
                                "email" to email
                            )
                            userRef.set(userData).addOnSuccessListener {
                                finalizeLogin(displayName, email)
                            }.addOnFailureListener {
                                Toast.makeText(this, "Failed to create user profile.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }.addOnFailureListener {
                        Toast.makeText(this, "Failed to fetch user data.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Firebase Auth Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}
