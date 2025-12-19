package com.example.quizapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.userName.text = getString(R.string.username)
        binding.userEmail.text = getString(R.string.user_email)

        // navigate to edit profile page  ~~
        binding.setting.setOnClickListener {
            navigateToEditProfile()
        }

        binding.scoreIcon.setOnClickListener {
        }

        binding.homeIcon.setOnClickListener {
            // navigate to home screen page ( yang pilih subject )
        }

        binding.profileIcon.setOnClickListener {
        }
    }

    private fun navigateToEditProfile() {
        val intent = Intent(this, EditProfile::class.java)
        startActivityForResult(intent, 100)
    }

    // data from EditProfile
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            val newUsername = data?.getStringExtra("newUsername")
            val newEmail = data?.getStringExtra("newEmail")
            val selectedAvatar = data?.getIntExtra("selectedAvatar", -1)

            if (newUsername != null) {
                binding.userName.text = newUsername
            }

            if (newEmail != null) {
                binding.userEmail.text = newEmail
            }

            if (selectedAvatar != null && selectedAvatar != -1) {
                binding.imageView.setImageResource(selectedAvatar)
            }
        }
    }

}
