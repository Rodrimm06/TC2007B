package com.example.tc2007b.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.tc2007b.components.Avatar
import com.example.tc2007b.components.Choice
import com.example.tc2007b.components.DemoNote
import com.example.tc2007b.components.Field
import com.example.tc2007b.components.Pill
import com.example.tc2007b.components.PrimaryButton
import com.example.tc2007b.components.ScreenHeader
import com.example.tc2007b.components.SecondaryButton
import com.example.tc2007b.model.Worker
import com.example.tc2007b.ui.theme.Green
import com.example.tc2007b.ui.theme.Ink
import com.example.tc2007b.ui.theme.Line
import com.example.tc2007b.ui.theme.Muted
import com.example.tc2007b.ui.theme.PaleTeal
import com.example.tc2007b.ui.theme.SoftGray
import com.example.tc2007b.ui.theme.Teal

// Lista del personal de la estancia
@Composable
fun StaffMockup(
    residence: String,
    workers: List<Worker>,
    onAdd: () -> Unit,
    onPerson: (Worker) -> Unit,
) {
    var search by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(true) }
    val people = workers.filter { it.residence == residence }
    val filtered =
        people.filter {
            it.active == active &&
                (it.name.contains(search, true) || it.type.contains(search, true))
        }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        ScreenHeader("Personal", "$residence · Tu estancia asignada", "Coordinadora")
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                search,
                { search = it },
                Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Buscar por nombre o puesto", fontSize = 12.sp, color = Muted)
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, null, Modifier.size(20.dp), tint = Muted)
                },
                singleLine = true,
                shape = RoundedCornerShape(50),
                colors =
                    OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = SoftGray,
                        focusedContainerColor = SoftGray,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Teal,
                    ),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = active,
                    onClick = { active = true },
                    label = { Text("Activo · ${people.count { it.active }}", fontSize = 11.sp) },
                    colors =
                        FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PaleTeal,
                            selectedLabelColor = Teal,
                        ),
                )
                FilterChip(
                    selected = !active,
                    onClick = { active = false },
                    label = { Text("Inactivo · ${people.count { !it.active }}", fontSize = 11.sp) },
                    colors =
                        FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PaleTeal,
                            selectedLabelColor = Teal,
                        ),
                )
            }
            PrimaryButton("Agregar personal", onAdd, Icons.Default.Add)
            Text(
                "PERSONAL ${if (active) "ACTIVO" else "INACTIVO"} · ${filtered.size} PERSONAS",
                color = Muted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
            )
            if (filtered.isEmpty())
                Text("No se encontraron personas.", color = Muted, fontSize = 12.sp)
            filtered.forEach { worker ->
                Row(
                    Modifier.fillMaxWidth().clickable { onPerson(worker) }.padding(vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Avatar(worker.name)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            worker.name,
                            color = Ink,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(worker.type, color = Muted, fontSize = 10.sp)
                        Text("${worker.start}–${worker.end}", color = Muted, fontSize = 10.sp)
                    }
                    Pill(
                        if (worker.active) "Activo" else "Inactivo",
                        if (worker.active) Green else SoftGray,
                        if (worker.active) Color(0xFF237452) else Muted,
                    )
                }
                HorizontalDivider(color = Line)
            }
            DemoNote()
        }
    }
}

// Formulario para agregar personal
@Composable
fun AddWorkerMockup(
    residence: String,
    onBack: () -> Unit,
    onSave: (String, String, String, String, String, String) -> Unit,
) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var type by rememberSaveable { mutableStateOf("Auxiliar educativa") }
    var start by rememberSaveable { mutableStateOf("08:00") }
    var end by rememberSaveable { mutableStateOf("16:00") }
    var error by rememberSaveable { mutableStateOf("") }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        ScreenHeader("Agregar personal", "Coordinadora · $residence", back = onBack)
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                "Completa los datos y el horario habitual. Todos los campos son obligatorios.",
                color = Muted,
                fontSize = 11.sp,
            )
            Column { Field("Nombre completo", name, { name = it }) }
            Column { Field("Correo electrónico", email, { email = it }, Icons.Default.MailOutline) }
            Column { Field("Teléfono", phone, { phone = it }, Icons.Default.Phone) }
            Choice(
                "Puesto",
                type,
                listOf(
                    "Auxiliar educativa",
                    "Educadora",
                    "Cocinera",
                    "Limpieza",
                    "Cuidador",
                    "Guardia",
                    "Encargada",
                ),
            ) {
                type = it
            }
            Text("Horario asignado", color = Ink, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Column(Modifier.weight(1f)) {
                    Field("Entrada", start, { start = it }, Icons.Default.Schedule)
                }
                Column(Modifier.weight(1f)) {
                    Field("Salida", end, { end = it }, Icons.Default.Schedule)
                }
            }
            Text("Formato de 24 horas · Lunes a viernes", color = Muted, fontSize = 10.sp)
            Text("Estancia asignada", color = Muted, fontSize = 10.sp)
            Row(
                Modifier.fillMaxWidth()
                    .background(SoftGray, RoundedCornerShape(6.dp))
                    .border(1.dp, Line, RoundedCornerShape(6.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(residence, Modifier.weight(1f), color = Ink, fontSize = 12.sp)
                Icon(Icons.Default.Lock, null, Modifier.size(16.dp), tint = Muted)
            }
            Text("Asignada a tu cuenta. No se puede modificar.", color = Muted, fontSize = 10.sp)
            if (error.isNotEmpty())
                Text(error, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
            PrimaryButton(
                "Guardar personal",
                {
                    val validTime = Regex("^([01]\\d|2[0-3]):[0-5]\\d$")
                    error =
                        when {
                            name.isBlank() || email.isBlank() || phone.isBlank() ->
                                "Completa todos los campos."
                            !android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() ->
                                "Ingresa un correo electrónico válido."
                            !validTime.matches(start) || !validTime.matches(end) ->
                                "Escribe el horario en formato HH:mm."
                            start >= end -> "La salida debe ser posterior a la entrada."
                            else -> ""
                        }
                    if (error.isEmpty())
                        onSave(name.trim(), email.trim(), phone.trim(), type, start, end)
                },
                Icons.Default.Check,
            )
            SecondaryButton("Cancelar", onBack)
            DemoNote()
        }
    }
}

// Datos de una persona del personal
@Composable
fun WorkerDetail(worker: Worker?, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        ScreenHeader("Detalle del personal", worker?.residence ?: "Personal", back = onBack)
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (worker == null) Text("No se encontró a la persona.", color = Ink)
            else {
                Text(worker.name, color = Ink, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                Text(worker.type, color = Muted)
                Text("Horario: ${worker.start}–${worker.end}", color = Ink)
                Text("Entrada: ${worker.entry ?: "Sin registrar"}", color = Ink)
                Text("Salida: ${worker.exit ?: "Sin registrar"}", color = Ink)
                if (worker.email.isNotEmpty()) Text("Correo: ${worker.email}", color = Ink)
                if (worker.phone.isNotEmpty()) Text("Teléfono: ${worker.phone}", color = Ink)
            }
            SecondaryButton("Volver", onBack)
            DemoNote()
        }
    }
}
