package com.example.tc2007b.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun LoginText(modifier: Modifier){
    var user by rememberSaveable() { mutableStateOf("") }
    var error by rememberSaveable() { mutableStateOf("")}
    OutlinedTextField(
        value = user,
        onValueChange = {
            user = it
            error = ""
        },
        label = {
            Text("Usuario")
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text
        ),
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    )
}