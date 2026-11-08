package com.example.tc2007b.model
enum class role(val r: String) {
    direccion("Direccion"), coordinacion("Coordinacion")
}
data class User(
    // Identificar al usuario
    val id: Int,
    // Mostrar su nombre
    val name: String,
    // Mostrar su id de autenticación
    val password: String,
    // Ver su estancia
    val residence: String,
    // Ver el id de su estancia
    val residenec_id: Int,
    // Ver el rol que tiene (direccion o coordinacion)
    val role: role
)

fun getUser(): List<User> = listOf(
    User(1, "Manuel","1234", "Residencia 1", 1, role.direccion),
    User(2, "Esperanza","fjbjne", "", 0, role.coordinacion),
    User(3,"Juana","passwd","Residencia 2",2,role.coordinacion,)
)

