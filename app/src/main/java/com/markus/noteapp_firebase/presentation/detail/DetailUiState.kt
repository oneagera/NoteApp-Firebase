package com.markus.noteapp_firebase.presentation.detail

import com.markus.noteapp_firebase.domain.model.Note

data class DetailUiState(
    val colorIndex: Int = 0,
    val title: String = "",
    val note: String = "",
    val noteAddedStatus: Boolean = false,
    val updateNoteStatus: Boolean = false,
    val selectedNote: Note? = null
)
