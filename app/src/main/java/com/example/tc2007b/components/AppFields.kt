package com.example.tc2007b.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tc2007b.ui.theme.Ink
import com.example.tc2007b.ui.theme.Line
import com.example.tc2007b.ui.theme.Muted
import com.example.tc2007b.ui.theme.Teal

// Campo para escribir información
@Composable
fun Field(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    icon: ImageVector? = null,
    password: Boolean = false,
    error: Boolean = false,
) {
    var visible by rememberSaveable { mutableStateOf(false) }
    Text(label, color = if (error) Color(0xFFD83B30) else Muted, fontSize = 10.sp)
    Spacer(Modifier.height(5.dp))
    OutlinedTextField(
        value,
        onChange,
        Modifier.fillMaxWidth(),
        singleLine = true,
        isError = error,
        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp, color = Ink),
        visualTransformation =
            if (password && !visible) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon =
            if (password)
                ({
                    IconButton(onClick = { visible = !visible }) {
                        Icon(
                            if (visible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            if (visible) "Ocultar contraseña" else "Mostrar contraseña",
                            Modifier.size(18.dp),
                            tint = Muted,
                        )
                    }
                })
            else if (icon != null) ({ Icon(icon, null, Modifier.size(18.dp), tint = Muted) })
            else null,
        shape = RoundedCornerShape(6.dp),
        colors =
            OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Teal,
                unfocusedBorderColor = Line,
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
            ),
    )
}

// Lista para seleccionar una opción
@Composable
fun Choice(label: String, value: String, options: List<String>, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text(label, color = Muted, fontSize = 10.sp)
        Spacer(Modifier.height(5.dp))
        Box {
            Row(
                Modifier.fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .border(1.dp, Line, RoundedCornerShape(6.dp))
                    .background(Color.White, RoundedCornerShape(6.dp))
                    .clickable { expanded = true }
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(value, color = Ink, fontSize = 12.sp, modifier = Modifier.weight(1f))
                Icon(Icons.Default.ExpandMore, null, Modifier.size(18.dp), tint = Muted)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, fontSize = 13.sp) },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}
