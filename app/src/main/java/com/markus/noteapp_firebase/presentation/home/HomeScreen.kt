package com.markus.noteapp_firebase.presentation.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.markus.noteapp_firebase.domain.repository.Resource
import com.markus.noteapp_firebase.presentation.common.NavigationDrawer
import com.markus.noteapp_firebase.presentation.common.ScaffoldItem
import com.markus.noteapp_firebase.presentation.home.components.HomeTopAppBarItem
import com.markus.noteapp_firebase.presentation.home.components.NoteItem
import com.markus.noteapp_firebase.presentation.common.MultiSelectionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    onNoteClick: (id: String) -> Unit,
    navigateToDetailPage: () -> Unit,
    navigateToLoginPage: () -> Unit,
    scope: CoroutineScope,
    drawerState: DrawerState,
    navController: NavController
) {
    LaunchedEffect(key1 = Unit) {
        homeViewModel.loadNotes()
    }

    val homeUiState = homeViewModel.homeUiState

    val items = homeUiState.notesList.data ?: emptyList()

    val multiSelectionManager = remember { MultiSelectionManager<String>() }

    val snackbarHostState = remember { SnackbarHostState() }

    BackHandler(
        enabled = multiSelectionManager.getSelectedNoteCount() > 0
    ) {
        multiSelectionManager.deselectAllNotes() // Deselect all items on back press
    }

    NavigationDrawer(
        navController = navController,
        scope = scope,
        drawerState = drawerState,
        navigateToLoginPage = {
            homeViewModel.signOut()
            navigateToLoginPage.invoke()
        },
        content = {
            ScaffoldItem(
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                topBar = {
                    if (multiSelectionManager.isMultiSelectionEnabled) {
                        HomeTopAppBarItem(
                            count = multiSelectionManager.getSelectedNoteCount(),
                            onDeleteClick = {
                                multiSelectionManager.selectedNotes.forEach { noteId ->
                                    homeViewModel.softDeleteNote(noteId)
                                }
                                !multiSelectionManager.isMultiSelectionEnabled
                                scope.launch {
                                    snackbarHostState.showSnackbar("note deleted")
                                }
                            },
                            onCancel = { multiSelectionManager.deselectAllNotes() },
                            areAllItemsSelected = multiSelectionManager.areAllNotesSelected(items.map { it.noteId }),
                            selectAllItems = { multiSelectionManager.selectAllNotes(items.map { it.noteId }) },
                            selectedNoteTimestamp = items.find { it.noteId == multiSelectionManager.getSingleSelectedNote() }?.timestamp
                        )
                    } else {
                        TopAppBar(
                            navigationIcon = {
                                IconButton(
                                    onClick = {
                                        scope.launch { drawerState.open() }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = null
                                    )
                                }
                            },
                            actions = {
                                IconButton(
                                    onClick = {
                                        scope.launch {
                                            snackbarHostState.showSnackbar(message = "Sort function under construction.")
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Sort,
                                        contentDescription = null
                                    )
                                }
                            },
                            title = { Text(text = "Notes") }
                        )
                    }
                },
                fab = {
                    FloatingActionButton(
                        onClick = { navigateToDetailPage.invoke() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null
                        )
                    }
                },
                content = { paddingValues ->
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
                                LazyVerticalStaggeredGrid(
                                    columns = StaggeredGridCells.Fixed(2),
                                    contentPadding = PaddingValues(16.dp)
                                ) {
                                    items(items) { note ->
                                        val isSelected =
                                            multiSelectionManager.selectedNotes.contains(note.noteId)

                                        NoteItem(
                                            note = note,
                                            onLongClick = {
                                                multiSelectionManager.toggleNoteSelection(note.noteId)
                                            },
                                            onClick = {
                                                if (multiSelectionManager.isMultiSelectionEnabled) {
                                                    multiSelectionManager.toggleNoteSelection(note.noteId)
                                                } else {
                                                    onNoteClick.invoke(note.noteId)
                                                }
                                            },
                                            isSelected = isSelected
                                        )
                                    }
                                }
                            }

                            else -> {
                                Text(
                                    text = homeUiState.notesList.throwable?.localizedMessage
                                        ?: "Unknown Error",
                                    color = Color.Red
                                )
                            }
                        }
                    }
                }
            )
        }
    )
}