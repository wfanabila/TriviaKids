package com.example.quizapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.databinding.EditProfileBinding
import androidx.appcompat.app.AlertDialog // alert dialog ~~
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.EmailAuthProvider



class EditProfile : AppCompatActivity() {

    private lateinit var binding: EditProfileBinding
    private var selectedAvatar: Int? = null

    private val editAvatarLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val avatarResId = result.data?.getIntExtra("selectedAvatar", -1) ?: -1
                if (avatarResId != -1) {
                    selectedAvatar = avatarResId
                    binding.imageView.setImageResource(avatarResId)

                    // update to the latest avatar everytime
                    ProfilePrefs.saveAvatar(this, avatarResId)
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

            // check if any field is filled. only need the current and new passwords if the user wants to change the password
            if (newUsername.isEmpty() && newEmail.isEmpty() && currentPassword.isEmpty() && newPassword.isEmpty() && selectedAvatar == null) {
                // if the user doesn't want to change anything, just exit
                Toast.makeText(this, "No changes detected", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // validate current password
            if (currentPassword.isNotEmpty() && newPassword.isNotEmpty()) {
                val savedPassword = ProfilePrefs.getPassword(this)

                if (currentPassword != savedPassword) {
                    Toast.makeText(this, "Incorrect current password. Please try again.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // reauthenticate the user with the current password before updating
                val user = FirebaseAuth.getInstance().currentUser
                val email = user?.email

                if (email != null) {
                    val credential = EmailAuthProvider.getCredential(email, currentPassword)

                    user.reauthenticate(credential).addOnCompleteListener { reAuthTask ->
                        if (reAuthTask.isSuccessful) {
                            // if reauthentication is successful, update the password
                            user.updatePassword(newPassword).addOnCompleteListener { updatePasswordTask ->
                                if (updatePasswordTask.isSuccessful) {
                                    // save the new password to SharedPreferences
                                    ProfilePrefs.savePassword(this, newPassword)
                                    Toast.makeText(this, "Password updated successfully!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this, "Failed to update password: ${updatePasswordTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(this, "Reauthentication failed. Please try again.", Toast.LENGTH_SHORT).show()
                            return@addOnCompleteListener
                        }
                    }
                }
            }

            // save the new username and email
            if (newUsername.isNotEmpty()) {
                ProfilePrefs.saveName(this, newUsername)
            }
            if (newEmail.isNotEmpty()) {
                ProfilePrefs.saveEmail(this, newEmail)
            }

            // save selected avatar
            selectedAvatar?.let { ProfilePrefs.saveAvatar(this, it) }

            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()

            // pass the updated data back to the calling activity (ProfileActivity)
            val resultIntent = Intent().apply {
                putExtra("newUsername", newUsername)
                putExtra("newEmail", newEmail)
                selectedAvatar?.let { putExtra("selectedAvatar", it) }
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }


        binding.closeButton.setOnClickListener {
            showDiscardDialog()
        }

        // nav bar
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    navigateToHomePage()
                    true
                }
                R.id.profile -> {
                    navigateToProfileActivity()
                    true
                }
                R.id.setting -> {
                    true
                }
                else -> false
            }
        }
    }

    private fun loadCurrentProfileData() {
        binding.username.setText(ProfilePrefs.getName(this))
        binding.email.setText(ProfilePrefs.getEmail(this))
        val avatar = ProfilePrefs.getAvatar(this)
        binding.imageView.setImageResource(avatar)
        selectedAvatar = avatar
    }

    private fun saveProfileChanges() {
        val newUsername = binding.username.text.toString().trim()
        val newEmail = binding.email.text.toString().trim()

        if (newUsername.isEmpty() || newEmail.isEmpty()) {
            Toast.makeText(this, "Username and Email are required", Toast.LENGTH_SHORT).show()
            return
        }

        ProfilePrefs.saveName(this, newUsername)
        ProfilePrefs.saveEmail(this, newEmail)
        selectedAvatar?.let { ProfilePrefs.saveAvatar(this, it) }

        val resultIntent = Intent().apply {
            putExtra("newUsername", newUsername)
            putExtra("newEmail", newEmail)
            selectedAvatar?.let { putExtra("selectedAvatar", it) }
        }

        setResult(RESULT_OK, resultIntent)
        finish()
    }

    // alert dialog to confirm whether the user wants to discard changes ~~
    private fun showDiscardDialog() {
        AlertDialog.Builder(this)
            .setTitle("Discard changes?")
            .setMessage("Your changes will not be saved.")
            .setPositiveButton("Discard") { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


    private fun navigateToProfileActivity() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToHomePage() {
        val intent = Intent(this, HomePage::class.java)
        startActivity(intent)
    }
}
