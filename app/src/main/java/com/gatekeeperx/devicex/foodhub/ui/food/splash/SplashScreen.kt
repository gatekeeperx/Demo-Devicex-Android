package com.gatekeeperx.devicex.foodhub.ui.food.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gatekeeperx.devicex.foodhub.R
import com.gatekeeperx.devicex.foodhub.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit = {}
) {
    var startAnimation by remember { mutableStateOf(false) }

    // Animation values
    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "alpha"
    )

    val scaleAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.5f,
        animationSpec = tween(
            durationMillis = 1000,
            easing = FastOutSlowInEasing
        ),
        label = "scale"
    )

    // Loading dots animation
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    val dot1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )
    val dot2Alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2"
    )
    val dot3Alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3"
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(2500) // Show splash for 2.5 seconds
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo/Icon - using a circular food image
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .scale(scaleAnim.value)
                    .alpha(alphaAnim.value)
                    .background(PrimaryBeige, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_noodles),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(120.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // App Name
            Text(
                text = "FoodHub",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.alpha(alphaAnim.value)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tagline
            Text(
                text = "Delicious food, delivered fast",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = TextSecondary,
                modifier = Modifier.alpha(alphaAnim.value)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Loading dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.alpha(alphaAnim.value)
            ) {
                LoadingDot(alpha = dot1Alpha)
                LoadingDot(alpha = dot2Alpha)
                LoadingDot(alpha = dot3Alpha)
            }
        }
    }
}

@Composable
fun LoadingDot(alpha: Float) {
    Box(
        modifier = Modifier
            .size(12.dp)
            .alpha(alpha)
            .background(PrimaryGreen, CircleShape)
    )
}

@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
fun SplashScreenPreview() {
    FoodDeliveryTheme {
        SplashScreen()
    }
}
