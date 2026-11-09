package com.example.tc2007b.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

// Ejemplo recuperado de internet y modificado para el proyecto
@Composable
fun PasswordTextField(password: String, onPasswordChange: (String) -> Unit) {
    // Para mostrar la contraseña
    var showPassword by rememberSaveable { mutableStateOf(false) }
    // Para poner la contraseña
    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = {
            Text("Contraseña")
        },
        singleLine = true,
        visualTransformation = if (showPassword) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        // Para poder mostrar o no mostrar la contraseña
        trailingIcon = {
            IconButton(onClick = {
                showPassword = !showPassword
            }) {
                Icon(
                    imageVector = if (showPassword) {
                        Icons.Filled.Visibility
                    } else {
                        Icons.Filled.VisibilityOff
                    },
                    contentDescription = "Mostrar contraseña"
                )
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        ),
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    )
}