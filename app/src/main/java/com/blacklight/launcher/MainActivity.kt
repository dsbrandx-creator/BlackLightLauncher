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
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    var showAppDrawer by remember { mutableStateOf(false) }
    var allApps by remember { mutableStateOf<List<AppInfo>>(emptyList()) }
    var currentBatch by remember { mutableStateOf(0) }
    
    // The swipe offset, used to animate the collapse
    var swipeOffset by remember { mutableStateOf(0f) }

    val batchSize = 8 // 8 apps per batch

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

    // The current batch of apps to display
    val currentApps = remember(currentBatch, allApps, showAppDrawer) {
        if (allApps.isEmpty()) emptyList()
        else {
            val start = currentBatch * batchSize
            allApps.drop(start).take(batchSize)
        }
    }
    
    // The total number of batches
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
            val centerX = screenWidth.value / 2
            val centerY = screenHeight.value * 0.75f // Ash position
            val baseRadius = screenWidth.value * 0.35f

            currentApps.forEachIndexed { index, app ->
                // Position apps in an arc (wing shape)
                val angle = (Math.PI * (index.toFloat() / (currentApps.size - 1).coerceAtLeast(1)) - Math.PI / 2).toFloat()
                val offsetX = (baseRadius * cos(angle)).dp
                val offsetY = (baseRadius * sin(angle) * 0.6f).dp

                // Animate each icon rising from the ash
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
                            x = (centerX.dp + offsetX) - (screenWidth / 2) + (screenWidth / 2) - (screenWidth / 2),
                            y = (centerY.dp + offsetY) - (screenHeight * 0.75f).dp + (screenHeight * 0.75f).dp
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

        // 3. THE ASH ICON WITH SILVER RING (Bottom Center Dock)
        if (!showAppDrawer) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 40.dp)
                    .size(80.dp)
                    .scale(ashScale)
                    .clickable {
                        // VIBRATE
                        val vibrator = context.getSystemService(Vibrator::class.java)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
                        } else {
                            @Suppress("DEPRECATION")
                            vibrator.vibrate(50)
                        }
                        // IGNITE THE PHOENIX
                        showAppDrawer = true
                        currentBatch = 0
                    },
                contentAlignment = Alignment.Center
            ) {
                // The Silver Ring
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

        // 4. THE SWIPE GESTURE (Only active when apps are shown)
        if (showAppDrawer) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(currentBatch, allApps) {
                        detectVerticalDragGestures { _, dragAmount ->
                            if (dragAmount < -50f) {
                                // SWIPE UP: Next batch
                                if (currentBatch < totalBatches - 1) {
                                    currentBatch++
                                }
                            } else if (dragAmount > 50f) {
                                // SWIPE DOWN: Collapse
                                showAppDrawer = false
                                currentBatch = 0
                            }
                        }
                    }
            )
        }

        // 5. TAP TO COLLAPSE (On empty space when apps are shown)
        if (showAppDrawer) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        showAppDrawer = false
                        currentBatch = 0
                    }
            )
        }
    }
}