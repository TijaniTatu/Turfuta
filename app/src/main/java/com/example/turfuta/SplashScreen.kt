package com.example.turfuta

import androidx.compose.animation.core.*
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    // Duration for which the splash screen will be shown
    val splashDuration = 3000L

    LaunchedEffect(Unit) {
        // Play the animation for a set duration, then call onTimeout to navigate
        delay(splashDuration)
        onTimeout()
    }

    Image(
        painter = painterResource(id = R.drawable.foot_ball),
        contentDescription = "Splash Animation",
        modifier = Modifier.fillMaxSize(),
        alignment = Alignment.Center
    )
}
