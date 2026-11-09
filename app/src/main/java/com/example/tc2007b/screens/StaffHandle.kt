package com.example.tc2007b.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tc2007b.model.Personal

// Detalle de personal
@Composable
fun StaffHandle(person: Personal?,
                onBack: () -> Unit,
                modifier: Modifier = Modifier){
    Column(
        modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Para poder volver
        TextButton(onClick = onBack) { Text("Volver") }
        // Si no encuentra nada
        if (person == null) {
            Text("No se encontro a la persona")
        } else {
            // Mostrar informacion de la persona
            Text(person.name, style = MaterialTheme.typography.headlineMedium)
            Text("Funcion: ${person.type}")
            Text("Horario: ${person.schedule}")
            Text("Estado de hoy: ${person.status.time}")
        }
    }
}