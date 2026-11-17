package com.example.tc2007b.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

val Ink = Color(0xFF193541)
val Muted = Color(0xFF617C88)
val Teal = Color(0xFF006D78)
val PaleTeal = Color(0xFFD3F0F0)
val PaleBlue = Color(0xFFE5F3FB)
val Background = Color(0xFFF7FAFC)
val Line = Color(0xFFCFDFE5)
val SoftGray = Color(0xFFEDF4F6)
val Green = Color(0xFFE1F2E9)
val Amber = Color(0xFFFFEFD0)
val Shape = RoundedCornerShape(14.dp)
val locale = Locale.forLanguageTag("es-MX")
val today: String
    get() =
        LocalDate.now()
            .format(DateTimeFormatter.ofPattern("EEEE d 'de' MMMM 'de' yyyy", locale))
            .replaceFirstChar { it.uppercase(locale) }
val clock: String
    get() = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
