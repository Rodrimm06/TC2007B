package com.example.tc2007b.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tc2007b.components.PersonalCard
import com.example.tc2007b.model.User
import com.example.tc2007b.model.getPersonal
import com.example.tc2007b.model.stat

// Para poder ver el calendario de los trabajadores
// **Faltan algunas otras funcionalidades
@Composable
fun Calendar(user: User, modifier: Modifier = Modifier){

    val personal = remember(user.residence_id) {
        getPersonal()
            .filter { it.residence_id == user.residence_id }
            .sortedBy { it.schedule }
    }
    val absent = personal.count { it.status == stat.absent }

    Column(modifier.fillMaxSize().padding(16.dp)) {
        Text("Horario de hoy", style = MaterialTheme.typography.headlineMedium)
        Text("${personal.size} trabajadores | $absent ausente(s)", style = MaterialTheme.typography.bodyMedium)
        LazyColumn {
            items(personal) { p ->
                PersonalCard(p, true)
            }
        }
    }
}