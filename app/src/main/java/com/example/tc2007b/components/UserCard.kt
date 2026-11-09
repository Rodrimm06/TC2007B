package com.example.tc2007b.components

import android.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tc2007b.model.User
import java.util.stream.IntStream

// Tarjeta reutilizable para mostrar los usuarios
@Composable
fun UserCard(
    User: User,
    onItemClick: (User) -> Unit
){
    Card(
        modifier = Modifier.padding(8.dp)
            .fillMaxWidth().
            clickable {onItemClick(User)}
    ){// Tener una row por si se requiere agregar una imagen despues
        /*Row(
            modifier=Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
        AsyncImage(
        model = User.photo
        contentDescription = "Foto de perfil"
        modifier = Modifier.size(80.dp)
        )
         */
        Column(Modifier.padding(start = 12.dp)) {
            Text(User.name, style = MaterialTheme.typography.titleLarge)
            Text("Residencia: ${User.residence}", style = MaterialTheme.typography.bodyMedium)
        }
        //}
    }
}
