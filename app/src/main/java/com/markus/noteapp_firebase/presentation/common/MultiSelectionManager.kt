package com.markus.noteapp_firebase.presentation.common

import androidx.compose.runtime.mutableStateOf

class MultiSelectionManager<T> {
    private val _selectedNotes = mutableStateOf(setOf<T>())
    val selectedNotes: Set<T> get() = _selectedNotes.value

    val isMultiSelectionEnabled: Boolean
        get() = _selectedNotes.value.isNotEmpty()

    fun toggleNoteSelection(item: T) {
        _selectedNotes.value = if (_selectedNotes.value.contains(item)) {
            _selectedNotes.value - item
        } else {
            _selectedNotes.value + item
        }
    }

    fun selectAllNotes(items: List<T>) {
        _selectedNotes.value = items.toSet()
    }

    fun deselectAllNotes() {
        _selectedNotes.value = emptySet()
    }

    fun areAllNotesSelected(items: List<T>): Boolean {
        return _selectedNotes.value.size == items.size && items.isNotEmpty()
    }

    fun getSelectedNoteCount(): Int {
        return _selectedNotes.value.size
    }

    fun getSingleSelectedNote(): T? {
        return if (_selectedNotes.value.size == 1) {
            _selectedNotes.value.first()
        } else {
            null
        }
    }
}