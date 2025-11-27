package com.example.triviakids

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.triviakids.ui.theme.TriviaKidsTheme

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TriviaKidsTheme {
                val navController = rememberNavController()
                ProfileScreen(navController = navController)  // Calling the ProfileScreen composable
            }
        }
    }
}
