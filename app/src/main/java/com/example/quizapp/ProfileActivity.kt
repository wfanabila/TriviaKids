package com.example.quizapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    // to receive result from EditProfile ~~
    private val editProfileLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == RESULT_OK) {
                val newUsername = result.data?.getStringExtra("newUsername")
                val newEmail = result.data?.getStringExtra("newEmail")
                val selectedAvatar =
                    result.data?.getIntExtra("selectedAvatar", -1) ?: -1

                newUsername?.let { ProfilePrefs.saveName(this, it) }
                newEmail?.let { ProfilePrefs.saveEmail(this, it) }
                if (selectedAvatar != -1) {
                    ProfilePrefs.saveAvatar(this, selectedAvatar)
                }

                loadProfileFromPrefs()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadProfileFromPrefs()

        // navigate to edit profile page ~~
        binding.setting.setOnClickListener {
            navigateToEditProfile()
        }

        // navigate to ScoreAfterGame page ~~
        binding.scoreIcon.setOnClickListener {
            startActivity(Intent(this, ScoreAfterGame::class.java))
        }

        // navigate to home page ( yang pilih subject tu ) ~~
        binding.homeIcon.setOnClickListener {

        }
    }

    private fun loadProfileFromPrefs() {
        binding.userName.text = ProfilePrefs.getName(this)
        binding.userEmail.text = ProfilePrefs.getEmail(this)
        binding.imageView.setImageResource(ProfilePrefs.getAvatar(this))
    }

    private fun navigateToEditProfile() {
        val intent = Intent(this, EditProfile::class.java)
        editProfileLauncher.launch(intent)
    }
}
