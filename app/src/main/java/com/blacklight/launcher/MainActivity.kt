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
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Ash Scale"
    )
    val ashAlpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Ash Alpha"
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

        // 2. THE ASH — Positioned in the DOCK area (where launcher icons sit)
        // This is the exact row where your Chrome / WhatsApp icons currently sit.
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 30.dp), // Sits just above the nav bar
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // THE ASH ICON (The single dock item)
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .scale(ashScale)
                    .alpha(ashAlpha)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.9f),
                                NeonPurple,
                                DeepPurple.copy(alpha = 0.6f),
                                Color.Transparent
                            )
                        )
                    )
                    .clickable {
                        // VIBRATE TO CONFIRM TAP
                        val vibrator = context.getSystemService(Vibrator::class.java)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
                        } else {
                            @Suppress("DEPRECATION")
                            vibrator.vibrate(50)
                        }
                        // Stage 2 will replace this with the Phoenix Rising
                    }
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