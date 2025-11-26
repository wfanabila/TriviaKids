package com.example.triviakids

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ProfileScreen(navController: NavController) {
    val username = "Leehan"
    val email = "leehan@example.com"
    val score = 100

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), // add padding kat sini....
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // pfp
        Image(
            painter = painterResource(id = R.drawable.pfp),
            contentDescription = "Profile Image",
            modifier = Modifier
                .size(120.dp) // size profile
                .padding(bottom = 16.dp)
                .clip(CircleShape)
        )

        Text(text = "Username: $username", modifier = Modifier.padding(bottom = 8.dp))
        Text(text = "Email: $email", modifier = Modifier.padding(bottom = 8.dp))
        Text(text = "Total Score: $score", modifier = Modifier.padding(bottom = 16.dp))

        // edit profile
        Button(
            onClick = {
                navController.navigate("edit_profile")  // Navigate to EditProfileScreen
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)  // Full width button
        ) {
            Text("Edit Profile")
        }

        // logout
        Button(
            onClick = {
                navController.navigate("login")  // Navigate to login
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log Out")
        }
    }
}
