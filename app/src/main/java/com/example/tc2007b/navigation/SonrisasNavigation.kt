package com.example.tc2007b.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tc2007b.components.BottomMenu
import com.example.tc2007b.model.AttendanceRecord
import com.example.tc2007b.model.User
import com.example.tc2007b.model.Worker
import com.example.tc2007b.model.getDemoRecords
import com.example.tc2007b.model.getDemoWorkers
import com.example.tc2007b.model.role
import com.example.tc2007b.screens.AddWorkerMockup
import com.example.tc2007b.screens.AttendanceMockup
import com.example.tc2007b.screens.ConfirmationMockup
import com.example.tc2007b.screens.CoordinatorMockup
import com.example.tc2007b.screens.DirectionMockup
import com.example.tc2007b.screens.LoginMockup
import com.example.tc2007b.screens.ReportMockup
import com.example.tc2007b.screens.ResidencesMockup
import com.example.tc2007b.screens.StaffMockup
import com.example.tc2007b.screens.WorkerDetail
import com.example.tc2007b.ui.theme.Background
import com.example.tc2007b.ui.theme.clock

// Regresa al inicio de sesión si el usuario no tiene acceso
@Composable
fun noAccess(onVolver: () -> Unit) {
    LaunchedEffect(Unit) { onVolver() }
}

