package com.example.tc2007b.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tc2007b.ui.theme.Background
import com.example.tc2007b.ui.theme.Ink
import com.example.tc2007b.ui.theme.Muted
import com.example.tc2007b.ui.theme.PaleBlue

// Encabezado que se utiliza en varias pantallas
@Composable
fun ScreenHeader(
    title: String,
    subtitle: String,
    badge: String? = null,
    back: (() -> Unit)? = null,
    onLogout: (() -> Unit)? = null,
) {
    Column(
        Modifier.fillMaxWidth().background(PaleBlue).padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            if (back != null) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    "Volver",
                    Modifier.size(19.dp).clickable { back() },
                    tint = Muted,
                )
                Spacer(Modifier.width(10.dp))
            }
            Text(
                "Sonrisas de Cristal",
                color = Color(0xFF376B85),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
            )
            if (badge != null)
                Text(
                    badge,
                    color = Muted,
                    fontSize = 10.sp,
                    modifier =
                        Modifier.background(Background, RoundedCornerShape(50))
                            .padding(horizontal = 9.dp, vertical = 4.dp),
                )
            if (onLogout != null) {
                IconButton(onClick = onLogout, modifier = Modifier.size(30.dp)) {
                    Icon(Icons.Default.Logout, "Cerrar sesión", Modifier.size(17.dp), tint = Muted)
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(title, color = Ink, fontSize = 23.sp, lineHeight = 27.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(7.dp))
        Text(subtitle, color = Color(0xFF527C92), fontSize = 11.sp)
    }
}
