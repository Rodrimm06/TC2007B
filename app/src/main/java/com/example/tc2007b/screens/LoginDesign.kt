package com.example.tc2007b.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.PeopleOutline
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tc2007b.components.DemoNote
import com.example.tc2007b.components.Field
import com.example.tc2007b.components.PrimaryButton
import com.example.tc2007b.components.SecondaryButton
import com.example.tc2007b.model.User
import com.example.tc2007b.model.getUser
import com.example.tc2007b.model.role
import com.example.tc2007b.ui.theme.Ink
import com.example.tc2007b.ui.theme.Muted
import com.example.tc2007b.ui.theme.PaleBlue
import com.example.tc2007b.ui.theme.SoftGray
import com.example.tc2007b.ui.theme.Teal

// Pantalla para iniciar sesión o entrar a la demostración
@Composable
fun LoginMockup(onLogin: (User) -> Unit) {
    var name by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Column(
            Modifier.fillMaxWidth()
                .background(PaleBlue, RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
                .padding(horizontal = 24.dp, vertical = 28.dp)
        ) {
            Text("FUNDACIÓN ESTANCIAS INFANTILES", color = Color(0xFF376B85), fontSize = 10.sp)
            Spacer(Modifier.height(9.dp))
            Text(
                "Sonrisas de Cristal",
                fontSize = 26.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Ink,
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PeopleOutline, null, Modifier.size(16.dp), tint = Teal)
                Spacer(Modifier.width(6.dp))
                Text(
                    "Control de Personal",
                    color = Teal,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Spacer(Modifier.height(10.dp))
            Text("Cuidamos a quienes cuidan.", color = Color(0xFF527C92), fontSize = 11.sp)
        }
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Column {
                Text(
                    "Bienvenida de nuevo",
                    color = Ink,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text("Ingresa con tu cuenta de la fundación.", color = Muted, fontSize = 11.sp)
            }
            // Campo para ingresar el usuario
            Column {
                Field(
                    "Usuario",
                    name,
                    {
                        name = it
                        error = false
                    },
                    Icons.Default.MailOutline,
                    error = error,
                )
            }
            // Campo para ingresar la contraseña
            Column {
                Field(
                    "Contraseña",
                    password,
                    {
                        password = it
                        error = false
                    },
                    password = true,
                    error = error,
                )
            }
            Row(
                Modifier.fillMaxWidth()
                    .background(
                        if (error) Color(0xFFFBEAE8) else SoftGray,
                        RoundedCornerShape(8.dp),
                    )
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    if (error) Icons.Default.ErrorOutline else Icons.Default.Shield,
                    null,
                    Modifier.size(17.dp),
                    tint = if (error) Color(0xFFCA5148) else Teal,
                )
                Spacer(Modifier.width(9.dp))
                Text(
                    if (error)
                        "Usuario o contraseña incorrectos. Verifica tus datos e inténtalo de nuevo."
                    else "Tu cuenta reconoce tu perfil y la información que puedes consultar.",
                    color = if (error) Color(0xFFBB3D33) else Muted,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                )
            }
            PrimaryButton(
                "Iniciar sesión",
                {
                    // Busca un usuario que coincida
                    val found =
                        getUser().firstOrNull {
                            it.name.equals(name.trim(), ignoreCase = true) &&
                                it.password == password
                        }
                    if (found == null) error = true else onLogin(found)
                },
            )
            Spacer(Modifier.height(2.dp))
            Text(
                "SIMULAR ACCESO · SOLO DEMOSTRACIÓN",
                color = Ink,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
            )
            // Botones para probar los dos roles
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.weight(1f)) {
                    SecondaryButton(
                        "Coordinadora",
                        {
                            onLogin(
                                User(100, "Mariana", "", "Estancia Arcoíris", 1, role.coordinacion)
                            )
                        },
                        Icons.Default.PersonOutline,
                    )
                }
                Box(Modifier.weight(1f)) {
                    SecondaryButton(
                        "Dirección",
                        { onLogin(User(101, "Gabriela", "", "", 0, role.direccion)) },
                        Icons.Default.Apartment,
                    )
                }
            }
            Text(
                "En producción, el perfil se asigna a tu cuenta; no se elige al iniciar sesión.",
                color = Muted,
                fontSize = 10.sp,
            )
            DemoNote()
        }
    }
}
