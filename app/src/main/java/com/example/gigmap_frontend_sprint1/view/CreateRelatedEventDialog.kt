package com.example.gigmap_frontend_sprint1.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.example.gigmap_frontend_sprint1.model.RelatedEventCreateRequest
import com.example.gigmap_frontend_sprint1.model.Venue
import com.example.gigmap_frontend_sprint1.services.GoogleMapsService
import com.example.gigmap_frontend_sprint1.viewmodel.RelatedEventViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateRelatedEventDialog(
    concertId: Int,
    currentUserId: Int?,
    relatedEventVM: RelatedEventViewModel,
    onDismiss: () -> Unit
) {
    var titulo by remember { mutableStateOf("") }
    var day by remember { mutableStateOf("") }
    var month by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("AFTERPARTY") }
    var status by remember { mutableStateOf("BORRADOR") }
    var descripcion by remember { mutableStateOf("") }
    var venueName by remember { mutableStateOf("") }
    var venueAddress by remember { mutableStateOf("") }
    var capacityText by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()
    val primary = BurgundyDark

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        containerColor = Color(0xFFF7F5F5),
        title = {
            Text(
                text = "Crear evento relacionado",
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Nombre del evento", fontFamily = InterFontFamily, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    placeholder = { Text("Ingresa el nombre del evento", fontFamily = InterFontFamily) }
                )

                Spacer(Modifier.height(12.dp))

                Text("Ingresa la fecha del evento", fontFamily = InterFontFamily, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = day,
                        onValueChange = { if (it.length <= 2) day = it },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        placeholder = { Text("DD", fontFamily = InterFontFamily) }
                    )
                    OutlinedTextField(
                        value = month,
                        onValueChange = { if (it.length <= 2) month = it },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        placeholder = { Text("MM", fontFamily = InterFontFamily) }
                    )
                    OutlinedTextField(
                        value = year,
                        onValueChange = { if (it.length <= 4) year = it },
                        modifier = Modifier.weight(1.4f),
                        shape = RoundedCornerShape(16.dp),
                        placeholder = { Text("YYYY", fontFamily = InterFontFamily) }
                    )
                }

                Spacer(Modifier.height(12.dp))

                Text("Tipo de evento", fontFamily = InterFontFamily, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { tipo = "PREVIA" },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            width = 1.dp,
                            brush = androidx.compose.ui.graphics.SolidColor(primary)
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (tipo == "PREVIA") primary else Color.Transparent,
                            contentColor = if (tipo == "PREVIA") Color.White else primary
                        )
                    ) { Text("Reunion", fontFamily = InterFontFamily) }

                    OutlinedButton(
                        onClick = { tipo = "AFTERPARTY" },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            width = 1.dp,
                            brush = androidx.compose.ui.graphics.SolidColor(primary)
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (tipo == "AFTERPARTY") primary else Color.Transparent,
                            contentColor = if (tipo == "AFTERPARTY") Color.White else primary
                        )
                    ) { Text("AfterParty", fontFamily = InterFontFamily) }
                }

                Spacer(Modifier.height(12.dp))

                Text("Descripcion", fontFamily = InterFontFamily, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { if (it.length <= 400) descripcion = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    maxLines = 3,
                    placeholder = { Text("Ingresa una descripcion", fontFamily = InterFontFamily) }
                )
                Text(
                    text = "Maximo 400 caracteres.",
                    fontFamily = InterFontFamily,
                    color = Color.Gray,
                    fontSize = MaterialTheme.typography.bodySmall.fontSize,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(Modifier.height(12.dp))

                Text("Estado", fontFamily = InterFontFamily, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { status = "BORRADOR" },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            width = 1.dp,
                            brush = androidx.compose.ui.graphics.SolidColor(primary)
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (status == "BORRADOR") primary else Color.Transparent,
                            contentColor = if (status == "BORRADOR") Color.White else primary
                        )
                    ) { Text("Borrador", fontFamily = InterFontFamily) }

                    OutlinedButton(
                        onClick = { status = "PUBLICADO" },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            width = 1.dp,
                            brush = androidx.compose.ui.graphics.SolidColor(primary)
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (status == "PUBLICADO") primary else Color.Transparent,
                            contentColor = if (status == "PUBLICADO") Color.White else primary
                        )
                    ) { Text("Publicado", fontFamily = InterFontFamily) }
                }

                Spacer(Modifier.height(12.dp))

                Text("Lugar", fontFamily = InterFontFamily, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(
                    value = venueName,
                    onValueChange = { venueName = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    label = { Text("Nombre del local", fontFamily = InterFontFamily) }
                )
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = venueAddress,
                    onValueChange = { venueAddress = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    label = { Text("Direccion", fontFamily = InterFontFamily) }
                )
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = capacityText,
                    onValueChange = { capacityText = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    label = { Text("Capacidad", fontFamily = InterFontFamily) }
                )

                Spacer(Modifier.height(8.dp))
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    coroutineScope.launch {
                        if (titulo.isBlank() || day.isBlank() || month.isBlank()
                            || year.isBlank() || venueName.isBlank() || venueAddress.isBlank()
                        ) return@launch

                        val coordinates = GoogleMapsService.getLatLngFromAddress(venueAddress)
                            ?: return@launch

                        val (lat, lng) = coordinates
                        val capacity = capacityText.toIntOrNull() ?: 0
                        val dayFormatted = day.padStart(2, '0')
                        val monthFormatted = month.padStart(2, '0')
                        val now = java.time.LocalTime.now()
                        val h = now.hour.toString().padStart(2, '0')
                        val m = now.minute.toString().padStart(2, '0')
                        val s = now.second.toString().padStart(2, '0')
                        val dateISO = "$year-$monthFormatted-${dayFormatted}T$h:$m:$s.000Z"

                        val venue = Venue(
                            name = venueName,
                            address = venueAddress,
                            latitude = lat,
                            longitude = lng,
                            capacity = capacity
                        )

                        val request = RelatedEventCreateRequest(
                            concertId = concertId,
                            titulo = titulo,
                            datehour = dateISO,
                            descripcion = descripcion,
                            tipo = tipo,
                            venue = venue,
                            status = status,
                            organizadorId = currentUserId ?: 0
                        )

                        relatedEventVM.createRelatedEvent(request) { success ->
                            if (success) onDismiss()
                        }
                    }
                }
            ) {
                Text("Crear", fontFamily = InterFontFamily, fontWeight = FontWeight.SemiBold, color = primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", fontFamily = InterFontFamily, fontWeight = FontWeight.Medium, color = Color.Gray)
            }
        }
    )
}

