package com.example.tc2007b.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tc2007b.model.Worker
import com.example.tc2007b.ui.theme.Amber
import com.example.tc2007b.ui.theme.Ink
import com.example.tc2007b.ui.theme.Line
import com.example.tc2007b.ui.theme.Muted
import com.example.tc2007b.ui.theme.PaleBlue
import com.example.tc2007b.ui.theme.PaleTeal
import com.example.tc2007b.ui.theme.Shape
import com.example.tc2007b.ui.theme.SoftGray
import com.example.tc2007b.ui.theme.Teal

// Tarjeta con el resumen de asistencia
@Composable
fun PercentCard(workers: List<Worker>, title: String, general: Boolean = false) {
    val active = workers.filter { it.active }
    val present = active.count { it.entry != null }
    val absent = active.size - present
    val late = active.count { it.entry != null && it.entry > it.start }
    val percent = if (active.isEmpty()) 0 else present * 100 / active.size
    Column(Modifier.fillMaxWidth().background(PaleTeal, Shape).padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Ink,
                modifier = Modifier.weight(1f),
            )
            if (!general) Pill("${active.size} personas activas", SoftGray, Muted)
        }
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
            Text(
                "$percent %",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Teal,
                modifier = Modifier.weight(1f),
            )
            Text("$present de ${active.size} presentes", color = Ink, fontSize = 11.sp)
        }
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { if (active.isEmpty()) 0f else present.toFloat() / active.size },
            modifier = Modifier.fillMaxWidth().height(6.dp),
            color = Teal,
            trackColor = Color.White,
        )
        Spacer(Modifier.height(10.dp))
        if (general) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatLabel("${active.map { it.residence }.distinct().size}", "Estancias")
                StatLabel("${active.size}", "Personas activas")
                StatLabel("$absent", "Ausencias")
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Pill("$absent ausencias", SoftGray, Muted)
                if (late > 0)
                    Pill("$late retardo${if (late == 1) "" else "s"}", Amber, Color(0xFF966000))
            }
            Spacer(Modifier.height(10.dp))
            Text(
                "El retardo está incluido en las personas presentes.",
                color = Muted,
                fontSize = 10.sp,
            )
        }
    }
}

// Número y descripción para el resumen
@Composable
fun StatLabel(number: String, label: String) {
    Column {
        Text(number, fontSize = 19.sp, fontWeight = FontWeight.Bold, color = Ink)
        Text(label, fontSize = 10.sp, color = Muted)
    }
}

// Etiqueta pequeña de estado
@Composable
fun Pill(text: String, background: Color, color: Color) {
    Text(
        text,
        Modifier.background(background, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 5.dp),
        fontSize = 10.sp,
        color = color,
    )
}

// Círculo con las iniciales de una persona
@Composable
fun Avatar(name: String) {
    val words = name.split(" ")
    var initials = ""
    for (word in words) {
        if (word.isNotBlank() && initials.length < 2) {
            initials += word.first().uppercase()
        }
    }
    Box(
        Modifier.size(40.dp).background(PaleBlue, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(initials, color = Color(0xFF376B85), fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

// Fila para mostrar un dato y su valor
@Composable
fun DetailRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Muted, fontSize = 11.sp)
        Text(value, color = Ink, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

// Tarjeta con la asistencia de una estancia
@Composable
fun ResidenceCard(name: String, workers: List<Worker>, onClick: () -> Unit) {
    val active = workers.filter { it.active }
    val present = active.count { it.entry != null }
    val late = active.count { it.entry != null && it.entry > it.start }
    val percentage = if (active.isEmpty()) 0 else present * 100 / active.size
    Column(
        Modifier.fillMaxWidth()
            .border(1.dp, Line, Shape)
            .background(Color.White, Shape)
            .clickable { onClick() }
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        Row(Modifier.fillMaxWidth()) {
            Text(
                name,
                Modifier.weight(1f),
                color = Ink,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
            )
            Text("$percentage %", color = Teal, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
        LinearProgressIndicator(
            progress = { if (active.isEmpty()) 0f else present.toFloat() / active.size },
            modifier = Modifier.fillMaxWidth().height(4.dp),
            color = Teal,
            trackColor = SoftGray,
        )
        Text("$present/${active.size} presentes", color = Ink, fontSize = 10.sp)
        Text("${active.size - present} ausencias · $late retardos", color = Muted, fontSize = 10.sp)
    }
}
