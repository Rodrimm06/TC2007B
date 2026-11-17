package com.example.tc2007b.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tc2007b.ui.theme.Line
import com.example.tc2007b.ui.theme.Muted
import com.example.tc2007b.ui.theme.Teal

// Botón principal de la aplicación
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    icon: ImageVector? = null,
    enabled: Boolean = true,
) {
    Button(
        onClick,
        Modifier.fillMaxWidth().height(46.dp),
        enabled = enabled,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(containerColor = Teal),
    ) {
        if (icon != null) {
            Icon(icon, null, Modifier.size(17.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

// Botón secundario
@Composable
fun SecondaryButton(text: String, onClick: () -> Unit, icon: ImageVector? = null) {
    OutlinedButton(
        onClick,
        Modifier.fillMaxWidth().height(44.dp),
        shape = RoundedCornerShape(50),
        border = androidx.compose.foundation.BorderStroke(1.dp, Line),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Teal),
    ) {
        if (icon != null) {
            Icon(icon, null, Modifier.size(17.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

// Aviso de que se usan datos de demostración
@Composable
fun DemoNote(text: String = "Datos de demostración") {
    Text(
        text,
        Modifier.fillMaxWidth().padding(vertical = 14.dp),
        textAlign = TextAlign.Center,
        color = Muted,
        fontSize = 10.sp,
    )
}
