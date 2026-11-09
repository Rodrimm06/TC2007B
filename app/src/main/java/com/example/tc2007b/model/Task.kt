package com.example.tc2007b.model
// Clase para una tarea
data class Task(
    // ID
    val id: Int,
    // Nombre
    val name: String,
    // Responsable (vacio si todavia esta sin asignar)
    val responsible: String,
    // Hora
    val schedule: String,
    // Saber si ya se completo
    val done: Boolean,
    // Id de la estancia
    val residence_id: Int
)
// Funcion para obtener una tarea
fun getTasks(): List<Task> = listOf(
    Task(1, "Apertura", "Gerardo", "07:00", true, 1),
    Task(2, "Limpieza de salas", "Manuel", "09:00", false, 1),
    Task(3, "Revisar materiales", "", "11:00", false, 1),
    Task(4, "Apertura", "Laura", "07:00", false, 2),
    Task(5, "Limpieza de salas", "Pedro", "09:00", false, 2)
)