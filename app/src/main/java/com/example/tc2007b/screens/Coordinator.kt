package com.example.tc2007b.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tc2007b.components.PersonalCard
import com.example.tc2007b.model.User
import com.example.tc2007b.model.getPersonal
import com.example.tc2007b.model.stat

// Pantalla de inicio de coordinacion
@Composable
fun Coordinator(user: User, onLogout: () -> Unit, modifier: Modifier = Modifier){
    // Solo el personal de su estancia
    val personal = getPersonal().filter { it.residence_id == user.residence_id }
    val absent = personal.count { it.status == stat.ausente }
    val late = personal.count { it.status == stat.late }
    val available = personal.size - absent

    Column(
        modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Hola, ${user.name}", style = MaterialTheme.typography.headlineMedium)
        Text(user.residence, style = MaterialTheme.typography.bodyMedium)

        // Resumen de asistencia
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Card(Modifier.weight(1f)) {
                Column(Modifier.padding(16.dp)) {
                    Text("$available / ${personal.size}", style = MaterialTheme.typography.titleLarge)
                    Text("Staff disponible")
                }
            }
            Card(Modifier.weight(1f)) {
                Column(Modifier.padding(16.dp)) {
                    Text("$late", style = MaterialTheme.typography.titleLarge)
                    Text("Staff tarde hoy")
                }
            }
        }

        // Alerta si alguien no se ha reportado
        if (absent > 0) {
            Card(
                Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    "$absent staff no se ha reportado aun",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Text("Checador", style = MaterialTheme.typography.titleMedium)
        LazyColumn(Modifier.weight(1f)) {
            items(personal) { p ->
                PersonalCard(p)
            }
        }

        OutlinedButton(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
            Text("Cerrar sesion")
        }
    }
}