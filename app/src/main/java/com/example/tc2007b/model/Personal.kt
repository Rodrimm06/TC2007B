package com.example.tc2007b.model

import android.R

enum class stat(val time: String){
    tiempo("A tiempo"),
    tarde("Tarde"),
    asuente("Ausente")
}
data class Personal(
    // ID para identificar al personal
    val id: Int,
    // Nombre de la persona
    val name: String,
    // Tipo de empleado (limpieza, cuiador, etc)
    val type: String,
    // A tiempo, ausente, tarde
    val status: stat,
)

fun getPersonal(): List<Personal> = listOf(
    Personal(1, "Manuel","Limpieza",stat.tiempo),
    Personal(2, "Gerardo","Cuidador",stat.tarde),
    Personal(3,"Marisa","Guardia",stat.asuente)
)
