package com.example.tc2007b.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.tc2007b.navigation.ScreenNames

// Barra de abajo de coordinacion (Inicio, Personal, Horario, Tareas)
@Composable
fun Menu(nav: NavController, modifier: Modifier = Modifier){
    val entry by nav.currentBackStackEntryAsState()
    val actual = entry?.destination?.route

    val pestanas = listOf(
        Triple(ScreenNames.Coordinator.route, "Inicio", Icons.Filled.Home),
        Triple(ScreenNames.Staff.route, "Personal", Icons.Filled.Person),
        Triple(ScreenNames.Calendar.route, "Horario", Icons.Filled.DateRange),
        Triple(ScreenNames.Tasks.route, "tasks", Icons.Filled.CheckCircle)
    )

    NavigationBar(modifier = modifier) {
        pestanas.forEach { (route, label, icon) ->
            NavigationBarItem(
                // El detalle de una persona cuenta como parte de "Personal"
                selected = actual == route ||
                        (route == ScreenNames.Staff.route && actual == ScreenNames.StaffHandle.route),
                onClick = {
                    nav.navigate(route) {
                        popUpTo(ScreenNames.Coordinator.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) }
            )
        }
    }
}