package com.example.tc2007b.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tc2007b.components.LoginText
import com.example.tc2007b.components.PasswordTextField
import com.example.tc2007b.model.User
import com.example.tc2007b.model.getUser

@Preview(showBackground = true)
@Composable
fun Login(
    modifier: Modifier = Modifier,
    users: List<User> = getUser(),
    onLoginSuccess: (User) -> Unit = {}
){
    var user by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf("") }
    var forgot by rememberSaveable { mutableStateOf(false) }

    Column(modifier.padding(16.dp).fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        // Titulo
        Text("Inicio de sesion",
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.SansSerif
        )
        // Se llama la funcion de login
        LoginText(Modifier, user) {
            user = it
            error = ""
        }
        // Se llama la funcion para la contraseña
        PasswordTextField(password) {
            password = it
            error = ""
        }

        if (error.isNotEmpty()) {
            Text(error, color = MaterialTheme.colorScheme.error)
        }
        // Boton para hacer el inicio de sesión
        Button(
            onClick = {
                // Busca un usuario que coincida
                val encontrado = users.firstOrNull {
                    it.name.equals(user.trim(), ignoreCase = true) && it.password == password
                }
                // Verificacion
                if (encontrado != null) {
                    onLoginSuccess(encontrado)
                } else {
                    error = "Usuario o contraseña incorrectos"
                }
            }, Modifier.fillMaxWidth().padding(8.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Iniciar Sesion")
        }
        // Recuperar contraseña
        TextButton(onClick = { forgot = !forgot }) {
            Text("¿Olvidaste tu contraseña?")
        }
        if (forgot) {
            Text("Contacta a un administrador para recuperar tu contraseña")
        }
    }
}





