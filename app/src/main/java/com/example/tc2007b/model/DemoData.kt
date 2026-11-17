package com.example.tc2007b.model

// Datos del personal para la demostración
data class Worker(
    // ID
    val id: Int,
    // Nombre
    val name: String,
    // Tipo de empleado
    val type: String,
    // Estancia
    val residence: String,
    // Datos de contacto
    val email: String = "",
    val phone: String = "",
    // Horario de turno
    val start: String = "08:00",
    val end: String = "16:00",
    // Entrada y salida registradas
    val entry: String? = null,
    val exit: String? = null,
    // Estado del personal
    val active: Boolean = true,
)

// Registro de una entrada o una salida
data class AttendanceRecord(
    // ID de la persona
    val workerId: Int,
    // Nombre y tipo de empleado
    val name: String,
    val type: String,
    // Hora del registro
    val time: String,
    // Saber si es entrada o salida
    val isEntry: Boolean,
    // Estancia
    val residence: String,
)

// Personal de ejemplo para las tres estancias
fun getDemoWorkers(): List<Worker> =
    listOf(
        Worker(
            1,
            "Lucía Herrera",
            "Auxiliar educativa",
            "Estancia Arcoíris",
            entry = "07:58",
            exit = "16:05",
        ),
        Worker(
            2,
            "Sofía Ramírez",
            "Educadora",
            "Estancia Arcoíris",
            entry = "07:55",
            exit = "16:02",
        ),
        Worker(3, "Elena Torres", "Cocinera", "Estancia Arcoíris", entry = "07:50", exit = "16:00"),
        Worker(4, "Daniela Cruz", "Educadora", "Estancia Arcoíris", entry = "08:14"),
        Worker(5, "Ana Morales", "Auxiliar educativa", "Estancia Arcoíris", entry = "07:54"),
        Worker(6, "Valeria Mendoza", "Auxiliar educativa", "Estancia Arcoíris", entry = "07:55"),
        Worker(7, "María López", "Educadora", "Estancia Arcoíris", entry = "07:56"),
        Worker(8, "José Hernández", "Cocinera", "Estancia Arcoíris", entry = "07:57"),
        Worker(9, "Paola García", "Educadora", "Estancia Arcoíris", entry = "07:58"),
        Worker(10, "Luis Romero", "Auxiliar educativa", "Estancia Arcoíris", entry = "07:59"),
        Worker(11, "Claudia Ruiz", "Auxiliar educativa", "Estancia Arcoíris"),
        Worker(12, "Andrés Pérez", "Educadora", "Estancia Arcoíris"),
        Worker(101, "Lucía Herrera", "Auxiliar educativa", "Los Girasoles", entry = "07:50"),
        Worker(102, "Sofía Ramírez", "Educadora", "Los Girasoles", entry = "07:51"),
        Worker(103, "Elena Torres", "Cocinera", "Los Girasoles", entry = "07:52"),
        Worker(104, "Daniela Cruz", "Educadora", "Los Girasoles", entry = "07:53"),
        Worker(105, "Ana Morales", "Auxiliar educativa", "Los Girasoles", entry = "07:54"),
        Worker(106, "Valeria Mendoza", "Auxiliar educativa", "Los Girasoles", entry = "07:55"),
        Worker(107, "María López", "Educadora", "Los Girasoles", entry = "07:56"),
        Worker(108, "José Hernández", "Cocinera", "Los Girasoles", entry = "07:57"),
        Worker(109, "Paola García", "Educadora", "Los Girasoles"),
        Worker(201, "Lucía Herrera", "Auxiliar educativa", "Pequeños Pasos", entry = "07:50"),
        Worker(202, "Sofía Ramírez", "Educadora", "Pequeños Pasos", entry = "07:51"),
        Worker(203, "Elena Torres", "Cocinera", "Pequeños Pasos", entry = "07:52"),
        Worker(204, "Daniela Cruz", "Educadora", "Pequeños Pasos", entry = "07:53"),
        Worker(205, "Ana Morales", "Auxiliar educativa", "Pequeños Pasos", entry = "07:54"),
        Worker(206, "Valeria Mendoza", "Auxiliar educativa", "Pequeños Pasos", entry = "07:55"),
        Worker(207, "María López", "Educadora", "Pequeños Pasos", entry = "07:56"),
        Worker(208, "José Hernández", "Cocinera", "Pequeños Pasos", entry = "07:57"),
        Worker(209, "Paola García", "Educadora", "Pequeños Pasos", entry = "07:58"),
    )

// Registros que aparecen al abrir la aplicación
fun getDemoRecords(): List<AttendanceRecord> =
    listOf(
        AttendanceRecord(2, "Sofía Ramírez", "Educadora", "16:02", false, "Estancia Arcoíris"),
        AttendanceRecord(3, "Elena Torres", "Cocinera", "16:00", false, "Estancia Arcoíris"),
        AttendanceRecord(
            1,
            "Lucía Herrera",
            "Auxiliar educativa",
            "07:58",
            true,
            "Estancia Arcoíris",
        ),
    )
