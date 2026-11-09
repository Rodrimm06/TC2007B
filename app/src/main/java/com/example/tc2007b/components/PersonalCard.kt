package com.example.tc2007b.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tc2007b.model.Personal
import com.example.tc2007b.model.stat


@Composable
// Tarjet reutilizable para mostrar al personal
fun PersonalCard(
    person: Personal,
    mostrarHorario: Boolean = false,
    onItemClick: (Personal) -> Unit = {}
){
    Card(
        modifier = Modifier.padding(vertical = 4.dp)
            .fillMaxWidth()
            .clickable { onItemClick(person) }
    ){
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Column(Modifier.weight(1f)) {
                Text(person.name, style = MaterialTheme.typography.titleMedium)
                Text(person.type, style = MaterialTheme.typography.bodyMedium)
                if (mostrarHorario) {
                    Text(person.schedule, style = MaterialTheme.typography.bodySmall)
                }
            }
            // Ausente se pone de color rojo
            Text(
                person.status.time,
                color = if (person.status == stat.absent) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}