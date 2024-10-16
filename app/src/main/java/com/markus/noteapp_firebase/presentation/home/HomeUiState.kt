package com.markus.noteapp_firebase.presentation.home

import com.markus.noteapp_firebase.domain.model.Note
import com.markus.noteapp_firebase.domain.repository.Resource

data class HomeUiState(
    val notesList: Resource<List<Note>> = Resource.Loading(),
    val noteDeletedStatus: Boolean = false
)
