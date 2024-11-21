package com.markus.noteapp_firebase.presentation.home.components

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

fun formatDate(timestamp: Timestamp): String {
    val sdf = SimpleDateFormat("dd-MM-yy hh:mm", Locale.getDefault())
    return sdf.format(timestamp.toDate())
}