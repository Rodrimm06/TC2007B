package com.example.tc2007b.navigation
// Clase sellada para navegación
sealed class ScreenNames(val route: String){
    // Login
    data object Login : ScreenNames("login")
    // Pantalla coordinacion
    data object Coordinator: ScreenNames("coordinator")
    // Pantalla direccion
    data object Direction: ScreenNames("direction")
    // Pantalla calendario
    data object Calendar: ScreenNames("calendar")
    // Pantalla de registro (Todavia no se elabora)
    data object Register: ScreenNames("register")
    // Pantalla de staff
    data object Staff: ScreenNames("staff")
    // Pantalla para ver detalles de un staff
    data object StaffHandle: ScreenNames("staffhandle/{id}"){
        fun createRoute(id: Int)="staffhandle/$id"
    }
    // Pantalla para ver tareas
    data object Tasks: ScreenNames("tasks")
    // Pantalla para hacer la asistencia
    data object Attendance: ScreenNames("attendance")
}