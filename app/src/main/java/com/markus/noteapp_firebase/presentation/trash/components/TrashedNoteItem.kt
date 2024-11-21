package com.markus.noteapp_firebase.presentation.trash.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.markus.noteapp_firebase.domain.model.Note
import com.markus.noteapp_firebase.presentation.util.Utils

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TrashedNoteItem(
    onLongClick: () -> Unit,
    onClick: () -> Unit,
    isSelected: Boolean = false,
    note: Note
) {
    Card(
        modifier = Modifier
            .combinedClickable(
                onLongClick = { onLongClick.invoke() },
                onClick = { onClick.invoke() }
            )
            .padding(8.dp)
            .fillMaxWidth()
            .fillMaxHeight()
            .clip(RoundedCornerShape(10.dp))
            .then(
                if (isSelected) {
                    Modifier.border(
                        4.dp,
                        Color.DarkGray,
                        RoundedCornerShape(10.dp)
                    )
                } else {
                    Modifier
                }
            ),
        colors = CardDefaults.cardColors(containerColor = Utils.colors[note.colorIndex])

    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                maxLines = 1,
                overflow = TextOverflow.Clip
            )
            Spacer(modifier = Modifier.size(8.dp))
            CompositionLocalProvider(
                LocalContentColor provides MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) //disabled/reduced emphasis
            ) {
                Text(
                    text = note.description,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Light,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 4
                )
            }
        }
    }
}