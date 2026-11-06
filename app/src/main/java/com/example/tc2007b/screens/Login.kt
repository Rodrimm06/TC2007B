package com.example.tc2007b.screens

import android.R.attr.label
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicSecureTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation.Companion.keyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Label
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SecureTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontFamily.Companion.SansSerif
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.tc2007b.components.LoginText
import com.example.tc2007b.components.PasswordTextField

@Preview(showBackground = true)
@Composable
fun login(
    modifier: Modifier = Modifier
){

    // Titulo de la pantalla
    Text("Inicio de sesion",
        fontSize = 24.sp,
        fontWeight = FontWeight.SemiBold,
        fontFamily = FontFamily.SansSerif
    )
    Column(modifier.padding(16.dp).fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        // Se llama la funcion que construye el login
        LoginText(modifier)
        // Se llama la funcion para la contraseña (recuperado de Internet)
        PasswordTextField()
        // Boton para hacer el inicio de sesión
        Button(
            onClick = {

            }, modifier.fillMaxWidth().padding(8.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Iniciar Sesion")
        }
        Button(
            onClick = {

            }, modifier.fillMaxWidth().padding(8.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("¿Olvidaste tu contraseña?")
        }
    }

}





