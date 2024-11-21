package com.markus.noteapp_firebase.presentation.trash

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.markus.noteapp_firebase.domain.repository.Resource
import com.markus.noteapp_firebase.domain.repository.StorageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrashViewModel @Inject constructor(
    private val repository: StorageRepository
) : ViewModel() {
    var trashUiState by mutableStateOf(TrashUiState())

    val hasUser: Boolean
        get() = repository.hasUser()

    private val userId: String
        get() = repository.getUserId()

    init {
        loadNotes()
    }

    fun loadNotes() {
        if (hasUser) {
            if (userId.isNotBlank()) {
                getTrashedUserNotes()
            }
        } else {
            trashUiState = trashUiState.copy(
                notesList = Resource.Error(
                    throwable = Throwable(message = "User is not logged in")
                )
            )
        }
    }

    private fun getTrashedUserNotes() = viewModelScope.launch {
        repository.getTrashedUserNotes(userId).collect {
            trashUiState = trashUiState.copy(notesList = it)
        }
    }

    fun restoreNote(noteId: String) {
        repository.restoreNote(noteId) { success ->
            if (success) getTrashedUserNotes()
        }
    }

    fun deleteNotePermanently(noteId: String) = repository.deleteNote(noteId) {
        trashUiState = trashUiState.copy(noteDeletedStatus = it)
    }

    fun signOut() = repository.signOut()
}

