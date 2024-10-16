package com.markus.noteapp_firebase.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarItem(
    onDeleteClick: () -> Unit,
    onMoreClick: () -> Unit,
    onCancel: () -> Unit,
    isVisible: Boolean
) {
    AnimatedVisibility(visible = isVisible) {
        TopAppBar(
            title = { Text(text = "Selected") },
            navigationIcon = {
                IconButton(onClick = { onCancel.invoke() }
                ) {
                    Icon(imageVector = Icons.Default.Cancel, contentDescription = null)
                }
            },
            actions = {
                IconButton(
                    onClick = { onDeleteClick.invoke() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null
                    )
                }
                IconButton(onClick = { onMoreClick.invoke() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null
                    )
                }
            }
        )
    }

}