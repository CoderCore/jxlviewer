package com.jxlviewer.ui.screen

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.jxlviewer.ui.nav.Home
import com.jxlviewer.ui.nav.ImageView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(initialDestination: NavKey) {
    val backStack = rememberNavBackStack(initialDestination)

    NavDisplay(
        backStack = backStack,
        entryDecorators =
        listOf(
            // Add the default decorators for managing scenes and saving state
            rememberSaveableStateHolderNavEntryDecorator(),
            // Then add the view model store decorator
            rememberViewModelStoreNavEntryDecorator()
        ),
        onBack = {
            backStack.removeAt(backStack.lastIndex)
        },
        predictivePopTransitionSpec = {
            EnterTransition.None togetherWith
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(200)
                )
        },
        entryProvider =
        entryProvider {
            entry<Home> {
                HomeScreen(
                    onFilePicked = { uri ->
                        backStack.add(ImageView(uri.toString()))
                    }
                )
            }
            entry<ImageView> { entry ->
                ViewerScreen(entry.uri.toUri())
            }
        }
    )
}
