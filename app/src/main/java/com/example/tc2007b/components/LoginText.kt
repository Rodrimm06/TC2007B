package com.example.tc2007b.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun LoginText(modifier: Modifier, user: String, onUserChange: (String) -> Unit){
    // Linea para ingresar texto
    OutlinedTextField(
        value = user,
        onValueChange = onUserChange,
        label = {
            Text("Usuario")
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text
        ),
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
    )
}