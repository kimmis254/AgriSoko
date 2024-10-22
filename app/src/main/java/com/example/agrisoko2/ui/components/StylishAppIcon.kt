package com.example.agrisoko2.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.agrisoko2.R

@Composable
fun StylishAppIcon(
    modifier: Modifier = Modifier,
    size: Dp,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    shadowElevation: Dp = 4.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .shadow(shadowElevation, shape = CircleShape, clip = true)
            .background(backgroundColor, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_logo),
            contentDescription = "App Icon",
            modifier = Modifier.size(size * 0.6f),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun StylishAppIconWithGradient(
    modifier: Modifier = Modifier,
    size: Dp,
    shadowElevation: Dp = 6.dp
) {
    val gradient = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary
        )
    )

    Box(
        modifier = modifier
            .size(size)
            .shadow(shadowElevation, shape = CircleShape, clip = true)
            .background(brush = gradient, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_logo),
            contentDescription = "App Icon",
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun AnimatedStylishAppIcon(
    modifier: Modifier = Modifier,
    size: Dp,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    shadowElevation: Dp = 6.dp
) {
    var isVisible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isVisible) 1f else 0.5f)
    val alpha by animateFloatAsState(if (isVisible) 1f else 0f)

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Box(
        modifier = modifier
            .size(size)
            .shadow(shadowElevation, shape = CircleShape, clip = true)
            .background(backgroundColor, shape = CircleShape)
            .graphicsLayer(scaleX = scale, scaleY = scale, alpha = alpha),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_logo),
            contentDescription = "App Icon",
            modifier = Modifier.size(size * 0.6f),
            contentScale = ContentScale.Fit
        )
    }
}
