package com.example.quizapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.databinding.EditProfileBinding

class EditProfile : AppCompatActivity() {

    private lateinit var binding: EditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = EditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadCurrentProfileData()

        binding.buttonEditAvatar.setOnClickListener {
            val intent = Intent(this, EditAvatar::class.java)
            startActivityForResult(intent, 1)
        }

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

    // update dari EditAvatar activity ~~
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val selectedAvatar = data?.getIntExtra("selectedAvatar", -1)
            if (selectedAvatar != null && selectedAvatar != -1) {
                binding.imageView.setImageResource(selectedAvatar)  // Update the profile picture
                Toast.makeText(this, "Avatar updated!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
