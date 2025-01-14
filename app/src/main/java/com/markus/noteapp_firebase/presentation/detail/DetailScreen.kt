package com.markus.noteapp_firebase.presentation.detail

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.markus.noteapp_firebase.presentation.util.Utils
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
@Composable
fun DetailScreen(
    detailViewModel: DetailViewModel = hiltViewModel(),
    noteId: String,
    onNavigate: () -> Unit
) {
    val detailUiState = detailViewModel.detailUiState

    val isFormsNotBlank = detailUiState.note.isNotBlank() && //check if the forms are blank
            detailUiState.title.isNotBlank()

    val selectedColor by animateColorAsState( //animation to change among colors
        targetValue = Utils.colors[detailUiState.colorIndex], label = ""
    )

    val isNoteIdNotBlank = noteId.isNotBlank()
    val icon = if (isNoteIdNotBlank) Icons.Default.Refresh
    else Icons.Default.Check

    LaunchedEffect(key1 = Unit) {
        if (isNoteIdNotBlank) {
            detailViewModel.getNote(noteId)
        } else {
            detailViewModel.resetState()
        }
    }
    BackHandler(
        enabled = isFormsNotBlank // Enable only if fields are not blank
    ) {
        if (isNoteIdNotBlank) {
            detailViewModel.updateNote(noteId)
        } else {
            detailViewModel.addNote()
        }
        onNavigate.invoke()
    }

    val scope = rememberCoroutineScope() //coroutineScope

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        floatingActionButton = {
            AnimatedVisibility(visible = isFormsNotBlank) {//helps hide the fab if usr has not typed anything
                FloatingActionButton(
                    onClick = {
                        if (isNoteIdNotBlank) {
                            detailViewModel.updateNote(noteId)
                        } else {
                            detailViewModel.addNote()
                        }
                        onNavigate.invoke()
                    }
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = selectedColor)
                .padding(paddingValues)
        ) {
            if (detailUiState.noteAddedStatus) {
                scope.launch {
                    snackbarHostState.showSnackbar("Note Added")
                    detailViewModel.resetNoteAddedStatus() //reset the state
                    onNavigate.invoke() //navigate back to home screen
                }
            }

            if (detailUiState.updateNoteStatus) {
                scope.launch {
                    snackbarHostState.showSnackbar("Note Updated")
                    detailViewModel.resetNoteAddedStatus()
                    onNavigate.invoke()
                }
            }

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                contentPadding = PaddingValues(
                    vertical = 16.dp,
                    horizontal = 8.dp
                )
            ) {
                itemsIndexed(Utils.colors) { colorIndex, color ->
                    ColorItem(color = color) {
                        detailViewModel.onColorChange(colorIndex)
                    }
                }
            }

            OutlinedTextField(
                value = detailUiState.title,
                onValueChange = { detailViewModel.onTitleChange(it) },
                label = {
                    Text(
                        text = "Title",
                        color = Color.Black
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                textStyle = MaterialTheme.typography.titleMedium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black
                )
            )

            OutlinedTextField(
                value = detailUiState.note,
                onValueChange = { detailViewModel.onNoteChange(it) },
                label = { Text(text = "Notes") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(8.dp),
                textStyle = MaterialTheme.typography.headlineSmall,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black
                )
            )
        }
    }
}

@Composable
fun ColorItem(
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        color = color,
        shape = CircleShape,
        modifier = Modifier
            .padding(8.dp)
            .size(36.dp)
            .clickable { onClick.invoke() },
        border = BorderStroke(2.dp, Color.Black)
    ) {

    }
}