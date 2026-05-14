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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gigmap_frontend_sprint1.model.RelatedEventCreateRequest
import com.example.gigmap_frontend_sprint1.model.Venue
import com.example.gigmap_frontend_sprint1.services.GoogleMapsService
import com.example.gigmap_frontend_sprint1.viewmodel.RelatedEventViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime



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

    val snackbarHostState = remember { SnackbarHostState() }

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
                

                    

                Text(
                    "Nombre del evento",
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(4.dp))

                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    placeholder = {
                        Text(
                            "Ingresa el nombre del evento",
                            fontFamily = InterFontFamily
                        )
                    }
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    "Ingresa la fecha del evento",
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    OutlinedTextField(
                        value = day,
                        onValueChange = {
                            if (it.length <= 2) day = it
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        placeholder = {
                            Text("DD", fontFamily = InterFontFamily)
                        }
                    )

                    OutlinedTextField(
                        value = month,
                        onValueChange = {
                            if (it.length <= 2) month = it
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        placeholder = {
                            Text("MM", fontFamily = InterFontFamily)
                        }
                    )

                    OutlinedTextField(
                        value = year,
                        onValueChange = {
                            if (it.length <= 4) year = it
                        },
                        modifier = Modifier.weight(1.4f),
                        shape = RoundedCornerShape(16.dp),
                        placeholder = {
                            Text("YYYY", fontFamily = InterFontFamily)
                        }
                    )
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    "Lugar",
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(4.dp))

                OutlinedTextField(
                    value = venueName,
                    onValueChange = { venueName = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    label = {
                        Text("Nombre del local")
                    }
                )

                Spacer(Modifier.height(6.dp))

                OutlinedTextField(
                    value = venueAddress,
                    onValueChange = { venueAddress = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    label = {
                        Text("Direccion")
                    }
                )

                Spacer(Modifier.height(6.dp))

                OutlinedTextField(
                    value = capacityText,
                    onValueChange = { capacityText = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    label = {
                        Text("Capacidad")
                    }
                )
                SnackbarHost(
                hostState = snackbarHostState,
                 modifier = Modifier.fillMaxWidth()
                )
            }
        },

        confirmButton = {

            TextButton(

                onClick = {

                    coroutineScope.launch {

                        

                        // VALIDAR CAMPOS

                        if (
                            titulo.isBlank() ||
                            day.isBlank() ||
                            month.isBlank() ||
                            year.isBlank() ||
                            venueName.isBlank() ||
                            venueAddress.isBlank()
                        ) {

                            snackbarHostState.showSnackbar("Completa todos los campos obligatorios")

                            return@launch
                        }

                        // VALIDAR CAPACIDAD

                        val capacity = capacityText.toIntOrNull()

                        if (capacity == null) {

                            snackbarHostState.showSnackbar("La capacidad debe ser un número")

                            return@launch
                        }

                        if (capacity < 5000 || capacity > 80000) {

                               snackbarHostState.showSnackbar("La capacidad debe estar entre 5000 y 80000")

                            return@launch
                        }

                        // VALIDAR FECHA

                        try {

                            val selectedDate = LocalDate.of(
                                year.toInt(),
                                month.toInt(),
                                day.toInt()
                            )

                            val today = LocalDate.now()

                            if (selectedDate.isBefore(today)) {

                               snackbarHostState.showSnackbar("La fecha no puede ser menor a hoy")

                                return@launch
                            }

                        } catch (e: Exception) {

                            snackbarHostState.showSnackbar("La fecha ingresada no es válida")

                            return@launch
                        }

                        // COORDENADAS

                        val coordinates =
                            GoogleMapsService.getLatLngFromAddress(
                                venueAddress
                            )

                        if (coordinates == null) {

                            snackbarHostState.showSnackbar("No se pudo encontrar la dirección")

                            return@launch
                        }

                        val (lat, lng) = coordinates

                        // FECHA ISO

                        val dayFormatted =
                            day.padStart(2, '0')

                        val monthFormatted =
                            month.padStart(2, '0')

                        val now = LocalTime.now()

                        val h =
                            now.hour.toString().padStart(2, '0')

                        val m =
                            now.minute.toString().padStart(2, '0')

                        val s =
                            now.second.toString().padStart(2, '0')

                        val dateISO =
                            "$year-$monthFormatted-$dayFormatted" +
                                    "T$h:$m:$s.000Z"

                        // VENUE

                        val venue = Venue(
                            name = venueName,
                            address = venueAddress,
                            latitude = lat,
                            longitude = lng,
                            capacity = capacity
                        )

                        // REQUEST

                        val request =
                            RelatedEventCreateRequest(
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

                            coroutineScope.launch {
                            if (success) {
                                snackbarHostState.showSnackbar("Evento creado correctamente")
                                onDismiss()

                            } else {

                                 snackbarHostState.showSnackbar("No se pudo crear el evento")
                            }
                         }
                        }
                    }
                }
            ) {

                Text(
                    "Crear",
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    color = primary
                )
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {

                Text(
                    "Cancelar",
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
            }
        }
    )
    
}