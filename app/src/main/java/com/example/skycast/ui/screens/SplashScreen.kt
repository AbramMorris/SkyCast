package com.example.skycast.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.skycast.R
import com.example.skycast.ui.navigation.ScreenRoute
import com.example.skycast.ui.theme.BlueBlack
import com.example.skycast.ui.theme.BlueLight
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {

    LaunchedEffect(Unit) {
        delay(2000)
        navController.navigate("home") {
            popUpTo(ScreenRoute.Splash.route) { inclusive = true }
        }
    }

    val backgroundColor = if (isSystemInDarkTheme()) BlueBlack else BlueLight

    val backgroundLottie = rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.splashlottie))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // 🎞️ Lottie Animation
            LottieAnimation(
                composition = backgroundLottie.value,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier.fillMaxSize()
            )
        }
        Text(
            text = "SkyCast",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
            ,modifier = Modifier
                .graphicsLayer {
                    translationY = 100f
                    alpha = 0.5f
                }

        )
    }
}



