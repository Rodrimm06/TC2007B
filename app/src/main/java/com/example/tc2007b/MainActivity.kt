package com.example.tc2007b

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tc2007b.model.getPersonal
import com.example.tc2007b.model.getUser
import com.example.tc2007b.model.role
import com.example.tc2007b.navigation.ScreenNames
import com.example.tc2007b.screens.Calendar
import com.example.tc2007b.screens.Coordinator
import com.example.tc2007b.screens.Direction
import com.example.tc2007b.screens.Login
import com.example.tc2007b.components.Menu
import com.example.tc2007b.screens.StaffHandle
import com.example.tc2007b.screens.StaffList
import com.example.tc2007b.screens.Tasks
import com.example.tc2007b.ui.theme.TC2007BTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TC2007BTheme {
                MainNavigation()
            }
        }
    }
}

// Si alguien entra a una pantalla que no le corresponde, lo regresa al login
@Composable
fun noAccess(onVolver: () -> Unit) {
    LaunchedEffect(Unit) { onVolver() }
}
@Preview
@Composable
fun MainNavigation(modifier: Modifier = Modifier) {
    val nav = rememberNavController()
    val users = remember { getUser() }
    var userId by rememberSaveable { mutableIntStateOf(-1) }
    val actualUser = users.firstOrNull { it.id == userId }
    val entry by nav.currentBackStackEntryAsState()
    val route = entry?.destination?.route
    val bar = listOf(
        ScreenNames.Coordinator.route,
        ScreenNames.Staff.route,
        ScreenNames.StaffHandle.route,
        ScreenNames.Calendar.route,
        ScreenNames.Tasks.route
    )
    // Manda al login y borra el historial de navegacion
    val toLogin = {
        if (nav.currentDestination?.route != ScreenNames.Login.route) {
            nav.navigate(ScreenNames.Login.route) {
                popUpTo(nav.graph.id) { inclusive = true }
            }
        }
    }
    // Funcion para hacer logout
    val logOut = {
        userId = -1
        toLogin()
    }
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = { if (route in bar) Menu(nav) }
    ) { innerPadding ->
        NavHost(
            navController = nav,
            startDestination = ScreenNames.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Segun el rol te manda a una pantalla u otra
            composable(ScreenNames.Login.route) {
                Login(
                    users = users,
                    onLoginSuccess = { u ->
                        userId = u.id
                        val destiny = if (u.role == role.direccion) {
                            ScreenNames.Direction.route
                        } else {
                            ScreenNames.Coordinator.route
                        }
                        nav.navigate(destiny) {
                            popUpTo(ScreenNames.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            // Pantalla de direccion
            composable(ScreenNames.Direction.route) {
                val u = actualUser
                if (u == null || u.role != role.direccion) {
                    noAccess(toLogin)
                    return@composable
                }
                Direction(user = u, onLogout = logOut)
            }

            // Pantallas de coordinacion
            composable(ScreenNames.Coordinator.route) {
                val u = actualUser
                if (u == null || u.role != role.coordinacion) {
                    noAccess(toLogin)
                    return@composable
                }
                Coordinator(user = u, onLogout = logOut)
            }
            // Pantalla para ver el staff
            composable(ScreenNames.Staff.route) {
                val u = actualUser
                if (u == null || u.role != role.coordinacion) {
                    noAccess(toLogin)
                    return@composable
                }
                StaffList(
                    user = u,
                    onPersonClick = { p ->
                        nav.navigate(ScreenNames.StaffHandle.createRoute(p.id))
                    }
                )
            }
            // Ver detalles del staff
            composable(
                route = ScreenNames.StaffHandle.route,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                val u = actualUser
                if (u == null || u.role != role.coordinacion) {
                    noAccess(toLogin)
                    return@composable
                }
                val id = backStackEntry.arguments?.getInt("id")
                // Solo puede ver al personal de su propia estancia
                val person = getPersonal().firstOrNull {
                    it.id == id && it.residence_id == u.residence_id
                }
                StaffHandle(person = person, onBack = { nav.popBackStack() })
            }
            // Ver calendario
            composable(ScreenNames.Calendar.route) {
                val u = actualUser
                if (u == null || u.role != role.coordinacion) {
                    noAccess(toLogin)
                    return@composable
                }
                Calendar(user = u)
            }
            //  Ver las tareas
            composable(ScreenNames.Tasks.route) {
                val u = actualUser
                if (u == null || u.role != role.coordinacion) {
                    noAccess(toLogin)
                    return@composable
                }
                Tasks(user = u)
            }
        }
    }
}