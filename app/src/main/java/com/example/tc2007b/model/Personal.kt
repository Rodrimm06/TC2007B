package com.example.tc2007b.model

import android.system.Os.stat



// Estatus para mostrar los tres estados que tiene personal
enum class stat(val time: String){
    tiempo("A tiempo"),
    late("late"),
    ausente("Ausente")
}
data class Personal(
    // ID
    val id: Int,
    // Nombre
    val name: String,
    // Tipo de empleado (limpieza, cuidador, etc)
    val type: String,
    // Estatus
    val status: stat,
    // Id de la estancia
    val residence_id: Int,
    // Horario de turno
    val schedule: String
)
// Funcion para obtener una persona de personal
fun getPersonal(): List<Personal> = listOf(
    Personal(1, "Manuel","Limpieza",stat.tiempo,1,"09:00-17:00"),
    Personal(2, "Gerardo","Cuidador",stat.late,1,"07:00-15:00"),
    Personal(3,"Marisa","Guardia",stat.ausente,1,"10:00-18:00"),
    Personal(4,"Laura","Cuidadora",stat.tiempo,2,"07:00-15:00"),
    Personal(5,"Pedro","Limpieza",stat.tiempo,2,"09:00-17:00"),
    Personal(6,"Rosa","Encargada",stat.late,2,"10:00-18:00")
)