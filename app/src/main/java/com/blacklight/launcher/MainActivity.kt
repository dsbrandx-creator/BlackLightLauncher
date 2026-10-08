package com.blacklight.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// The Black-Light Colors
val ObsidianBlack = Color(0xFF050505)
val ShiningSilver = Color(0xFFE0E0E0)
val NeonPurple = Color(0xFF9B00FF)

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
    // This is the state: when false, we show the Phoenix. When true, we show the App Drawer.
    var showAppDrawer by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack) // The Void Background
    ) {
        if (!showAppDrawer) {
            // THE PHOENIX FOUNDATION
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                // Your Shining Silver Phoenix Image
                Image(
                    painter = painterResource(id = R.drawable.phoenix_glowing_silver),
                    contentDescription = "Black-Light Phoenix",
                    modifier = Modifier
                        .size(250.dp)
                        .clickable { 
                            // When tapped, the Phoenix "spreads its wings" to reveal the apps
                            showAppDrawer = true 
                        }
                )

                Spacer(modifier = Modifier.height(40.dp))

                // The Black-Light Text
                Text(
                    text = "BLACK-LIGHT",
                    color = ShiningSilver,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp
                )
                Text(
                    text = "THE CHIEF REBELLION OFFICER",
                    color = NeonPurple,
                    fontSize = 12.sp,
                    letterSpacing = 2.sp
                )
            }
        } else {
            // THE APP DRAWER (Revealed when Phoenix is tapped)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "BLACK-LIGHT",
                    color = ShiningSilver,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 40.dp)
                )

                // This is where your apps will eventually go
                Text(
                    text = "App Drawer Initialized",
                    color = NeonPurple,
                    fontSize = 16.sp
                )
                Text(
                    text = "666 ANTI-DOGMA BATTALION",
                    color = ShiningSilver,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(top = 20.dp)
                )

                // A button to go back to the Phoenix
                Text(
                    text = "RETURN TO PHOENIX",
                    color = NeonPurple,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .padding(top = 60.dp)
                        .clickable { showAppDrawer = false }
                )
            }
        }
    }
}