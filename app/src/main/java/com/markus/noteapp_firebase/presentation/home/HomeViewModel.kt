package com.markus.noteapp_firebase.presentation.home

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
class HomeViewModel @Inject constructor(
    val repository: StorageRepository
) : ViewModel() {

    var homeUiState by mutableStateOf(HomeUiState())

    val hasUser: Boolean
        get() = repository.hasUser()
    private val userId: String
        get() = repository.getUserId()

    fun loadNotes() {
        if (hasUser) {
            if (userId.isNotBlank()) {
                getUserNotes(userId)
            }
        } else {
            homeUiState = homeUiState.copy(
                notesList = Resource.Error(
                    throwable = Throwable(message = "User is not logged in")
                )
            )
        }
    }


    private fun getUserNotes(userId: String) = viewModelScope.launch {
        repository.getUserNotes(userId).collect {
            homeUiState = homeUiState.copy(notesList = it)
        }
    }

    fun softDeleteNote(noteId: String) = repository.softDeleteNote(noteId) {
        homeUiState = homeUiState.copy(noteSoftDeletedStatus = it)
    }

    fun signOut() = repository.signOut()

}