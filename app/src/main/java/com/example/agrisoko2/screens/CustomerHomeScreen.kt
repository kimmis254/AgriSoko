package com.example.agrisoko2.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.agrisoko2.ui.components.AnimatedGradientBackground
import com.example.agrisoko2.ui.components.StylishAppIconWithGradient

@Composable
fun CustomerHomeScreen() {
       Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            StylishAppIconWithGradient(size = 40.dp)
            Text(text = "Welcome Customer")
            // Add customer-specific UI elements here
        }
    }
