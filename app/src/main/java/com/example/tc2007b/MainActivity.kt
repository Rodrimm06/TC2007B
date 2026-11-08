package com.example.tc2007b

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tc2007b.model.User
import com.example.tc2007b.model.getUser
import com.example.tc2007b.navigation.ScreenNames
import com.example.tc2007b.ui.theme.TC2007BTheme
import com.example.tc2007b.screens.Login
import com.example.tc2007b.screens.StaffHandle

import com.example.tc2007b.model.getUser
import com.example.tc2007b.screens.Menu

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TC2007BTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainNavigation(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
@Preview
@Composable
fun MainNavigation(modifier: Modifier = Modifier) {
    val nav = rememberNavController()
    val users = remember { getUser() }
    NavHost(
        navController = nav,
        startDestination = ScreenNames.Login.route,
        modifier = modifier
    ) {
        composable(ScreenNames.Login.route) {
            Menu(modifier = Modifier)
        }
        composable(
            route = ScreenNames.StaffHandle.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id")
            val user = users.firstOrNull { it.id == id }
            StaffHandle(user = user, modifier)
        }
        composable(
            route = ScreenNames.Menu.route,
        ) { backStackEntry ->
            Menu(modifier)
        }


    }
}