package com.example.quizapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.databinding.EditProfileBinding
import androidx.appcompat.app.AlertDialog // alert dialog ~~
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class EditProfile : AppCompatActivity() {

    private lateinit var binding: EditProfileBinding
    private var selectedAvatar: Int? = null
    // firebase instances
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val editAvatarLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val avatarResId = result.data?.getIntExtra("selectedAvatar", -1) ?: -1
                if (avatarResId != -1) {
                    selectedAvatar = avatarResId
                    binding.imageView.setImageResource(avatarResId)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_nav)
        bottomNav.selectedItemId = R.id.setting

        loadCurrentProfileData() // load current profile data from SharedPreferences ~~

        binding.buttonEditAvatar.setOnClickListener {
            val intent = Intent(this, EditAvatar::class.java)
            editAvatarLauncher.launch(intent)
        }

        binding.save.setOnClickListener {
            val newUsername = binding.username.text.toString().trim()
            val newEmail = binding.email.text.toString().trim()
            val currentPassword = binding.currPass.text.toString().trim()
            val newPassword = binding.newPass.text.toString().trim()

            val currentStoredUsername = ProfilePrefs.getName(this)
            val currentStoredEmail = ProfilePrefs.getEmail(this)
            val currentStoredAvatar = ProfilePrefs.getAvatar(this)

            val isUsernameChanged = newUsername.isNotEmpty() && newUsername != currentStoredUsername
            val isEmailChanged = newEmail.isNotEmpty() && newEmail != currentStoredEmail
            val isPasswordChanged = currentPassword.isNotEmpty() && newPassword.isNotEmpty()

            val isAvatarChanged = selectedAvatar != null && selectedAvatar != currentStoredAvatar

            if (!isUsernameChanged && !isEmailChanged && !isPasswordChanged && !isAvatarChanged) {
                Toast.makeText(this, "No changes detected", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.save.isEnabled = false

            if (isAvatarChanged && !isUsernameChanged && !isEmailChanged && !isPasswordChanged) {
                completeFinalSave(currentStoredUsername, currentStoredEmail)
                return@setOnClickListener
            }

            // multi-step validation for username and email uniqueness
            validateUniqueness(newUsername, newEmail, isUsernameChanged, isEmailChanged) {
                startUpdateProcess(newUsername, newEmail, currentPassword, newPassword, isUsernameChanged, isEmailChanged)
            }
        }

        binding.closeButton.setOnClickListener { showDiscardDialog() }

        // nav bar
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> { navigateToHomePage(); true }
                R.id.profile -> { navigateToProfileActivity(); true }
                R.id.setting -> true
                else -> false
            }
        }
    }

    // check if both new username and new email are available
    private fun validateUniqueness(name: String, mail: String, nameChanged: Boolean, mailChanged: Boolean, onSuccess: () -> Unit) {
        if (nameChanged) {
            db.collection("users").whereEqualTo("username", name).get().addOnSuccessListener { nameDocs ->
                if (!nameDocs.isEmpty) {
                    Toast.makeText(this, "Username already taken.", Toast.LENGTH_SHORT).show()
                    binding.save.isEnabled = true
                } else {
                    checkEmailUniqueness(mail, mailChanged, onSuccess)
                }
            }
        } else {
            checkEmailUniqueness(mail, mailChanged, onSuccess)
        }
    }

    private fun checkEmailUniqueness(mail: String, mailChanged: Boolean, onSuccess: () -> Unit) {
        if (mailChanged) {
            db.collection("users").whereEqualTo("email", mail).get().addOnSuccessListener { mailDocs ->
                if (!mailDocs.isEmpty) {
                    Toast.makeText(this, "Email already in use by another account.", Toast.LENGTH_SHORT).show()
                    binding.save.isEnabled = true
                } else {
                    onSuccess()
                }
            }
        } else {
            onSuccess()
        }
    }

    // handle firestore updates and local saving
    private fun startUpdateProcess(username: String, email: String, currPass: String, newPass: String, userChanged: Boolean, emailChanged: Boolean) {
        val user = auth.currentUser ?: return
        val updates = mutableMapOf<String, Any>()
        if (userChanged) updates["username"] = username
        if (emailChanged) updates["email"] = email

        // password or email changes require reauthentication
        if (emailChanged || (currPass.isNotEmpty() && newPass.isNotEmpty())) {
            handleSecureUpdates(username, email, currPass, newPass, updates, emailChanged)
        } else if (updates.isNotEmpty()) {
            // only username changed
            db.collection("users").document(user.uid).update(updates).addOnSuccessListener {
                completeFinalSave(username, email)
            }
        } else {
            completeFinalSave(username, email)
        }
    }

    private fun handleSecureUpdates(username: String, email: String, currPass: String, newPass: String, updates: Map<String, Any>, emailChanged: Boolean) {
        val user = auth.currentUser ?: return
        val savedPass = ProfilePrefs.getPassword(this)

        if (currPass != savedPass) {
            Toast.makeText(this, "Incorrect current password.", Toast.LENGTH_SHORT).show()
            binding.save.isEnabled = true
            return
        }

        val credential = EmailAuthProvider.getCredential(user.email!!, currPass)
        user.reauthenticate(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // if email changed, update Firebase Auth Email
                if (emailChanged) {
                    user.updateEmail(email).addOnCompleteListener { emailTask ->
                        if (emailTask.isSuccessful) {
                            proceedToPasswordAndFirestore(username, email, newPass, updates)
                        } else {
                            Toast.makeText(this, "Failed to update email: ${emailTask.exception?.message}", Toast.LENGTH_SHORT).show()
                            binding.save.isEnabled = true
                        }
                    }
                } else {
                    proceedToPasswordAndFirestore(username, email, newPass, updates)
                }
            } else {
                Toast.makeText(this, "Reauthentication failed.", Toast.LENGTH_SHORT).show()
                binding.save.isEnabled = true
            }
        }
    }

    private fun proceedToPasswordAndFirestore(name: String, mail: String, newPass: String, updates: Map<String, Any>) {
        val user = auth.currentUser ?: return

        val finishFirestore = {
            if (updates.isNotEmpty()) {
                db.collection("users").document(user.uid).update(updates).addOnSuccessListener {
                    completeFinalSave(name, mail)
                }
            } else {
                completeFinalSave(name, mail)
            }
        }

        if (newPass.isNotEmpty()) {
            user.updatePassword(newPass).addOnCompleteListener { pwTask ->
                if (pwTask.isSuccessful) {
                    ProfilePrefs.savePassword(this, newPass)
                    finishFirestore()
                } else {
                    Toast.makeText(this, "Password update failed.", Toast.LENGTH_SHORT).show()
                    binding.save.isEnabled = true
                }
            }
        } else {
            finishFirestore()
        }
    }

    private fun completeFinalSave(username: String, email: String) {
        if (username.isNotEmpty()) ProfilePrefs.saveName(this, username)
        if (email.isNotEmpty()) ProfilePrefs.saveEmail(this, email)

        selectedAvatar?.let { ProfilePrefs.saveAvatar(this, it) }

        Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()

        val resultIntent = Intent().apply {
            putExtra("newUsername", username)
            putExtra("newEmail", email)
            selectedAvatar?.let { putExtra("selectedAvatar", it) }
        }
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    private fun loadCurrentProfileData() {
        val currentAvatar = ProfilePrefs.getAvatar(this)
        binding.username.setText(ProfilePrefs.getName(this))
        binding.email.setText(ProfilePrefs.getEmail(this))
        binding.imageView.setImageResource(currentAvatar)

        selectedAvatar = currentAvatar
    }

    private fun showDiscardDialog() {
        AlertDialog.Builder(this)
            .setTitle("Discard changes?")
            .setMessage("Your changes will not be saved.")
            .setPositiveButton("Discard") { dialog, _ -> finish() }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun navigateToProfileActivity() { startActivity(Intent(this, ProfileActivity::class.java)) }
    private fun navigateToHomePage() { startActivity(Intent(this, HomePage::class.java)) }
}