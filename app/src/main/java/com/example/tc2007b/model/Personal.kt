package com.example.tc2007b.model

import android.R


data class Personal(
    // ID para identificar al personal
    val id: Int,
    // Nombre de la persona
    val name: String,
    // Tipo de empleado (limpieza, cuiador, etc)
    val type: String,
    // A tiempo, ausente, tarde
    val status: String,
)