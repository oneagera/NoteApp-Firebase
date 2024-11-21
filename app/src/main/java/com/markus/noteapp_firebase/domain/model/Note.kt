package com.markus.noteapp_firebase.domain.model

import com.google.firebase.Timestamp

data class Note(
    val userId: String = "",
    val title: String = "",
    val description: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val colorIndex: Int = 0,
    val noteId: String = "",
    val deleted: Boolean = false,
    val deletedTimestamp: Timestamp? = null
)
