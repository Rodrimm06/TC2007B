package com.example.tc2007b

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.tc2007b.navigation.SonrisasApp
import com.example.tc2007b.ui.theme.TC2007BTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION")
        window.statusBarColor = android.graphics.Color.rgb(229, 243, 251)
        @Suppress("DEPRECATION")
        window.navigationBarColor = android.graphics.Color.rgb(237, 244, 246)
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility =
            android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        setContent {
            TC2007BTheme(dynamicColor = false) {
                SonrisasApp()
            }
        }
    }
}
