package com.example.tc2007b.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tc2007b.model.User
import com.example.tc2007b.model.getPersonal
import com.example.tc2007b.model.getResidence
import com.example.tc2007b.model.stat


@Composable
fun Direction(user: User, onLogout: () -> Unit, modifier: Modifier = Modifier){
    val residence = getResidence()
    val personal = getPersonal()

    Column(
        modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Hola, ${user.name}", style = MaterialTheme.typography.headlineMedium)
        Text("Resumen de estancias", style = MaterialTheme.typography.bodyMedium)
        // Lazy colum para mostrar todas las estancias
        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(residence) { e ->
                // Mostrar informacion de cada estancia
                val fromResidence = personal.filter { it.residence_id == e.id }
                val absent = fromResidence.count { it.status == stat.absent }
                val late = fromResidence.count { it.status == stat.late }
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(e.name, style = MaterialTheme.typography.titleLarge)
                        Text(e.location, style = MaterialTheme.typography.bodyMedium)
                        Text("Staff disponible: ${fromResidence.size - absent} / ${fromResidence.size}")
                        Text("Tarde: $late | Ausente: $absent")
                        if (absent > 0) {
                            Text("Revisar cobertura", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }

        OutlinedButton(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
            Text("Cerrar sesion")
        }
    }
}