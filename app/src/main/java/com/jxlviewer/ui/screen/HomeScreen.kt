package com.jxlviewer.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.jxlviewer.R

/**
 * Launcher home: the gallery was removed, the only entry point is the system
 * file picker (SAF). Opening from other apps still goes through the
 * ACTION_VIEW intent filters to [ImageView].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onFilePicked: (Uri) -> Unit) {
    val openFileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            onFilePicked(uri)
        }
    }

    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = {
            Text(stringResource(R.string.app_name))
        })
    }) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Button(onClick = {
                openFileLauncher.launch(arrayOf("image/jxl"))
            }) {
                Text(stringResource(R.string.open_jxl_file))
            }
        }
    }
}
