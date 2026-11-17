package com.example.tc2007b.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tc2007b.components.Avatar
import com.example.tc2007b.components.Choice
import com.example.tc2007b.components.DemoNote
import com.example.tc2007b.components.DetailRow
import com.example.tc2007b.components.Pill
import com.example.tc2007b.components.PrimaryButton
import com.example.tc2007b.components.ScreenHeader
import com.example.tc2007b.components.SecondaryButton
import com.example.tc2007b.model.AttendanceRecord
import com.example.tc2007b.model.Worker
import com.example.tc2007b.ui.theme.Amber
import com.example.tc2007b.ui.theme.Green
import com.example.tc2007b.ui.theme.Ink
import com.example.tc2007b.ui.theme.Line
import com.example.tc2007b.ui.theme.Muted
import com.example.tc2007b.ui.theme.PaleBlue
import com.example.tc2007b.ui.theme.PaleTeal
import com.example.tc2007b.ui.theme.Shape
import com.example.tc2007b.ui.theme.SoftGray
import com.example.tc2007b.ui.theme.Teal
import com.example.tc2007b.ui.theme.clock
import com.example.tc2007b.ui.theme.today

// Pantalla para registrar una entrada o salida
@Composable
fun AttendanceMockup(
    residence: String,
    workers: List<Worker>,
    onBack: () -> Unit,
    onRegister: (Worker, Boolean) -> Unit,
) {
    val people = workers.filter { it.residence == residence && it.active }
    var selectedId by rememberSaveable(residence) { mutableIntStateOf(-1) }
    val worker = people.firstOrNull { it.id == selectedId } ?: people.firstOrNull()
    var entry by rememberSaveable { mutableStateOf(true) }
    LaunchedEffect(worker?.id) { entry = worker?.entry == null }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        ScreenHeader("Registrar asistencia", "$residence · Coordinadora", back = onBack)
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(today, color = Muted, fontSize = 11.sp, modifier = Modifier.weight(1f))
                Pill("◷ $clock h", PaleTeal, Teal)
            }
            if (people.isEmpty())
                Text("No hay personal activo para registrar.", color = Muted, fontSize = 12.sp)
            else {
                val options = people.map { "${it.name} · SC-${it.id.toString().padStart(3, '0')}" }
                val selection =
                    if (worker != null) {
                        "${worker.name} · SC-${worker.id.toString().padStart(3, '0')}"
                    } else {
                        "Seleccionar"
                    }
                Choice("Seleccionar persona", selection, options) { selected ->
                    val index = options.indexOf(selected)
                    if (index >= 0) {
                        selectedId = people[index].id
                    }
                }
                if (worker != null) {
                    Column(
                        Modifier.fillMaxWidth()
                            .border(1.dp, Line, Shape)
                            .background(Color.White, Shape)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Avatar(worker.name)
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(
                                    worker.name,
                                    color = Ink,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                )
                                Text(
                                    "${worker.type} · SC-${worker.id.toString().padStart(3, '0')}",
                                    color = Muted,
                                    fontSize = 10.sp,
                                )
                            }
                        }
                        DetailRow("Horario asignado", "${worker.start}–${worker.end}")
                        DetailRow("Entrada registrada", worker.entry ?: "Pendiente")
                        DetailRow("Salida registrada", worker.exit ?: "Pendiente")
                        Pill(
                            when {
                                worker.exit != null -> "Jornada completa"
                                worker.entry != null -> "Entrada registrada · Salida pendiente"
                                else -> "Entrada pendiente"
                            },
                            if (worker.exit != null) Green else Amber,
                            if (worker.exit != null) Color(0xFF237452) else Color(0xFF966000),
                        )
                    }
                    Text(
                        "¿Qué deseas registrar?",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Ink,
                    )
                    Row(
                        Modifier.fillMaxWidth().border(1.dp, Line, RoundedCornerShape(50)),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        listOf(true to "Entrada", false to "Salida").forEach { (isEntry, label) ->
                            Box(
                                Modifier.weight(1f)
                                    .background(
                                        if (entry == isEntry) PaleTeal else Color.Transparent,
                                        RoundedCornerShape(50),
                                    )
                                    .clickable { entry = isEntry }
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    label,
                                    color = if (entry == isEntry) Teal else Muted,
                                    fontSize = 12.sp,
                                    fontWeight =
                                        if (entry == isEntry) FontWeight.Bold else FontWeight.Normal,
                                )
                            }
                        }
                    }
                    val allowed =
                        if (entry) worker.entry == null
                        else worker.entry != null && worker.exit == null
                    Column(
                        Modifier.fillMaxWidth().background(PaleBlue, Shape).padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        DetailRow(
                            "Hora de ${if (entry) "entrada" else "salida"} a registrar",
                            clock,
                        )
                        Text(
                            if (allowed)
                                "Registrarás la ${if (entry) "entrada" else "salida"} de ${worker.name} en tu estancia. Aún no está confirmada."
                            else if (entry) "La entrada de esta persona ya está registrada."
                            else if (worker.entry == null)
                                "Primero registra la entrada de esta persona."
                            else "La salida de esta persona ya está registrada.",
                            color = Muted,
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                        )
                    }
                    PrimaryButton(
                        "Registrar ${if (entry) "entrada" else "salida"} · $clock",
                        { onRegister(worker, entry) },
                        if (entry) Icons.Default.Login else Icons.Default.Logout,
                        enabled = allowed,
                    )
                }
            }
            DemoNote()
        }
    }
}

