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
import com.example.tc2007b.model.Personal
import com.example.tc2007b.model.User
import com.example.tc2007b.model.getPersonal

// Lista del personal
@Composable
fun StaffList(user: User, onPersonClick: (Personal) -> Unit, modifier: Modifier = Modifier){
    // Mostrar solo personal de la misma estancia
    val personal = remember(user.residence_id) {
        getPersonal().filter { it.residence_id == user.residence_id }
    }
    // Columna para mostrar todas las personas de la estancia
    Column(modifier.fillMaxSize().padding(16.dp)) {
        Text("Personal", style = MaterialTheme.typography.headlineMedium)
        Text("${personal.size} miembros · ${user.residence}", style = MaterialTheme.typography.bodyMedium)
        // Lazy column para mostrar tajetas del personal
        LazyColumn {
            items(personal) { p ->
                PersonalCard(p, true, onPersonClick)
            }
        }
    }
}