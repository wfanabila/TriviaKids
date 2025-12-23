package com.example.quizapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.databinding.EditAvatarBinding

class EditAvatar : AppCompatActivity() {

    private lateinit var binding: EditAvatarBinding
    private var selectedAvatar: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EditAvatarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // show current avatar ~~
        val currentAvatar = ProfilePrefs.getAvatar(this)
        binding.imageView.setImageResource(currentAvatar)
        selectedAvatar = currentAvatar

        setAvatarSelectionListeners()

        binding.save.setOnClickListener { saveAvatarChanges() }

        binding.scoreIcon.setOnClickListener {
            startActivity(Intent(this, ScoreAfterGame::class.java))
        }

        binding.homeIcon.setOnClickListener {

        }
    }

    private fun setAvatarSelectionListeners() {
        binding.imageButtonAvatar1.setOnClickListener { selectAvatar(R.drawable.avatar_1) }
        binding.imageButtonAvatar2.setOnClickListener { selectAvatar(R.drawable.avatar_2) }
        binding.imageButtonAvatar3.setOnClickListener { selectAvatar(R.drawable.avatar_3) }
        binding.imageButtonAvatar4.setOnClickListener { selectAvatar(R.drawable.avatar_4) }
        binding.imageButtonAvatar5.setOnClickListener { selectAvatar(R.drawable.avatar_5) }
        binding.imageButtonAvatar6.setOnClickListener { selectAvatar(R.drawable.avatar_6) }
        binding.imageButtonAvatar7.setOnClickListener { selectAvatar(R.drawable.avatar_7) }
        binding.imageButtonAvatar8.setOnClickListener { selectAvatar(R.drawable.avatar_8) }
        binding.imageButtonPfp.setOnClickListener { selectAvatar(R.drawable.pfp_ava) }
    }

    private fun selectAvatar(avatarResId: Int) {
        selectedAvatar = avatarResId
        binding.imageView.setImageResource(avatarResId)
        Toast.makeText(this, "Avatar selected", Toast.LENGTH_SHORT).show()
    }

    private fun saveAvatarChanges() {
        if (selectedAvatar == null) {
            Toast.makeText(this, "Please select an avatar", Toast.LENGTH_SHORT).show()
            return
        }

        // back to EditProfile page ~~
        val resultIntent = Intent()
        resultIntent.putExtra("selectedAvatar", selectedAvatar)
        setResult(RESULT_OK, resultIntent)
        finish()
    }
}
