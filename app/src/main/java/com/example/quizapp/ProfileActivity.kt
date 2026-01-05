package com.example.quizapp

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.databinding.ActivityProfileBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    // receive result from edit profile ~~
    private val editProfileLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val newUsername = result.data?.getStringExtra("newUsername")
                val newEmail = result.data?.getStringExtra("newEmail")
                val selectedAvatar = result.data?.getIntExtra("selectedAvatar", -1) ?: -1

                newUsername?.let { ProfilePrefs.saveName(this, it) }
                newEmail?.let { ProfilePrefs.saveEmail(this, it) }
                if (selectedAvatar != -1) ProfilePrefs.saveAvatar(this, selectedAvatar)

                loadProfileFromPrefs()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadProfileFromPrefs()

        binding.setting.setOnClickListener { navigateToEditProfile() }
        binding.settingLayout.setOnClickListener { navigateToEditProfile() }
        binding.nextIcon1.setOnClickListener { navigateToEditProfile() }
        binding.nextIcon2.setOnClickListener { navigateToEditProfile() }

        // score popup
        binding.engTotalScore.setOnClickListener {
            showScorePopup(
                subject = "ENGLISH",
                total = binding.engTotalLabel.text.toString(),
                best = "10",
                fastest = "04:07"
            )
        }

        binding.mathTotalScore.setOnClickListener {
            showScorePopup(
                subject = "MATHEMATICS",
                total = binding.mathTotalLabel.text.toString(),
                best = "9",
                fastest = "02:37"
            )
        }

        binding.scnTotalScore.setOnClickListener {
            showScorePopup(
                subject = "SCIENCE",
                total = binding.scnTotalLabel.text.toString(),
                best = "6",
                fastest = "03:07"
            )
        }

        // view pfp
        binding.imageView.setOnClickListener {
            showProfilePopup(ProfilePrefs.getAvatar(this))
        }

        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_nav)
        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    val intent = Intent(this, HomePage::class.java)
                    startActivity(intent)
                    true
                }
                R.id.profile -> {
                    // stay in profile page.
                    true
                }
                R.id.setting -> {
                    navigateToEditProfile()
                    true
                }
                else -> false
            }
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


    // pop up score
    private fun showScorePopup(
        subject: String,
        total: String,
        best: String,
        fastest: String
    ) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_score_details, null)

        val titleTv = dialogView.findViewById<TextView>(R.id.dialogTitle)
        val totalTv = dialogView.findViewById<TextView>(R.id.dialogTotal)
        val bestTv = dialogView.findViewById<TextView>(R.id.dialogBest)
        val fastestTv = dialogView.findViewById<TextView>(R.id.dialogFastest)
        val subjectTv = dialogView.findViewById<TextView>(R.id.dialogSubject)

        titleTv.text = getString(R.string.total_score_title)
        totalTv.text = total
        bestTv.text = getString(R.string.best_score_value, best)
        fastestTv.text = getString(R.string.fastest_time_value, fastest)
        subjectTv.text = getString(R.string.subject_score_label, subject)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_transparent_bg)

        dialog.show()

        dialogView.setOnClickListener { dialog.dismiss() }
    }


    // view pfp
    private fun showProfilePopup(avatarResId: Int) {
        val dialogView = layoutInflater.inflate(R.layout.view_profile_pic, null)

        val img = dialogView.findViewById<android.widget.ImageView>(R.id.dialogProfileImage)
        img.setImageResource(avatarResId)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_transparent_bg)

        dialog.show()

        img.setOnClickListener { dialog.dismiss() }

        dialogView.setOnClickListener { dialog.dismiss() }
    }

}
