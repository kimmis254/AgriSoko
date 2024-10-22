package com.example.agrisoko2.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.agrisoko2.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Display the app icon or a loading animation
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground), // Replace with your app icon or logo
            contentDescription = "App Icon",
            modifier = Modifier.size(150.dp)
        )
    }

    LaunchedEffect(Unit) {
        delay(2000) // 2 seconds delay
        navController.navigate("check_user_status") {
            popUpTo("splash_screen") { inclusive = true }
        }
    }
}
