package com.example.quizapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.databinding.EditProfileBinding
import androidx.appcompat.app.AlertDialog // alert dialog ~~


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

        loadCurrentProfileData() // load current profile data from SharedPreferences ~~

        binding.buttonEditAvatar.setOnClickListener {
            val intent = Intent(this, EditAvatar::class.java)
            editAvatarLauncher.launch(intent)
        }

        binding.save.setOnClickListener {
            saveProfileChanges()
        }

        binding.scoreIcon.setOnClickListener {
            startActivity(Intent(this, ScoreAfterGame::class.java))
        }

        binding.homeIcon.setOnClickListener {

        }

        binding.profileIcon.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.closeButton.setOnClickListener {
            showDiscardDialog()
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


}
