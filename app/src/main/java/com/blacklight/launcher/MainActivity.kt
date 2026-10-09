package com.blacklight.launcher

import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// The Black-Light Colors
val NeonPurple = Color(0xFF9B00FF)
val DeepPurple = Color(0xFF1A0033)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BlackLightVoid()
        }
    }
}

@Composable
fun BlackLightVoid() {
    val context = LocalContext.current

    // The Ash Pulse Animation
    val infiniteTransition = rememberInfiniteTransition(label = "Ash Pulse")
    val ashScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Ash Scale"
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 1. THE VOID WALLPAPER
        Image(
            painter = painterResource(id = R.drawable.blacklight_wallpaper),
            contentDescription = "Black-Light Void",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 2. THE ASH ICON (Bottom Center Dock)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp) // Sits just above the nav bar
                .size(80.dp)
                .scale(ashScale)
                .clip(CircleShape)
                .clickable {
                    // VIBRATE TO CONFIRM TAP
                    val vibrator = context.getSystemService(Vibrator::class.java)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(50)
                    }
                }
        ) {
            // The Phoenix Ash Image
            Image(
                painter = painterResource(id = R.drawable.phoenix_ash),
                contentDescription = "Phoenix Ash",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // A subtle purple glow on top of the image
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.Transparent,
                                NeonPurple.copy(alpha = 0.2f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        // 3. Faint "BLACK-LIGHT" TEXT at the top
        Text(
            text = "BLACK-LIGHT",
            color = Color.White.copy(alpha = 0.15f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 8.sp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 60.dp)
        )
    }
}