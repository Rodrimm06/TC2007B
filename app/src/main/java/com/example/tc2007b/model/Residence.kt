package com.example.tc2007b.model
// Clase de estancia
data class Residence(
    // ID
    val id: Int,
    // Nombre
    val name: String,
    // Ubicación de la estancia
    val location: String
)
// Funcion para obtener una estancia
fun getResidence(): List<Residence> = listOf(
    Residence(1, "Estancia 1", "Ubicacion 1"),
    Residence(2, "Estancia 2", "Ubicacion 2")
)