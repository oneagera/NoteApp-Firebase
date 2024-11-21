package com.markus.noteapp_firebase.presentation.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Timestamp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBarItem(
    onDeleteClick: () -> Unit,
    onCancel: () -> Unit,
    areAllItemsSelected: Boolean,
    count: Int,
    selectAllItems: () -> Unit,
    selectedNoteTimestamp: Timestamp?
) {
    var isMoreSectionVisible by remember {
        mutableStateOf(false)
    }

    var showInfoDialog by remember { mutableStateOf(false) }

    AnimatedVisibility(visible = true) {
        TopAppBar(
            title = { Text(text = "$count Selected") },
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
                if (count == 1) {
                    IconButton(onClick = { showInfoDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null
                        )
                    }
                }

                if (!areAllItemsSelected)
                    IconButton(onClick = { isMoreSectionVisible = !isMoreSectionVisible }
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = null
                        )
                    }
                AnimatedVisibility(visible = isMoreSectionVisible && !areAllItemsSelected) {
                    DropdownMenu(
                        expanded = isMoreSectionVisible,
                        onDismissRequest = {
                            isMoreSectionVisible = false
                        },
                        offset = DpOffset(x = 0.dp, y = (-16).dp)
                    ) {
                        DropdownMenuItem(
                            text = { Text(text = "Select all") },
                            onClick = {
                                selectAllItems.invoke()
                                isMoreSectionVisible = false
                            }
                        )
                    }
                }
            }
        )
    }
    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            confirmButton = {
                Button(onClick = { showInfoDialog = false }) {
                    Text("OK")
                }
            },
            title = { Text("Note Info") },
            text = {
                Text(
                    "Date Created: ${formatDate(selectedNoteTimestamp!!)}",
                    fontSize = 22.sp,
                    color = Color.White
                )
            }
        )
    }
}