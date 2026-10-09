package com.blacklight.launcher

import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import kotlin.math.cos
import kotlin.math.sin

// The Black-Light Colors
val NeonPurple = Color(0xFF9B00FF)
val ShiningSilver = Color(0xFFC0C0C0)
val DeepPurple = Color(0xFF1A0033)

data class AppInfo(val name: String, val packageName: String, val icon: android.graphics.Bitmap)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BlackLightLauncher()
        }
    }
}

@Composable
fun BlackLightLauncher() {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.toFloat()
    val screenHeight = configuration.screenHeightDp.toFloat()

    var showAppDrawer by remember { mutableStateOf(false) }
    var allApps by remember { mutableStateOf<List<AppInfo>>(emptyList()) }
    var currentBatch by remember { mutableStateOf(0) }

    val batchSize = 8

    // Load installed apps
    LaunchedEffect(Unit) {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val apps = pm.queryIntentActivities(intent, 0)
        allApps = apps.map { resolveInfo: ResolveInfo ->
            AppInfo(
                name = resolveInfo.loadLabel(pm).toString(),
                packageName = resolveInfo.activityInfo.packageName,
                icon = resolveInfo.loadIcon(pm).toBitmap(100, 100)
            )
        }.sortedBy { it.name }
    }

    // The Ash Pulse Animation
    val infiniteTransition = rememberInfiniteTransition(label = "Ash Pulse")
    val ashScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Ash Scale"
    )

    // The current batch of apps
    val currentApps = remember(currentBatch, allApps, showAppDrawer) {
        if (allApps.isEmpty()) emptyList()
        else {
            val start = currentBatch * batchSize
            allApps.drop(start).take(batchSize)
        }
    }

    val totalBatches = if (allApps.isEmpty()) 1 else (allApps.size + batchSize - 1) / batchSize

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // 1. THE VOID WALLPAPER
        Image(
            painter = painterResource(id = R.drawable.blacklight_wallpaper),
            contentDescription = "Black-Light Void",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 2. THE APPS RISING FROM THE ASH
        if (showAppDrawer && currentApps.isNotEmpty()) {
            val centerX = screenWidth / 2f
            val centerY = screenHeight * 0.55f
            val baseRadius = screenWidth * 0.35f

            currentApps.forEachIndexed { index, app ->
                // Position in a wing-like arc
                val count = currentApps.size
                val angle = if (count > 1) {
                    (Math.PI * (index.toFloat() / (count - 1)) - Math.PI / 2).toFloat()
                } else {
                    0f
                }
                val offsetX = baseRadius * cos(angle)
                val offsetY = baseRadius * sin(angle) * 0.6f

                val iconAlpha by animateFloatAsState(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 800, delayMillis = index * 50),
                    label = "Icon Alpha $index"
                )
                val iconScale by animateFloatAsState(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 800, delayMillis = index * 50),
                    label = "Icon Scale $index"
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .offset(
                            x = (centerX - screenWidth / 2f + offsetX).dp,
                            y = (centerY - screenHeight / 2f + offsetY).dp
                        )
                        .alpha(iconAlpha)
                        .scale(iconScale)
                        .clickable {
                            val launchIntent = context.packageManager.getLaunchIntentForPackage(app.packageName)
                            if (launchIntent != null) {
                                context.startActivity(launchIntent)
                            }
                        }
                ) {
                    Image(
                        bitmap = app.icon.asImageBitmap(),
                        contentDescription = app.name,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = app.name,
                        color = ShiningSilver,
                        fontSize = 9.sp,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        modifier = Modifier.width(70.dp)
                    )
                }
            }
        }

        // 3. THE ASH ICON (Bottom Center Dock)
        if (!showAppDrawer) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 40.dp)
                    .size(80.dp)
                    .scale(ashScale)
                    .clickable {
                        val vibrator = context.getSystemService(Vibrator::class.java)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
                        } else {
                            @Suppress("DEPRECATION")
                            vibrator.vibrate(50)
                        }
                        showAppDrawer = true
                        currentBatch = 0
                    },
                contentAlignment = Alignment.Center
            ) {
                // Silver Ring
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(
                            width = 2.dp,
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    ShiningSilver,
                                    Color.White,
                                    ShiningSilver,
                                    Color.Gray,
                                    ShiningSilver
                                )
                            ),
                            shape = CircleShape
                        )
                )
                // Purple Glow
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    NeonPurple.copy(alpha = 0.4f),
                                    DeepPurple.copy(alpha = 0.2f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )
                // Phoenix Ash Image
                Image(
                    painter = painterResource(id = R.drawable.phoenix_ash),
                    contentDescription = "Phoenix Ash",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                        .clip(CircleShape)
                )
            }
        }

        // 4. SWIPE GESTURE (when apps are shown)
        if (showAppDrawer) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(currentBatch, allApps) {
                        detectVerticalDragGestures { _, dragAmount ->
                            if (dragAmount < -50f) {
                                if (currentBatch < totalBatches - 1) {
                                    currentBatch++
                                }
                            } else if (dragAmount > 50f) {
                                showAppDrawer = false
                                currentBatch = 0
                            }
                        }
                    }
            )
        }

        // 5. Faint "BLACK-LIGHT" TEXT at the top
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