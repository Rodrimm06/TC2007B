package com.example.tc2007b.screens

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.PeopleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tc2007b.components.DemoNote
import com.example.tc2007b.components.PercentCard
import com.example.tc2007b.components.PrimaryButton
import com.example.tc2007b.components.ScreenHeader
import com.example.tc2007b.components.SecondaryButton
import com.example.tc2007b.model.AttendanceRecord
import com.example.tc2007b.model.Worker
import com.example.tc2007b.ui.theme.Ink
import com.example.tc2007b.ui.theme.Muted
import com.example.tc2007b.ui.theme.Teal
import com.example.tc2007b.ui.theme.today

// Pantalla de inicio para coordinación
@Composable
fun CoordinatorMockup(
    name: String,
    residence: String,
    workers: List<Worker>,
    records: List<AttendanceRecord>,
    onAttendance: () -> Unit,
    onStaff: () -> Unit,
    onLogout: () -> Unit,
) {
    val people = workers.filter { it.residence == residence }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        ScreenHeader("Hola, $name", residence, "Coordinadora", onLogout = onLogout)
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarToday, null, Modifier.size(15.dp), tint = Teal)
                Spacer(Modifier.width(8.dp))
                Text(today, color = Muted, fontSize = 11.sp)
            }
            PercentCard(people, "Asistencia de hoy")
            PrimaryButton("Registrar asistencia", onAttendance, Icons.Default.EventAvailable)
            SecondaryButton("Ver personal", onStaff, Icons.Default.PeopleOutline)
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Registros recientes",
                    color = Ink,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    "Ver personal",
                    Modifier.clickable { onStaff() },
                    color = Teal,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            val recent = records.filter { it.residence == residence }.take(3)
            if (recent.isEmpty()) Text("Todavía no hay registros.", color = Muted, fontSize = 12.sp)
            recent.forEach { record ->
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        if (record.isEntry) Icons.Default.Login else Icons.Default.Logout,
                        null,
                        Modifier.size(20.dp),
                        tint = Teal,
                    )
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            record.name,
                            fontSize = 12.sp,
                            color = Ink,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(record.type, fontSize = 10.sp, color = Muted)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            record.time,
                            fontSize = 12.sp,
                            color = Ink,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            if (record.isEntry) "Entrada" else "Salida",
                            fontSize = 10.sp,
                            color = Muted,
                        )
                    }
                }
            }
            DemoNote("Solo administras tu estancia · Datos de demostración")
        }
    }
}
