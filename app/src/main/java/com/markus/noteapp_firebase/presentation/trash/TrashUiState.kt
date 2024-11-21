package com.markus.noteapp_firebase.presentation.trash

import com.markus.noteapp_firebase.domain.model.Note
import com.markus.noteapp_firebase.domain.repository.Resource

data class TrashUiState(
    val notesList: Resource<List<Note>> = Resource.Loading(),
    val noteDeletedStatus: Boolean = false
)
