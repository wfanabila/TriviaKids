package com.example.quizapp

import android.content.Context
import androidx.core.content.edit

    // for saving and retrieving
    // user profile data (name, email, avatar) using SharedPreferences.

object ProfilePrefs {
    private const val PREF = "profile_prefs"
    private const val KEY_NAME = "name"
    private const val KEY_EMAIL = "email"
    private const val KEY_AVATAR = "avatar"


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
            .getString(KEY_EMAIL, "leehan04@gmail.com") ?: "leehan04@gmail.com"
    }

    fun getAvatar(context: Context): Int {
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getInt(KEY_AVATAR, R.drawable.pfp_ava)
    }
}
