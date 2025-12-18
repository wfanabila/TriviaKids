package com.example.quizapp

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Example user data
        val userName = "Leehan"
        val userEmail = "leehan04@gmail.com"
        val totalScore = 90
        val englishScore = 20
        val mathScore = 15
        val scienceScore = 25

        // Setting user data into the UI
        binding.profileName.text = userName
        binding.profileEmail.text = userEmail
        binding.totalScore.text = "Total Score: $totalScore/100"
        binding.englishScore.text = "English: $englishScore"
        binding.mathScore.text = "Math: $mathScore"
        binding.scienceScore.text = "Science: $scienceScore"

        // Button to Edit Profile
        binding.editProfileButton.setOnClickListener {
            // You can navigate to another activity (for editing the profile)
            // For now, just show a message or navigate to the EditProfileActivity
            // startActivity(Intent(this, EditProfileActivity::class.java))
        }
    }
}
