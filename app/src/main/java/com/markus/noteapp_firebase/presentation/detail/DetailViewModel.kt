package com.markus.noteapp_firebase.presentation.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.markus.noteapp_firebase.domain.model.Note
import com.markus.noteapp_firebase.domain.repository.StorageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: StorageRepository
) : ViewModel() {
    var detailUiState by mutableStateOf(DetailUiState())
        private set

    //check if repo has user
    private val hasUser: Boolean
        get() = repository.hasUser()

    //get the user
    private val user: FirebaseUser?
        get() = repository.user()

    fun onColorChange(colorIndex: Int) {
        detailUiState = detailUiState.copy(colorIndex = colorIndex)
    }

    fun onTitleChange(title: String) {
        detailUiState = detailUiState.copy(title = title)
    }

    fun onNoteChange(note: String) {
        detailUiState = detailUiState.copy(note = note)
    }

    fun addNote() {
        if (hasUser) {
            repository.addNote(
                userId = user!!.uid,
                title = detailUiState.title,
                description = detailUiState.note,
                color = detailUiState.colorIndex,
                timestamp = Timestamp.now()
            ) {
                detailUiState = detailUiState.copy(noteAddedStatus = it)
            }
        }
    }

    private fun setEditFields(note: Note) {
        detailUiState = detailUiState.copy(
            colorIndex = note.colorIndex,
            title = note.title,
            note = note.description
        )
    }

    fun getNote(noteId: String) {
        repository.getNote(
            noteId = noteId,
            onError = { }
        ) {
            detailUiState = detailUiState.copy(selectedNote = it)
            detailUiState.selectedNote?.let { it1 -> setEditFields(it1) }
        }
    }

    fun updateNote(noteId: String) {
        repository.updateNote(
            title = detailUiState.title,
            description = detailUiState.note,
            color = detailUiState.colorIndex,
            noteId = noteId
        ) {
            detailUiState = detailUiState.copy(updateNoteStatus = it)
        }
    }

    fun resetNoteAddedStatus() {
        detailUiState = detailUiState.copy(
            noteAddedStatus = false,
            updateNoteStatus = false
        )
    }

    //reset the whole state back to normal
    fun resetState() {
        detailUiState = DetailUiState()
    }
}