package com.example.feature.splash.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.septianbeneran.template.core.ui.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    onNavigateToLanding: () -> Unit = {}
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Center
    ) {
        LaunchedEffect(Unit) {
            delay(2000)
            onNavigateToLanding()
        }
        Image(
            modifier = modifier.wrapContentSize().align(Center).padding(horizontal = 24.dp),
            painter = painterResource(R.drawable.splash_logo),
            contentDescription = "Splash Logo"
        )
    }
}