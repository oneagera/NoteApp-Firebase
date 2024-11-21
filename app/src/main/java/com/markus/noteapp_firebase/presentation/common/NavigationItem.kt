package com.markus.noteapp_firebase.presentation.common

import androidx.compose.ui.graphics.vector.ImageVector

data class NavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String,
    val onClick: () -> Unit
)
