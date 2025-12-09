package com.example.triviakids

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen() {

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Background gradient
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF7B61FF), // purple top
            Color(0xFFCDE9E9)  // light blue bottom
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(40.dp))

            // White Card
            Card(
                shape = RoundedCornerShape(25.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        text = "Login to your Account",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text("Username :")
                    TextField(
                        value = username,
                        onValueChange = { username = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 5.dp),
                        shape = RoundedCornerShape(15.dp),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFDDE7FF),
                            focusedContainerColor = Color(0xFFDDE7FF)
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text("Password :")
                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 5.dp),
                        shape = RoundedCornerShape(15.dp),
                        visualTransformation = PasswordVisualTransformation(),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFDDE7FF),
                            focusedContainerColor = Color(0xFFDDE7FF)
                        )
                    )

                    Spacer(modifier = Modifier.height(25.dp))

                    Button(
                        onClick = { /* TODO: handle login */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF7A85FF)
                        )
                    ) {
                        Text("LOGIN", fontSize = 18.sp, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            // Bottom text
            Row {
                Text("Doesn’t have an account?")
                Text(
                    " Sign up",
                    color = Color(0xFF4A56FF),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}