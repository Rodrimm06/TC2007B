package com.example.tc2007b.navigation

sealed class ScreenNames(val route: String){
    data object Menu: ScreenNames("menu")

    data object Login : ScreenNames("login")

    data object Coordinator: ScreenNames("coordinator")

    data object Direction: ScreenNames("direction")

    data object Calendar: ScreenNames("calendar")

    data object Register: ScreenNames("register")

    data object StaffHandle: ScreenNames("staffhandle/{id}"){
        fun createRoute(id: Int)="staffhandle$id"
    }
    data object Tasks: ScreenNames("tasks")

    data object Attendance: ScreenNames("attendance")
}