package com.markus.noteapp_firebase.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.markus.noteapp_firebase.presentation.util.FontFamily

val fontFamily = FontFamily()
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = fontFamily.fontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    //note content
    headlineSmall = TextStyle(
        fontFamily = fontFamily.fontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
        color = Color.Black
    ),
    //note title
    titleMedium = TextStyle(
        fontFamily = fontFamily.fontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.5.sp,
        color = Color.Black
    ),
    bodySmall = TextStyle(
        fontFamily = fontFamily.fontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)