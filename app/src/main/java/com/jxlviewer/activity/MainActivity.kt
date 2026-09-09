package com.jxlviewer.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import com.jxlviewer.ui.nav.Home
import com.jxlviewer.ui.nav.ImageView
import com.jxlviewer.ui.screen.MainScreen
import com.jxlviewer.ui.theme.AppTheme


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // HDR window mode, set once for the whole activity and never toggled at
        // runtime (a runtime color-mode switch makes SurfaceFlinger drop
        // adaptive-refresh displays into a ~15Hz compatibility state, which makes
        // panning huge images janky). COLOR_MODE_HDR (API 31+) composites
        // RGBA_F16/PQ buffers through the hardware HDR pipeline, so decoded
        // BT.2020+PQ bitmaps keep their absolute luminance and the panel boosts;
        // WIDE_COLOR_GAMUT on API 26-30. Unsupported values are clamped by the
        // platform; minSdk 23 has no setColorMode at all.
        // Base window state = wide color gamut (never HDR at creation), matching
        // the system gallery: ViewerScreen flips the color mode per image
        // (HDR for real HDR content, WCG otherwise) and restores WCG on exit.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.colorMode = ActivityInfo.COLOR_MODE_WIDE_COLOR_GAMUT
        }
        // API 35: request EDR headroom unconditionally (the gallery keeps
        // desiredHdrHeadroom set even in WCG mode); the panel's HDR session
        // only activates when the mode is HDR AND headroom > 1.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            window.attributes.desiredHdrHeadroom = 4.926f
        }
        enableEdgeToEdge(
            navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT)
        )

        val initialDestination = getIntentDestination() ?: Home

        setContent {
            AppTheme {
                MainScreen(initialDestination)
            }
        }
    }

    private fun getIntentDestination(): ImageView? {
        val intent = this.intent ?: return null
        return if (intent.action == Intent.ACTION_VIEW) {
            val intentData = this.intent?.data
            if (intentData != null) {
                ImageView(intentData.toString())
            } else {
                null
            }
        } else {
            null
        }
    }
}
