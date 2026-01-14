package com.example.quizapp

import android.content.Context
import androidx.core.content.edit

    // for saving and retrieving
    // user profile data (name, email, avatar) using SharedPreferences.

object ProfilePrefs {
    private const val PREF = "profile_prefs"
    private const val KEY_NAME = "name"
    private const val KEY_EMAIL = "email"
    private const val KEY_PASSWORD = "password"
    private const val KEY_AVATAR = "avatar"
    private const val KEY_HAS_ACCOUNT = "has_account"
    private const val KEY_LOGGED_IN = "logged_in"

    // SAVE user's details into SharedPreferences
    fun saveName(context: Context, name: String) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit { putString(KEY_NAME, name) }
    }

    fun saveEmail(context: Context, email: String) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit { putString(KEY_EMAIL, email) }
    }

    fun saveAvatar(context: Context, avatarResId: Int) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit { putInt(KEY_AVATAR, avatarResId) }
    }

    // GET saved user's details from SharedPreferences
    fun getName(context: Context): String {
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getString(KEY_NAME, "Leehan") ?: "Leehan"
    }

    fun getEmail(context: Context): String {
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getString(KEY_EMAIL, "defaultEmail@example.com") ?: "defaultEmail@example.com"
    }

    // Get saved password from SharedPreferences
    fun getPassword(context: Context): String =
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getString(KEY_PASSWORD, "") ?: ""

    // Default PFP if no avatar has been set
    fun getAvatar(context: Context): Int {
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getInt(KEY_AVATAR, R.drawable.pfp_ava)  // Default avatar if not set
    }


    fun saveSignupData(context: Context, name: String, email: String, password: String) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit {
            putString(KEY_NAME, name)
            putString(KEY_EMAIL, email)
            putString(KEY_PASSWORD, password)
            putBoolean(KEY_HAS_ACCOUNT, true)  // Mark account as created
            putInt(KEY_AVATAR, R.drawable.pfp_ava) // Default avatar
            apply()
        }
    }

    fun setLoggedIn(context: Context, value: Boolean) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit {
            putBoolean(KEY_LOGGED_IN, value)
        }
    }

    // check if an account exists in SharedPreferences
    fun hasAccount(context: Context): Boolean {
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getBoolean(KEY_HAS_ACCOUNT, false)
    }

    fun isLoggedIn(context: Context): Boolean =
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getBoolean(KEY_LOGGED_IN, false)

    fun logout(context: Context) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit {
                putBoolean(KEY_LOGGED_IN, false)  // Reset logged-in status to false
                apply()
            }
    }

    // save password in SharedPreferences
    fun savePassword(context: Context, newPassword: String) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit {
            putString(KEY_PASSWORD, newPassword)  // Save the new password
            apply()
        }
    }

}
