package com.example.tc2007b.model

data class User(
    // Identificar al usuario
    val id: Int,
    // Mostrar su id de autenticación
    val auth_id: String,
    // Ver su estancia
    val residence: String,
    // Ver el id de su estancia
    val residenec_id: Int,
    // Ver el rol que tiene (direccion o coordinacion)
    val rol: String
)