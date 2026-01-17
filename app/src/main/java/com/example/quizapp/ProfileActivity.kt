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
import com.google.firebase.firestore.FieldValue

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private var totalEnglish = 0
    private var totalMaths = 0
    private var totalScience = 0
    private var bestEnglish = 0
    private var bestMaths = 0
    private var bestScience = 0
    private var fastestEnglish = "00:00"
    private var fastestMaths = "00:00"
    private var fastestScience = "00:00"

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
                best = bestEnglish.toString(),
                fastest = fastestEnglish.toString()
            )
        }

        // math score
        binding.mathTotalScore.setOnClickListener {
            showScorePopup(
                subject = "MATHEMATICS",
                total = totalMaths.toString(),
                best = bestMaths.toString(),
                fastest = fastestMaths.toString()
            )
        }

        // scn score
        binding.scnTotalScore.setOnClickListener {
            showScorePopup(
                subject = "SCIENCE",
                total = totalScience.toString(),
                best = bestScience.toString(),
                fastest = fastestScience.toString()
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
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("users")
            .document(user.uid)
            .collection("history")
            .get()
            .addOnSuccessListener { result ->
                totalEnglish = 0; totalMaths = 0; totalScience = 0
                bestEnglish = 0; bestMaths = 0; bestScience = 0
                fastestEnglish = "00:00"; fastestMaths = "00:00"; fastestScience = "00:00"

                for (document in result) {
                    val quizType = document.getString("quizType") ?: ""
                    val score = document.getLong("score")?.toInt() ?: 0

                    val timeRecord = document.getString("totalTime") ?: "00:00"

                    when (quizType) {
                        "English" -> {
                            totalEnglish += score
                            if (score > bestEnglish) bestEnglish = score
                            // compare to find the actual fastest time
                            if (isFaster(timeRecord, fastestEnglish)) fastestEnglish = timeRecord
                        }
                        "Maths" -> {
                            totalMaths += score
                            if (score > bestMaths) bestMaths = score
                            if (isFaster(timeRecord, fastestMaths)) fastestMaths = timeRecord
                        }
                        "Science" -> {
                            totalScience += score
                            if (score > bestScience) bestScience = score
                            if (isFaster(timeRecord, fastestScience)) fastestScience = timeRecord
                        }
                    }
                }

                binding.engTotalLabel.text = totalEnglish.toString()
                binding.mathTotalLabel.text = totalMaths.toString()
                binding.scnTotalLabel.text = totalScience.toString()
            }
    }

    private fun updateSubjectScore(quizType: String, score: Int, time: String) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) return

        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(user.uid)
        val quizRef = userRef.collection("history").document(quizType)

        quizRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val currentBestScore = document.getLong("bestScore")?.toInt() ?: 0
                    val currentFastestTime = document.getString("fastestTime") ?: "00:00"

                    // check if the new score is better than the current best score
                    val newBestScore = if (score > currentBestScore) score else currentBestScore

                    if (newBestScore != currentBestScore) {
                        quizRef.update("bestScore", newBestScore)
                            .addOnSuccessListener {
                                Log.d("ProfileActivity", "Successfully updated best score")
                            }
                            .addOnFailureListener { e ->
                                Log.w("ProfileActivity", "Error updating best score", e)
                            }
                    }

                    // compare and update fastest time only if the score is the best
                    var newFastestTime = currentFastestTime
                    if (score == newBestScore) {
                        // check if the new time is faster
                        newFastestTime = if (isFaster(time, currentFastestTime)) time else currentFastestTime
                    }

                    // update fastest time if it's different
                    if (newFastestTime != currentFastestTime) {
                        quizRef.update("fastestTime", newFastestTime)
                            .addOnSuccessListener {
                                Log.d("ProfileActivity", "Successfully updated fastest time")
                            }
                            .addOnFailureListener { e ->
                                Log.w("ProfileActivity", "Error updating fastest time", e)
                            }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("ProfileActivity", "Error getting document.", exception)
            }
    }

    private fun isFaster(newTime: String, currentFastestTime: String): Boolean {
        if (currentFastestTime == "00:00") return true

        val newTimeParts = newTime.split(":").map { it.toIntOrNull() ?: 0 }
        val currentTimeParts = currentFastestTime.split(":").map { it.toIntOrNull() ?: 0 }

        if (newTimeParts.size != 2 || currentTimeParts.size != 2) return false

        val newTimeInSeconds = newTimeParts[0] * 60 + newTimeParts[1]
        val currentTimeInSeconds = currentTimeParts[0] * 60 + currentTimeParts[1]

        if (newTimeInSeconds <= 0) return false

        return newTimeInSeconds < currentTimeInSeconds
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
            else -> layoutInflater.inflate(R.layout.dialog_eng_score_details, null)
        }

        val titleTv = dialogView.findViewById<TextView>(R.id.dialogTitle)
        val totalTv = dialogView.findViewById<TextView>(R.id.dialogTotal)
        val bestTv = dialogView.findViewById<TextView>(R.id.dialogBest)
        val fastestTv = dialogView.findViewById<TextView>(R.id.dialogFastestTime)
        val subjectTv = dialogView.findViewById<TextView>(R.id.dialogSubject)

        titleTv.text = getString(R.string.total_score_title)
        totalTv.text = total
        bestTv.text = ": $best"
        fastestTv.text = ": $fastest"
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
        // sign out from Firebase
        FirebaseAuth.getInstance().signOut()

        // sign out from Local Prefs
        ProfilePrefs.logout(this)

        val intent = Intent(this, LoginPage::class.java)
        // clear task to prevent going back
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

}
