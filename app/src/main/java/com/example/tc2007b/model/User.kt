package com.example.tc2007b.model
// Dos tipos de roles de usuario
enum class role(val r: String) {
    direccion("Direccion"), coordinacion("Coordinacion")
}
// Clase usuario
data class User(
    // ID
    val id: Int,
    // Nombre
    val name: String,
    // Contraseña
    val password: String,
    // Estancia
    val residence: String,
    // Id de su estancia
    val residence_id: Int,
    // Rol (direccion o coordinacion)
    val role: role
)
// Funcion para obtener los usuarios
fun getUser(): List<User> = listOf(
    User(1, "Manuel","1234", "Residencia 1", 1, role.coordinacion),
    User(3,"Juana","passwd","Residencia 2",2,role.coordinacion,),
    User(4,"Robert","1234","",0,role.direccion)
)

