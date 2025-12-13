package com.example.quizapp.triviakids

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.databinding.EditProfileBinding // Corrected binding class

class EditProfile : AppCompatActivity() {

    // Declare the binding variable
    private lateinit var binding: EditProfileBinding  // Use the correct binding class

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the binding object
        binding = EditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // --- 1. Load current profile data ---
        loadCurrentProfileData()

        // --- 2. Set up the Save Changes button ---
        binding.save.setOnClickListener {
            // Call the function to handle the profile update
            saveProfileChanges()
        }
    }

    private fun loadCurrentProfileData() {
        // TODO: Replace with actual data fetching logic (e.g., SharedPreferences, database)

        // Example data loading:
        val currentUsername = "leehan"
        val currentEmail = "leehan404@gmail.com"

        // Set the loaded data to the input fields
        binding.username.setText(currentUsername)
        binding.email.setText(currentEmail)
    }

    private fun saveProfileChanges() {
        val newUsername = binding.username.text.toString().trim()
        val newEmail = binding.email.text.toString().trim()
        val currentPassword = binding.currPass.text.toString().trim()
        val newPassword = binding.newPass.text.toString().trim()

        if (newUsername.isEmpty() || newEmail.isEmpty() || currentPassword.isEmpty()) {
            Toast.makeText(this, "Username, Email, and Current Password are required.", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword.isNotEmpty() && newPassword.length < 6) {
            Toast.makeText(this, "New Password must be at least 6 characters long.", Toast.LENGTH_SHORT).show()
            return
        }

        // --- Profile Update Logic ---
        // TODO: 1. Verify the 'currentPassword' against the stored hash.
        // If it doesn't match, show an error and return.

        // TODO: 2. Perform the actual update (e.g., API call, local storage update)

        // Example success message (after successful API/DB update)
        Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()

        // Optionally, close the activity after success
        // finish()
    }
}
