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
                    // Status code 10 = Developer Error (Check SHA-1/Package Name)
                    Toast.makeText(this, "Google Sign-In failed. Code: ${e.statusCode}", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Sign-In Cancelled or Failed (Result Code: ${result.resultCode})", Toast.LENGTH_LONG).show()
            }
        }

        btnGoogleLogin.setOnClickListener {
            // Force sign-out from Google so the account picker always shows
            googleSignInClient.signOut().addOnCompleteListener {
                val signInIntent = googleSignInClient.signInIntent
                googleSignInLauncher.launch(signInIntent)
            }
        }

        // 4. Standard Login Button Logic
        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // query Firestore to find the email by username
            val db = FirebaseFirestore.getInstance()
            db.collection("users")
                .whereEqualTo("username", username) // find the document where the username matches
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful && !task.result.isEmpty) {
                        val document = task.result.documents[0]
                        val emailFromDb = document.getString("email")

                        if (emailFromDb != null) {
                            auth.signInWithEmailAndPassword(emailFromDb, password)
                                .addOnCompleteListener(this) { authTask ->
                                    if (authTask.isSuccessful) {
                                        // login successful
                                        val user = auth.currentUser
                                        Toast.makeText(this, "Login success!", Toast.LENGTH_SHORT).show()

                                        ProfilePrefs.saveName(this, username)
                                        ProfilePrefs.saveEmail(this, emailFromDb)
                                        ProfilePrefs.setLoggedIn(this, true)


                                        startActivity(Intent(this, HomePage::class.java))
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

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    val user = auth.currentUser
                    val uid = user?.uid ?: return@addOnCompleteListener
                    val email = user.email ?: ""
                    val displayName = user.displayName ?: "User"

                    // Check if user exists in Firestore
                    val db = FirebaseFirestore.getInstance()
                    val userRef = db.collection("users").document(uid)

                    userRef.get().addOnSuccessListener { document ->
                        if (document.exists()) {
                            // User exists, retrieve data
                            val username = document.getString("username") ?: displayName
                            
                            ProfilePrefs.saveName(this, username)
                            ProfilePrefs.saveEmail(this, email)
                            ProfilePrefs.setLoggedIn(this, true)
                            // Ideally save avatar if available from Google or DB
                            
                            updateUI()
                        } else {
                            // New user, create record
                            val userData = hashMapOf(
                                "username" to displayName,
                                "email" to email
                            )
                            
                            userRef.set(userData).addOnSuccessListener {
                                ProfilePrefs.saveName(this, displayName)
                                ProfilePrefs.saveEmail(this, email)
                                ProfilePrefs.setLoggedIn(this, true)
                                updateUI()
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

    private fun updateUI() {
        startActivity(Intent(this, HomePage::class.java))
        finish()
    }
}