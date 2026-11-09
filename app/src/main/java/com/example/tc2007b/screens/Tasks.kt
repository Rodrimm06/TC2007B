package com.example.tc2007b.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tc2007b.model.Task
import com.example.tc2007b.model.User
import com.example.tc2007b.model.getTasks

// Para ver las tareas de cada estancia
@Composable
fun Tasks(user: User, modifier: Modifier = Modifier){
    // Solo ver las tareas de una estancia
    val tasks = remember(user.residence_id) {
        mutableStateListOf<Task>().apply {
            addAll(getTasks().filter { it.residence_id == user.residence_id })
        }
    }
    val done = tasks.count { it.done }

    Column(modifier.fillMaxSize().padding(16.dp)) {
        Text("Tareas", style = MaterialTheme.typography.headlineMedium)
        Text("$done de ${tasks.size} tareas completadas", style = MaterialTheme.typography.bodyMedium)
        // Ver el progreso de tareas
        LinearProgressIndicator(
            progress = { if (tasks.isEmpty()) 0f else done.toFloat() / tasks.size },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )
        // Lazy column para ver las tareas
        LazyColumn {
            itemsIndexed(tasks) { i, t ->
                Card(Modifier.padding(vertical = 4.dp).fillMaxWidth()) {
                    Row(
                        Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = t.done,
                            onCheckedChange = { tasks[i] = t.copy(done = it) }
                        )
                        Column {
                            Text(t.name, style = MaterialTheme.typography.titleMedium)
                            Text(
                                if (t.responsible.isEmpty()) "Sin asignar"
                                else "${t.responsible} · ${t.schedule}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}