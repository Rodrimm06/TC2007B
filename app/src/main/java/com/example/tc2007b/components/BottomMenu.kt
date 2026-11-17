package com.example.tc2007b.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.InsertChart
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tc2007b.ui.theme.Muted
import com.example.tc2007b.ui.theme.PaleTeal
import com.example.tc2007b.ui.theme.SoftGray
import com.example.tc2007b.ui.theme.Teal

// Menú inferior para cambiar de pantalla
@Composable
fun BottomMenu(page: String, direction: Boolean, onSelect: (String) -> Unit) {
    val options =
        if (direction)
            listOf(
                Triple("summary", "Resumen", Icons.Default.Dashboard),
                Triple("residences", "Estancias", Icons.Default.Apartment),
                Triple("reports", "Reportes", Icons.Default.InsertChart),
            )
        else
            listOf(
                Triple("home", "Inicio", Icons.Default.Home),
                Triple("staff", "Personal", Icons.Default.People),
                Triple("attendance", "Asistencia", Icons.Default.EventAvailable),
            )
    Row(
        Modifier.fillMaxWidth().background(SoftGray).padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        options.forEach { (route, label, icon) ->
            val selected = page == route || (route == "attendance" && page == "confirmed")
            Column(
                Modifier.weight(1f).clickable { onSelect(route) },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    Modifier.background(
                            if (selected) PaleTeal else Color.Transparent,
                            RoundedCornerShape(50),
                        )
                        .padding(horizontal = 16.dp, vertical = 3.dp)
                ) {
                    Icon(icon, label, Modifier.size(19.dp), tint = if (selected) Teal else Muted)
                }
                Text(
                    label,
                    fontSize = 10.sp,
                    color = if (selected) Teal else Muted,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                )
            }
        }
    }
}
