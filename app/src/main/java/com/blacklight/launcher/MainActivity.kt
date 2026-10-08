package com.blacklight.launcher

import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
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
val ObsidianBlack = Color(0xFF050505)
val DeepPurple = Color(0xFF1A0033)
val ShiningSilver = Color(0xFFE0E0E0)
val NeonPurple = Color(0xFF9B00FF)

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
    var installedApps by remember { mutableStateOf<List<AppInfo>>(emptyList()) }
    
    // The radius of the circle. Swiping up increases this.
    var radiusMultiplier by remember { mutableStateOf(1f) }
    
    // Animation state for the Phoenix rising
    val phoenixScale by animateFloatAsState(
        targetValue = if (showAppDrawer) 0.4f else 1f,
        animationSpec = tween(durationMillis = 800),
        label = "Phoenix Scale"
    )
    val wingAlpha by animateFloatAsState(
        targetValue = if (showAppDrawer) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "Wing Alpha"
    )

    // Load installed apps
    LaunchedEffect(Unit) {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val apps = pm.queryIntentActivities(intent, 0)
        installedApps = apps.map { resolveInfo: ResolveInfo ->
            AppInfo(
                name = resolveInfo.loadLabel(pm).toString(),
                packageName = resolveInfo.activityInfo.packageName,
                icon = resolveInfo.loadIcon(pm).toBitmap(100, 100)
            )
        }.sortedBy { it.name }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(DeepPurple, ObsidianBlack),
                    radius = 1500f
                )
            )
            // Swipe up/down gesture to expand/contract the circle
            .pointerInput(showAppDrawer) {
                if (showAppDrawer) {
                    detectVerticalDragGestures { _, dragAmount ->
                        // dragAmount is negative when swiping up, positive when down
                        val newMultiplier = radiusMultiplier - (dragAmount / 500f)
                        radiusMultiplier = newMultiplier.coerceIn(0.8f, 2.0f)
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        
        // THE WING CIRCLE (The Phoenix's Aura)
        if (wingAlpha > 0f) {
            Box(
                modifier = Modifier
                    .size((screenWidth * 0.9f) * radiusMultiplier)
                    .alpha(wingAlpha)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(Color.Transparent, NeonPurple.copy(alpha = 0.15f), Color.Transparent)
                        ),
                        shape = CircleShape
                    )
            )
        }

        // THE INSTALLED APPS - Positioned Radially
        if (showAppDrawer) {
            val totalApps = installedApps.size
            val baseRadius = (screenWidth.value * 0.35f) * radiusMultiplier
            
            installedApps.forEachIndexed { index, app ->
                // Calculate the angle for each app (spread evenly around the circle)
                val angle = (2 * Math.PI * index / totalApps).toFloat()
                
                // Calculate X and Y offsets using polar coordinates
                val offsetX = (baseRadius * cos(angle)).dp
                val offsetY = (baseRadius * sin(angle)).dp
                
                // Animate each icon flying out from the center
                val iconAlpha by animateFloatAsState(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 800, delayMillis = index * 30),
                    label = "Icon Alpha $index"
                )
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .offset(x = offsetX, y = offsetY)
                        .alpha(iconAlpha)
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
                            .background(ObsidianBlack)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = app.name,
                        color = ShiningSilver,
                        fontSize = 9.sp,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        modifier = Modifier.width(60.dp)
                    )
                }
            }
        }

        // THE PHOENIX FOUNDATION
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Image(
                painter = painterResource(id = R.drawable.phoenix_glowing_silver),
                contentDescription = "Black-Light Phoenix",
                modifier = Modifier
                    .size(300.dp)
                    .alpha(phoenixScale)
                    .clickable { showAppDrawer = !showAppDrawer }
            )
            
            if (!showAppDrawer) {
                Text(
                    text = "BLACK-LIGHT",
                    color = ShiningSilver,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 6.sp
                )
                Text(
                    text = "THE CHIEF REBELLION OFFICER",
                    color = NeonPurple,
                    fontSize = 12.sp,
                    letterSpacing = 3.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
        
        // The Footer Text
        if (showAppDrawer) {
            Text(
                text = "666 ANTI-DOGMA BATTALION • SWIPE TO EXPAND",
                color = NeonPurple,
                fontSize = 9.sp,
                letterSpacing = 2.sp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 40.dp)
            )
        }
    }
}