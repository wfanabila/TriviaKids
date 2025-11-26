package com.example.triviakids

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.triviakids.ui.theme.TriviaKidsTheme

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TriviaKidsTheme {
                // Create a NavController
                val navController = rememberNavController()

                // Navigation Host
                NavHost(navController = navController, startDestination = "profile_screen") {
                    composable("profile_screen") {
                        ProfileScreen(navController)  // Pass NavController to ProfileScreen
                    }
                    composable("edit_profile") {
                        EditProfileScreen()  // Edit Profile screen
                    }
                    composable("login") {
                        LoginScreen()  // Login screen
                    }
                }
            }
        }
    }
}