// Muestra el registro que se acaba de guardar
@Composable
fun ConfirmationMockup(
    record: AttendanceRecord?,
    workers: List<Worker>,
    onAgain: () -> Unit,
    onHome: () -> Unit,
) {
    val worker = workers.firstOrNull { it.id == record?.workerId }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        ScreenHeader("Asistencia registrada", "Coordinadora · ${record?.residence ?: ""}")
        Column(
            Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Spacer(Modifier.height(8.dp))
            Box(
                Modifier.size(72.dp).background(PaleTeal, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.Check, null, Modifier.size(35.dp), tint = Teal)
            }
            Text(
                "${if (record?.isEntry == true) "Entrada" else "Salida"} registrada",
                color = Ink,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                "La ${if (record?.isEntry == true) "entrada" else "salida"} de ${record?.name ?: ""} quedó registrada correctamente a las ${record?.time ?: ""}.",
                color = Muted,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
            )
            if (worker != null)
                Column(
                    Modifier.fillMaxWidth()
                        .border(1.dp, Line, Shape)
                        .background(Color.White, Shape)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(9.dp),
                ) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            worker.name,
                            Modifier.weight(1f),
                            fontWeight = FontWeight.Bold,
                            color = Ink,
                            fontSize = 14.sp,
                        )
                        Pill("✓ Confirmada", Green, Color(0xFF237452))
                    }
                    Text(worker.type, color = Muted, fontSize = 10.sp)
                    DetailRow("Estancia", worker.residence)
                    DetailRow("Fecha", today)
                    DetailRow("Horario asignado", "${worker.start}–${worker.end}")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Column(
                            Modifier.weight(1f)
                                .background(SoftGray, RoundedCornerShape(6.dp))
                                .padding(10.dp)
                        ) {
                            Text("Entrada", color = Muted, fontSize = 10.sp)
                            Text(
                                worker.entry ?: "—",
                                color = Teal,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Column(
                            Modifier.weight(1f)
                                .background(SoftGray, RoundedCornerShape(6.dp))
                                .padding(10.dp)
                        ) {
                            Text("Salida", color = Muted, fontSize = 10.sp)
                            Text(
                                worker.exit ?: "—",
                                color = Teal,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            PrimaryButton("Registrar otra asistencia", onAgain, Icons.Default.Add)
            SecondaryButton("Volver al inicio", onHome)
            DemoNote()
        }
    }
}
