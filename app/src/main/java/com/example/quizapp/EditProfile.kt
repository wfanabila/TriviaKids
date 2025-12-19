package com.example.quizapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.databinding.EditProfileBinding

class EditProfile : AppCompatActivity() {

    private lateinit var binding: EditProfileBinding
    private var selectedAvatar: Int? = null

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

        if (newUsername.isEmpty() || newEmail.isEmpty()) {
            Toast.makeText(this, "Username and Email are required.", Toast.LENGTH_SHORT).show()
            return
        }

        // updated data back to profile page ~~
        val resultIntent = Intent()
        resultIntent.putExtra("newUsername", newUsername)
        resultIntent.putExtra("newEmail", newEmail)

        // pass changed avatar ~~
        selectedAvatar?.let {
            resultIntent.putExtra("selectedAvatar", it)
        }

        setResult(RESULT_OK, resultIntent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val avatarResId = data?.getIntExtra("selectedAvatar", -1)
            if (avatarResId != null && avatarResId != -1) {
                selectedAvatar = avatarResId
                binding.imageView.setImageResource(avatarResId)
                Toast.makeText(this, "Avatar updated!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
