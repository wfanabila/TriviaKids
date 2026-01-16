package com.example.quizapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.databinding.ActivityProfileBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private var totalEnglish = 0
    private var totalMaths = 0
    private var totalScience = 0

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

        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_nav)
        bottomNav.selectedItemId = R.id.profile

        loadProfileFromPrefs()
        fetchAndDisplayScores()

        // setting ---> edit profile page
        binding.setting.setOnClickListener { navigateToEditProfile() }
        binding.settingLayout.setOnClickListener { navigateToEditProfile() }
        binding.nextIcon1.setOnClickListener { navigateToEditProfile() }

        // logout ---> exit, login page
        binding.logout.setOnClickListener { navigateToLogin() }
        binding.logoutLayout.setOnClickListener { navigateToLogin() }
        binding.nextIcon2.setOnClickListener { navigateToLogin() }

        // eng score
        binding.engTotalScore.setOnClickListener {
            showScorePopup(
                subject = "ENGLISH",
                total = totalEnglish.toString(),
                best = "10", // You can calculate best logic similarly later
                fastest = "04:07"
            )
        }

        // math score
        binding.mathTotalScore.setOnClickListener {
            showScorePopup(
                subject = "MATHEMATICS",
                total = totalMaths.toString(),
                best = "9",
                fastest = "02:37"
            )
        }

        // scn score
        binding.scnTotalScore.setOnClickListener {
            showScorePopup(
                subject = "SCIENCE",
                total = totalScience.toString(),
                best = "6",
                fastest = "03:07"
            )
        }

        // view pfp
        binding.imageView.setOnClickListener {
            showProfilePopup(ProfilePrefs.getAvatar(this))
        }

        // nav bar
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    navigateToHomePage()
                    true
                }
                R.id.profile -> {
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

    override fun onResume() {
        super.onResume()
        fetchAndDisplayScores()
    }

    private fun fetchAndDisplayScores() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) return

        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .document(user.uid)
            .collection("history")
            .get()
            .addOnSuccessListener { result ->
                totalEnglish = 0
                totalMaths = 0
                totalScience = 0

                for (document in result) {
                    val score = document.getLong("score")?.toInt() ?: 0
                    val quizType = document.getString("quizType") ?: ""

                    when (quizType) {
                        "English" -> totalEnglish += score
                        "Maths" -> totalMaths += score
                        "Science" -> totalScience += score
                    }
                }

                // Update UI
                binding.engTotalLabel.text = totalEnglish.toString()
                binding.mathTotalLabel.text = totalMaths.toString()
                binding.scnTotalLabel.text = totalScience.toString()
            }
            .addOnFailureListener { exception ->
                Log.w("ProfileActivity", "Error getting documents.", exception)
            }
    }

    private fun loadProfileFromPrefs() {
        // load the data from SharedPreferences
        val username = ProfilePrefs.getName(this)
        val email = ProfilePrefs.getEmail(this)
        val avatar = ProfilePrefs.getAvatar(this)

        binding.userName.text = username
        binding.userEmail.text = email
        binding.imageView.setImageResource(avatar)
    }

    // pop up score
    private fun showScorePopup(subject: String, total: String, best: String, fastest: String) {
        val dialogView = when (subject) {
            "ENGLISH" -> layoutInflater.inflate(R.layout.dialog_eng_score_details, null)
            "MATHEMATICS" -> layoutInflater.inflate(R.layout.dialog_math_score_details, null)
            "SCIENCE" -> layoutInflater.inflate(R.layout.dialog_scn_score_details, null)
            else -> layoutInflater.inflate(R.layout.dialog_eng_score_details, null) // Default case
        }

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


    private fun navigateToEditProfile() {
        val intent = Intent(this, EditProfile::class.java)
        editProfileLauncher.launch(intent)
    }

    private fun navigateToHomePage() {
        val intent = Intent(this, HomePage::class.java)
        startActivity(intent)
    }

    private fun navigateToLogin() {
        // Sign out from Firebase
        FirebaseAuth.getInstance().signOut()

        // Sign out from Local Prefs
        ProfilePrefs.logout(this)

        val intent = Intent(this, LoginPage::class.java)
        // Clear task to prevent going back
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }


}