@Composable
fun SonrisasApp() {
    val nav = rememberNavController()
    val workers = remember { mutableStateListOf<Worker>().apply { addAll(getDemoWorkers()) } }
    val records = remember {
        mutableStateListOf<AttendanceRecord>().apply { addAll(getDemoRecords()) }
    }
    var user by remember { mutableStateOf<User?>(null) }
    var selectedResidence by rememberSaveable { mutableStateOf("Estancia Arcoíris") }
    var lastRecord by remember { mutableStateOf<AttendanceRecord?>(null) }
    val current = user
    val isDirection = current?.role == role.direccion
    val ownResidence =
        when (current?.residence_id) {
            2 -> "Los Girasoles"
            3 -> "Pequeños Pasos"
            else -> "Estancia Arcoíris"
        }
    val entry by nav.currentBackStackEntryAsState()
    val route = entry?.destination?.route

    // Manda al inicio de sesión y borra el historial de navegación
    val toLogin = {
        nav.navigate(ScreenNames.Login.route) { popUpTo(nav.graph.id) { inclusive = true } }
    }
    val logOut = {
        user = null
        toLogin()
    }

    val barRoutes =
        listOf(
            ScreenNames.Coordinator.route,
            ScreenNames.Staff.route,
            ScreenNames.Attendance.route,
            ScreenNames.AttendanceConfirmation.route,
            ScreenNames.Direction.route,
            ScreenNames.Residences.route,
            ScreenNames.Reports.route,
        )
    val menuPage =
        when (route) {
            ScreenNames.Coordinator.route -> "home"
            ScreenNames.Staff.route -> "staff"
            ScreenNames.Attendance.route -> "attendance"
            ScreenNames.AttendanceConfirmation.route -> "confirmed"
            ScreenNames.Direction.route -> "summary"
            ScreenNames.Residences.route -> "residences"
            ScreenNames.Reports.route -> "reports"
            else -> ""
        }

    Scaffold(
        containerColor = Background,
        bottomBar = {
            if (current != null && route in barRoutes) {
                BottomMenu(menuPage, isDirection) { page ->
                    val destination =
                        when (page) {
                            "home" -> ScreenNames.Coordinator.route
                            "staff" -> ScreenNames.Staff.route
                            "attendance" -> ScreenNames.Attendance.route
                            "summary" -> ScreenNames.Direction.route
                            "residences" -> ScreenNames.Residences.route
                            else -> ScreenNames.Reports.route
                        }
                    nav.navigate(destination) {
                        val start =
                            if (isDirection) ScreenNames.Direction.route
                            else ScreenNames.Coordinator.route
                        popUpTo(start) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = nav,
            startDestination = ScreenNames.Login.route,
            modifier = Modifier.fillMaxSize().padding(innerPadding),
        ) {
            composable(ScreenNames.Login.route) {
                LoginMockup { logged ->
                    user = logged
                    val destination =
                        if (logged.role == role.direccion) {
                            ScreenNames.Direction.route
                        } else {
                            ScreenNames.Coordinator.route
                        }
                    nav.navigate(destination) {
                        popUpTo(ScreenNames.Login.route) { inclusive = true }
                    }
                }
            }
            composable(ScreenNames.Coordinator.route) {
                if (current == null || current.role != role.coordinacion) {
                    noAccess(toLogin)
                } else {
                    CoordinatorMockup(
                        current.name,
                        ownResidence,
                        workers,
                        records,
                        onAttendance = { nav.navigate(ScreenNames.Attendance.route) },
                        onStaff = { nav.navigate(ScreenNames.Staff.route) },
                        onLogout = logOut,
                    )
                }
            }
            composable(ScreenNames.Staff.route) {
                if (current == null || current.role != role.coordinacion) {
                    noAccess(toLogin)
                } else {
                    StaffMockup(
                        ownResidence,
                        workers,
                        onAdd = { nav.navigate(ScreenNames.StaffAdd.route) },
                        onPerson = { person ->
                            nav.navigate(ScreenNames.StaffHandle.createRoute(person.id))
                        },
                    )
                }
            }
            composable(ScreenNames.StaffAdd.route) {
                if (current == null || current.role != role.coordinacion) {
                    noAccess(toLogin)
                } else {
                    AddWorkerMockup(ownResidence, onBack = { nav.popBackStack() }) {
                        name,
                        email,
                        phone,
                        type,
                        start,
                        end ->
                        var nextId = 1
                        for (person in workers) {
                            if (person.id >= nextId) {
                                nextId = person.id + 1
                            }
                        }
                        workers.add(
                            Worker(nextId, name, type, ownResidence, email, phone, start, end)
                        )
                        nav.popBackStack()
                    }
                }
            }
            composable(
                route = ScreenNames.StaffHandle.route,
                arguments = listOf(navArgument("id") { type = NavType.IntType }),
            ) { backStackEntry ->
                if (current == null || current.role != role.coordinacion) {
                    noAccess(toLogin)
                } else {
                    val id = backStackEntry.arguments?.getInt("id")
                    val person = workers.firstOrNull { it.id == id && it.residence == ownResidence }
                    WorkerDetail(person, onBack = { nav.popBackStack() })
                }
            }
            composable(ScreenNames.Attendance.route) {
                if (current == null || current.role != role.coordinacion) {
                    noAccess(toLogin)
                } else {
                    AttendanceMockup(ownResidence, workers, onBack = { nav.popBackStack() }) {
                        worker,
                        isEntry ->
                        val index =
                            workers.indexOfFirst {
                                it.id == worker.id && it.residence == ownResidence
                            }
                        if (index >= 0) {
                            val time = clock
                            if (isEntry) {
                                workers[index] = worker.copy(entry = time, exit = null)
                            } else {
                                workers[index] = worker.copy(exit = time)
                            }
                            val record =
                                AttendanceRecord(
                                    worker.id,
                                    worker.name,
                                    worker.type,
                                    time,
                                    isEntry,
                                    ownResidence,
                                )
                            records.add(0, record)
                            lastRecord = record
                            nav.navigate(ScreenNames.AttendanceConfirmation.route)
                        }
                    }
                }
            }
            composable(ScreenNames.AttendanceConfirmation.route) {
                if (current == null || current.role != role.coordinacion) {
                    noAccess(toLogin)
                } else {
                    ConfirmationMockup(
                        lastRecord,
                        workers,
                        onAgain = { nav.popBackStack() },
                        onHome = { nav.navigate(ScreenNames.Coordinator.route) },
                    )
                }
            }
            composable(ScreenNames.Direction.route) {
                if (current == null || current.role != role.direccion) {
                    noAccess(toLogin)
                } else {
                    DirectionMockup(
                        current.name,
                        workers,
                        onResidence = { residence ->
                            selectedResidence = residence
                            nav.navigate(ScreenNames.Reports.route)
                        },
                        onLogout = logOut,
                    )
                }
            }
            composable(ScreenNames.Residences.route) {
                if (current == null || current.role != role.direccion) {
                    noAccess(toLogin)
                } else {
                    ResidencesMockup(workers) { residence ->
                        selectedResidence = residence
                        nav.navigate(ScreenNames.Reports.route)
                    }
                }
            }
            composable(ScreenNames.Reports.route) {
                if (current == null || current.role != role.direccion) {
                    noAccess(toLogin)
                } else {
                    ReportMockup(workers, selectedResidence) { selectedResidence = it }
                }
            }
        }
    }
}
