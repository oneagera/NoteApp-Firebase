package com.markus.noteapp_firebase.presentation.trash

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.markus.noteapp_firebase.domain.model.Note
import com.markus.noteapp_firebase.domain.repository.Resource
import com.markus.noteapp_firebase.presentation.common.NavigationDrawer
import com.markus.noteapp_firebase.presentation.common.ScaffoldItem
import com.markus.noteapp_firebase.presentation.trash.components.TrashTopBarItem
import com.markus.noteapp_firebase.presentation.trash.components.TrashedNoteItem
import com.markus.noteapp_firebase.presentation.common.MultiSelectionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrashScreen(
    navigateToLoginPage: () -> Unit,
    navController: NavController,
    scope: CoroutineScope,
    drawerState: DrawerState,
    trashViewModel: TrashViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val trashUiState = trashViewModel.trashUiState
    val items = trashUiState.notesList.data ?: emptyList<Note>().filter { it.deleted }

    val multiSelectionManager = remember { MultiSelectionManager<String>() }

    var isMoreSectionVisible by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = Unit) {
        trashViewModel.loadNotes()
    }

    NavigationDrawer(
        navController = navController,
        scope = scope,
        drawerState = drawerState,
        navigateToLoginPage = {
            trashViewModel.signOut()
            navigateToLoginPage.invoke()
        },
        content = {
            ScaffoldItem(
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                topBar = {
                    if (multiSelectionManager.isMultiSelectionEnabled) {
                        TrashTopBarItem(
                            onDeleteClick = {
                                multiSelectionManager.getSelectedNoteCount()
                                multiSelectionManager.selectedNotes.forEach { noteId ->
                                    trashViewModel.deleteNotePermanently(noteId)
                                    multiSelectionManager.deselectAllNotes()
                                }
                                scope.launch {
                                    snackbarHostState.showSnackbar("note deleted permanently")
                                }
                            },
                            onRestoreClick = {
                                multiSelectionManager.getSelectedNoteCount()
                                multiSelectionManager.selectedNotes.forEach { noteId ->
                                    trashViewModel.restoreNote(noteId)
                                    multiSelectionManager.deselectAllNotes()
                                }
                                scope.launch {
                                    snackbarHostState.showSnackbar("note restored")
                                }
                            },
                            onCancel = { multiSelectionManager.deselectAllNotes() },
                            areAllItemsSelected = multiSelectionManager.areAllNotesSelected(items.map { it.noteId }),
                            count = multiSelectionManager.getSelectedNoteCount(),
                            selectAllItems = { multiSelectionManager.selectAllNotes(items.map { it.noteId }) },
                        )
                    } else {
                        TopAppBar(
                            title = { Text(text = "Trash") },
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
                                IconButton(onClick = {
                                    isMoreSectionVisible = !isMoreSectionVisible
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = null
                                    )
                                }
                                DropdownMenu(
                                    expanded = isMoreSectionVisible,
                                    onDismissRequest = {
                                        isMoreSectionVisible = false
                                    },
                                    offset = DpOffset(x = 0.dp, y = (-16).dp)
                                ) {
                                    DropdownMenuItem(
                                        text = { Text(text = "Empty trash") },
                                        onClick = {
                                            multiSelectionManager.selectAllNotes(items.filter { it.deleted }
                                                .map { it.noteId })
                                            multiSelectionManager.selectedNotes.forEach { noteId ->
                                                trashViewModel.deleteNotePermanently(noteId)
                                            }
                                            scope.launch {
                                                snackbarHostState.showSnackbar(
                                                    message =
                                                    if (multiSelectionManager.getSelectedNoteCount() > 1) {
                                                        "notes deleted permanently"
                                                    } else {
                                                        "note deleted permanently"
                                                    }
                                                )
                                            }
                                            isMoreSectionVisible = false
                                        }
                                    )
                                }
                            }
                        )
                    }
                },
                fab = { },
                content = { paddingValues ->
                    Column(modifier = Modifier.padding(paddingValues)) {
                        when (trashUiState.notesList) {
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

                                        TrashedNoteItem(
                                            note = note,
                                            onLongClick = {
                                                multiSelectionManager.toggleNoteSelection(note.noteId)
                                            },
                                            onClick = {
                                                if (multiSelectionManager.isMultiSelectionEnabled) {
                                                    multiSelectionManager.toggleNoteSelection(note.noteId)
                                                }
                                            },
                                            isSelected = isSelected
                                        )
                                    }
                                }
                            }

                            else -> {
                                Text(
                                    text = trashUiState.notesList.throwable?.localizedMessage
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