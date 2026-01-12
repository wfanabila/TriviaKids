package com.example.quizapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.databinding.EditAvatarBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class EditAvatar : AppCompatActivity() {

    private lateinit var binding: EditAvatarBinding
    private var selectedAvatar: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EditAvatarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_nav)
        bottomNav.selectedItemId = R.id.setting

        // show current avatar ~~
        val currentAvatar = ProfilePrefs.getAvatar(this)
        binding.imageView.setImageResource(currentAvatar)
        selectedAvatar = currentAvatar

        setAvatarSelectionListeners()

        binding.save.setOnClickListener { saveAvatarChanges() }

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
