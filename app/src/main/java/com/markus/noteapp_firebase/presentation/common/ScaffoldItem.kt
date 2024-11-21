package com.markus.noteapp_firebase.presentation.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable

@Composable
fun ScaffoldItem(
    snackbarHost: @Composable () -> Unit,
    topBar: @Composable () -> Unit,
    fab: @Composable () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        snackbarHost = snackbarHost,
        topBar = topBar,
        floatingActionButton = fab,
        content = content
    )
}