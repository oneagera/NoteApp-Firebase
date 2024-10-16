package com.markus.noteapp_firebase.presentation.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.markus.noteapp_firebase.domain.model.Note
import com.markus.noteapp_firebase.domain.repository.Resource
import com.markus.noteapp_firebase.ui.theme.NoteAppFirebaseTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel?,
    onNoteClick: (id: String) -> Unit,
    navigateToDetailPage: () -> Unit,
    navigateToLoginPage: () -> Unit
) {
    val homeUiState = homeViewModel?.homeUiState ?: HomeUiState()

    var selectedNote: Note? by remember {
        mutableStateOf(null)
    }

    var isVisible by remember {
        mutableStateOf(false)
    }

    var items by remember {
        mutableStateOf(
            (homeUiState.notesList.data ?: emptyList()).map { note ->
                note to false  // Pair each note with an isSelected Boolean
            }
        )
    }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = Unit) {
        homeViewModel?.loadNotes()

    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigateToDetailPage.invoke() }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
        },
        topBar = {
            if (isVisible) {
                TopAppBarItem(
                    onDeleteClick = { selectedNote?.noteId?.let { homeViewModel?.deleteNote(it) } },
                    onMoreClick = {  },
                    onCancel = { isVisible = false },
                    isVisible = true
                )
            } else {
                TopAppBar(
                    navigationIcon = {

                    },
                    actions = {
                        IconButton(
                            onClick = {
                                homeViewModel?.signOut()
                                navigateToLoginPage.invoke()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = null
                            )
                        }
                    },
                    title = { Text(text = "Home") }
                )
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            when (homeUiState.notesList) {
                is Resource.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(align = Alignment.Center)
                    )
                }

                is Resource.Success -> {
                    LazyVerticalStaggeredGrid( //OPTIONS: LazyVerticalGrid, LazyHorizontalGrid
                        columns = StaggeredGridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(homeUiState.notesList.data ?: emptyList()) { note ->
                            NoteItem(
                                note = note,
                                onLongClick = {
                                    isVisible = true
                                    selectedNote = note
                                },
                                onClick = {
                                    scope.launch { delay(100) }
                                    onNoteClick.invoke(note.noteId) },
                                onDeleteClick = { homeViewModel?.deleteNote(note.noteId) }
                            )
                        }
                    }
                }

                else -> {
                    Text(
                        text = homeUiState.notesList.throwable?.localizedMessage ?: "Unknown Error",
                        color = Color.Red
                    )
                }
            }
        }
    }
    LaunchedEffect(key1 = homeViewModel?.hasUser) {
        if (homeViewModel?.hasUser == false) {
            navigateToLoginPage.invoke()
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    NoteAppFirebaseTheme {
        HomeScreen(
            homeViewModel = null,
            onNoteClick = {},
            navigateToDetailPage = {},
            navigateToLoginPage = {}
        )
    }
}
