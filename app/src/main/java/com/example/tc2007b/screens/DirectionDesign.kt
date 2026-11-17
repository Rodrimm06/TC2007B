package com.example.tc2007b.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tc2007b.components.Choice
import com.example.tc2007b.components.DemoNote
import com.example.tc2007b.components.PercentCard
import com.example.tc2007b.components.Pill
import com.example.tc2007b.components.ResidenceCard
import com.example.tc2007b.components.ScreenHeader
import com.example.tc2007b.model.Worker
import com.example.tc2007b.ui.theme.Amber
import com.example.tc2007b.ui.theme.Green
import com.example.tc2007b.ui.theme.Ink
import com.example.tc2007b.ui.theme.Line
import com.example.tc2007b.ui.theme.Muted
import com.example.tc2007b.ui.theme.PaleBlue
import com.example.tc2007b.ui.theme.Shape
import com.example.tc2007b.ui.theme.SoftGray
import com.example.tc2007b.ui.theme.Teal
import com.example.tc2007b.ui.theme.today

// Resumen de asistencia para dirección
@Composable
fun DirectionMockup(
    name: String,
    workers: List<Worker>,
    onResidence: (String) -> Unit,
    onLogout: () -> Unit,
) {
    val residences = workers.map { it.residence }.distinct()
    var filter by rememberSaveable { mutableStateOf("Todas las estancias") }
    val shown =
        if (filter == "Todas las estancias") workers else workers.filter { it.residence == filter }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        ScreenHeader(
            "Hola, $name",
            "Panorama de todas las estancias",
            "Dirección",
            onLogout = onLogout,
        )
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(today, Modifier.weight(1f), color = Muted, fontSize = 11.sp)
                Pill("♧ Solo consulta", SoftGray, Muted)
            }
            Choice("Estancia", filter, listOf("Todas las estancias") + residences) { filter = it }
            PercentCard(shown, "Asistencia general de hoy", general = true)
            Text("Por estancia", color = Ink, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            residences
                .filter { filter == "Todas las estancias" || it == filter }
                .forEach { residence ->
                    ResidenceCard(
                        residence,
                        workers.filter { it.residence == residence },
                        { onResidence(residence) },
                    )
                }
            DemoNote("Solo consulta · Datos de demostración")
        }
    }
}

// Lista de estancias para dirección
@Composable
fun ResidencesMockup(workers: List<Worker>, onResidence: (String) -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        ScreenHeader("Estancias", "Dirección · Solo consulta", "Dirección")
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                "Selecciona una estancia para consultar su asistencia.",
                color = Muted,
                fontSize = 11.sp,
            )
            workers
                .map { it.residence }
                .distinct()
                .forEach { residence ->
                    ResidenceCard(
                        residence,
                        workers.filter { it.residence == residence },
                        { onResidence(residence) },
                    )
                }
            DemoNote("Solo consulta · Datos de demostración")
        }
    }
}

// Consulta de asistencia de una estancia
@Composable
fun ReportMockup(workers: List<Worker>, residence: String, onResidence: (String) -> Unit) {
    val people = workers.filter { it.residence == residence && it.active }
    val present = people.count { it.entry != null }
    val late = people.count { it.entry != null && it.entry > it.start }
    val percent = if (people.isEmpty()) 0 else present * 100 / people.size
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        ScreenHeader("Consulta de asistencia", "Dirección · Solo consulta")
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Choice("Estancia", residence, workers.map { it.residence }.distinct(), onResidence)
            Column {
                Text("Fecha", color = Muted, fontSize = 10.sp)
                Spacer(Modifier.height(5.dp))
                Row(
                    Modifier.fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(6.dp))
                        .border(1.dp, Line, RoundedCornerShape(6.dp))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(today, Modifier.weight(1f), fontSize = 12.sp, color = Ink)
                    Icon(Icons.Default.CalendarToday, null, Modifier.size(17.dp), tint = Muted)
                }
            }
            Column(
                Modifier.fillMaxWidth().background(PaleBlue, Shape).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "$percent %",
                        Modifier.weight(1f),
                        color = Teal,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text("$present de ${people.size} presentes", color = Ink, fontSize = 11.sp)
                }
                Text(
                    "${people.size - present} ausencias · $late retardos incluidos en los presentes",
                    color = Muted,
                    fontSize = 10.sp,
                )
            }
            Text(
                "Jornada del personal",
                color = Ink,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
            if (people.isEmpty())
                Text("No hay personal activo en esta estancia.", color = Muted, fontSize = 12.sp)
            people.forEach { worker ->
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            worker.name,
                            Modifier.weight(1f),
                            color = Ink,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        val status =
                            if (worker.entry == null) "Ausencia"
                            else if (worker.exit == null) "Sin salida" else "Completo"
                        Pill(
                            status,
                            if (status == "Completo") Green
                            else if (status == "Sin salida") Amber else SoftGray,
                            if (status == "Completo") Color(0xFF237452)
                            else if (status == "Sin salida") Color(0xFF966000) else Muted,
                        )
                    }
                    Text(
                        worker.type +
                            if (worker.entry != null && worker.entry > worker.start) " · Retardo"
                            else "",
                        color = Muted,
                        fontSize = 10.sp,
                    )
                    Row(Modifier.fillMaxWidth()) {
                        Text(
                            "⇥  Entrada ${worker.entry ?: "—"}",
                            Modifier.weight(1f),
                            color = Ink,
                            fontSize = 11.sp,
                        )
                        Text(
                            "⇥  Salida ${worker.exit ?: if (worker.entry != null) "Pendiente" else "—"}",
                            Modifier.weight(1f),
                            color = Ink,
                            fontSize = 11.sp,
                        )
                    }
                    HorizontalDivider(color = Line)
                }
            }
            Text(
                "'Sin salida' indica un registro incompleto, no una ausencia. Esta demostración muestra únicamente el día de hoy.",
                color = Muted,
                fontSize = 10.sp,
            )
            DemoNote("Solo consulta · Datos de demostración")
        }
    }
}
