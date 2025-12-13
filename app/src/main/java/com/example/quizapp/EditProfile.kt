package com.example.quizapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.databinding.EditProfileBinding

class EditProfile : AppCompatActivity() {

    // Declare the binding variable
    private lateinit var binding: EditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = EditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)


        loadCurrentProfileData()

        binding.save.setOnClickListener {
            saveProfileChanges()
        }
    }

    private fun loadCurrentProfileData() {

        val currentUsername = "leehan"
        val currentEmail = "leehan404@gmail.com"

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

        Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
    }
}
