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
import androidx.compose.foundation.shape.RoundedCornerShape
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
val ObsidianBlack = Color(0xFF050505)
val NeonPurple = Color(0xFF9B00FF)
val DeepPurple = Color(0xFF1A0033)
val EmberOrange = Color(0xFFB026FF)

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
        initialValue = 0.6f,
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

        // 2. THE ASH HEAP (Bottom Center, resting on the nav bar)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(120.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            // Layer 1: The Base Ash (Widest, darkest)
            Box(
                modifier = Modifier
                    .width(180.dp)
                    .height(40.dp)
                    .alpha(ashAlpha * 0.4f)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                DeepPurple,
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(topStart = 100.dp, topEnd = 100.dp)
                    )
            )

            // Layer 2: The Glowing Embers (Middle)
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(30.dp)
                    .alpha(ashAlpha * 0.7f)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                NeonPurple.copy(alpha = 0.8f),
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(topStart = 100.dp, topEnd = 100.dp)
                    )
            )

            // Layer 3: The Core Fire (Smallest, brightest, pulsing)
            Box(
                modifier = Modifier
                    .width(60.dp * ashScale)
                    .height(20.dp * ashScale)
                    .alpha(ashAlpha)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.9f),
                                EmberOrange.copy(alpha = 0.8f),
                                NeonPurple.copy(alpha = 0.5f),
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(topStart = 100.dp, topEnd = 100.dp)
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