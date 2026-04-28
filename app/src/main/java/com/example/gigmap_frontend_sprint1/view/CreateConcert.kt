@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.gigmap_frontend_sprint1.view
//imports
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.gigmap_frontend_sprint1.model.ConcertCreateRequest
import com.example.gigmap_frontend_sprint1.model.PlatformRequest
import com.example.gigmap_frontend_sprint1.model.VenueRequest
import com.example.gigmap_frontend_sprint1.services.CloudinaryService
import com.example.gigmap_frontend_sprint1.services.GoogleMapsService
import com.example.gigmap_frontend_sprint1.viewmodel.ConcertViewModel
import com.example.gigmap_frontend_sprint1.viewmodel.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@Composable
fun CreateConcert(
    navController: NavHostController,
    concertVM: ConcertViewModel,
    userVM: UserViewModel

) {

    val userId = userVM.currentUserId // entero guardado en el ViewModel

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("") }

    var day by remember { mutableStateOf("") }
    var month by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }

    var platformName by remember { mutableStateOf("") }
    var platformImage by remember { mutableStateOf("") }

    var venueName by remember { mutableStateOf("") }
    var venueAddress by remember { mutableStateOf("") }
    var venueCapacity by remember { mutableStateOf("") }

    var imageUrl by remember { mutableStateOf<Uri?>(null) }
    var uploadedUrl by remember { mutableStateOf<String?>(null) }
    var isUploadingImage by remember { mutableStateOf(false) }

    // Estados para los dropdowns
    var genreExpanded by remember { mutableStateOf(false) }
    var platformExpanded by remember { mutableStateOf(false) }

    val genres = listOf("ROCK", "POP", "ELECTRONICA", "Urbano",  "Jazz", "Reggaeton", "Indie")
    val platforms = listOf("Joinnus", "Teleticket", "Ticketmaster")
    
    // Mapa de imágenes de plataformas
    val platformImages = mapOf(
        "Teleticket" to "https://yt3.googleusercontent.com/E4LWQc_szds7dK-uBf1MhIS_udkQDvvy-EMbJn9ei_tmjLo67QHtbNxN9K2kKcCQoKUG0Bpon60=s900-c-k-c0x00ffffff-no-rj",
        "Ticketmaster" to "https://yt3.googleusercontent.com/nTRGvdqj_uPP1sXy2K52SHlxa8cd1d4_Tfe2h_q1qGEiEtAAtU35Fr-R2yw2JbmEgtRjchoQtA=s900-c-k-c0x00ffffff-no-rj",
        "Joinnus" to "https://cdn-blog.joinnus.com/blog/wp-content/uploads/2018/12/19185753/joinnus-logo.jpg"
    )

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUrl = it
            isUploadingImage = true
            coroutineScope.launch {
                try {
                    // Convertir URI a File temporal
                    val inputStream = context.contentResolver.openInputStream(it)
                    val tempFile = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
                    inputStream?.use { input ->
                        FileOutputStream(tempFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                    
                    // Subir a Cloudinary en background thread
                    val url = withContext(Dispatchers.IO) {
                        CloudinaryService.uploadImage(tempFile)
                    }
                    
                    if (url != null) {
                        uploadedUrl = url
                        snackbarHostState.showSnackbar("Imagen subida exitosamente")
                    } else {
                        snackbarHostState.showSnackbar("Error al subir la imagen. Verifica configuración de Cloudinary")
                    }
                    
                    // Limpiar archivo temporal
                    tempFile.delete()
                } catch (e: Exception) {
                    snackbarHostState.showSnackbar("Error: ${e.message}")
                } finally {
                    isUploadingImage = false
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
            .background(Color(0xFFF6F6F6)),
        contentWindowInsets = WindowInsets(0.dp)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF6F6F6))
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Spacer(Modifier.height(20.dp))

            // Name
            Text(
                "Nombre del concierto",
                color = Color(0xFF736D6D),
                fontWeight = FontWeight.SemiBold,
                fontFamily = InterFontFamily,
                fontSize = 16.sp
            )
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = {
                    Text(
                        "Ingresa el nombre del concierto",
                        fontWeight = FontWeight.Medium,
                        fontFamily = InterFontFamily,
                        fontSize = 16.sp,
                        color = Color(0xFF8E8787)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(16.dp))

            // Date
            Text(
                "Ingresa la fecha del concierto",
                color = Color(0xFF736D6D),
                fontWeight = FontWeight.SemiBold,
                fontFamily = InterFontFamily,
                fontSize = 16.sp
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = day,
                    onValueChange = { day = it },
                    placeholder = {
                        Text(
                            "DD",
                            color = Color(0xFF8E8787),
                            fontWeight = FontWeight.Medium,
                            fontFamily = InterFontFamily,
                            fontSize = 16.sp
                        )
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = month,
                    onValueChange = { month = it },
                    placeholder = {
                        Text(
                            "MM",
                            color = Color(0xFF8E8787),
                            fontWeight = FontWeight.Medium,
                            fontFamily = InterFontFamily,
                            fontSize = 16.sp
                        )
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = year,
                    onValueChange = { year = it },
                    placeholder = {
                        Text(
                            "YYYY",
                            color = Color(0xFF8E8787),
                            fontWeight = FontWeight.Medium,
                            fontFamily = InterFontFamily,
                            fontSize = 16.sp
                        )
                    },
                    modifier = Modifier.weight(1.5f),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            // Genre
            Text(
                "Género",
                color = Color(0xFF736D6D),
                fontWeight = FontWeight.SemiBold,
                fontFamily = InterFontFamily,
                fontSize = 16.sp
            )
            ExposedDropdownMenuBox(
                expanded = genreExpanded,
                onExpandedChange = { genreExpanded = it }
            ) {
                OutlinedTextField(
                    value = genre,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = {
                        Text(
                            "Selecciona el género musical",
                            color = Color(0xFF8E8787),
                            fontWeight = FontWeight.Medium,
                            fontFamily = InterFontFamily,
                            fontSize = 16.sp
                        )
                    },
                    trailingIcon = {
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = Color(0xFF5C0F1A)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = genreExpanded,
                    onDismissRequest = { genreExpanded = false }
                ) {
                    genres.forEach { genreOption ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    genreOption,
                                    color = Color(0xFF5C0F1A),
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = InterFontFamily,
                                    fontSize = 16.sp
                                )
                            },
                            onClick = {
                                genre = genreOption
                                genreExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Description
            Text(
                "Descripción",
                color = Color(0xFF736D6D),
                fontWeight = FontWeight.SemiBold,
                fontFamily = InterFontFamily,
                fontSize = 16.sp
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = {
                    Text(
                        "Ingresa una descripción del evento",
                        color = Color(0xFF8E8787),
                        fontWeight = FontWeight.Medium,
                        fontFamily = InterFontFamily,
                        fontSize = 16.sp
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                maxLines = 3
            )

            Spacer(Modifier.height(16.dp))

            // Image
            Text(
                "Imagen",
                color = Color(0xFF736D6D),
                fontWeight = FontWeight.SemiBold,
                fontFamily = InterFontFamily,
                fontSize = 16.sp
            )
            Button(
                onClick = { imagePickerLauncher.launch("image/*") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C0F1A)),
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !isUploadingImage
            ) {
                if (isUploadingImage) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Subiendo imagen...", color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontFamily = InterFontFamily,
                        fontSize = 16.sp)
                } else {
                    Icon(Icons.Default.Image, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Cargar una imagen", color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontFamily = InterFontFamily,
                        fontSize = 16.sp)
                }
            }

            uploadedUrl?.let {
                Text("Imagen subida correctamente ", color = Color(0xFF2E7D32), fontSize = 12.sp)
            }

            Spacer(Modifier.height(16.dp))

            // PLatform
            Text(
                "Plataforma",
                color = Color(0xFF736D6D),
                fontWeight = FontWeight.SemiBold,
                fontFamily = InterFontFamily,
                fontSize = 16.sp
            )
            ExposedDropdownMenuBox(
                expanded = platformExpanded,
                onExpandedChange = { platformExpanded = it }
            ) {
                OutlinedTextField(
                    value = platformName,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = {
                        Text(
                            "Selecciona la plataforma de venta",
                            color = Color(0xFF8E8787),
                            fontWeight = FontWeight.Medium,
                            fontFamily = InterFontFamily,
                            fontSize = 16.sp
                        )
                    },
                    trailingIcon = {
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = Color(0xFF5C0F1A)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = platformExpanded,
                    onDismissRequest = { platformExpanded = false }
                ) {
                    platforms.forEach { platformOption ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    platformOption,
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.SemiBold,

                                )
                            },
                            onClick = {
                                platformName = platformOption
                                // Platform image
                                platformImage = platformImages[platformOption] ?: ""
                                platformExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Venue
            Text(
                "Recinto",
                color = Color(0xFF736D6D),
                fontWeight = FontWeight.SemiBold,
                fontFamily = InterFontFamily,
                fontSize = 16.sp
            )
            OutlinedTextField(
                value = venueName,
                onValueChange = { venueName = it },
                placeholder = {
                    Text(
                        "Ingresa el nombre del recinto",
                        color = Color(0xFF8E8787),
                        fontWeight = FontWeight.Medium,
                        fontFamily = InterFontFamily,
                        fontSize = 16.sp
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(16.dp))

            // Address
            Text(
                "Dirección del recinto",
                color = Color(0xFF736D6D),
                fontWeight = FontWeight.SemiBold,
                fontFamily = InterFontFamily,
                fontSize = 16.sp
            )
            OutlinedTextField(
                value = venueAddress,
                onValueChange = { venueAddress = it },
                placeholder = {
                    Text(
                        "Ingresa la dirección completa",
                        color = Color(0xFF8E8787),
                        fontWeight = FontWeight.Medium,
                        fontFamily = InterFontFamily,
                        fontSize = 16.sp
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(16.dp))

            // Cpacity
            Text(
                "Capacidad",
                color = Color(0xFF736D6D),
                fontWeight = FontWeight.SemiBold,
                fontFamily = InterFontFamily,
                fontSize = 16.sp
            )
            OutlinedTextField(
                value = venueCapacity,
                onValueChange = { venueCapacity = it },
                placeholder = {
                    Text(
                        "Capacidad del recinto",
                        color = Color(0xFF8E8787),
                        fontWeight = FontWeight.Medium,
                        fontFamily = InterFontFamily,
                        fontSize = 16.sp
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(24.dp))

            // Create Button
            Button(
                onClick = {
                    coroutineScope.launch {
                        // Validación 1: Campos básicos
                        if (title.isBlank() || description.isBlank() || genre.isBlank()) {
                            snackbarHostState.showSnackbar("Completa todos los campos requeridos")
                            return@launch
                        }

                        // Validación 2: Fecha
                        if (day.isBlank() || month.isBlank() || year.isBlank()) {
                            snackbarHostState.showSnackbar("Ingresa la fecha completa del concierto")
                            return@launch
                        }

                        // Validación 3: Imagen obligatoria
                        if (uploadedUrl.isNullOrBlank()) {
                            snackbarHostState.showSnackbar("Debes subir una imagen del concierto")
                            return@launch
                        }

                        // Validación 4: Plataforma
                        if (platformName.isBlank()) {
                            snackbarHostState.showSnackbar("Selecciona una plataforma de venta")
                            return@launch
                        }

                        // Validación 5: Recinto
                        if (venueName.isBlank()) {
                            snackbarHostState.showSnackbar("Ingresa el nombre del recinto")
                            return@launch
                        }

                        // Validación 6: Dirección
                        if (venueAddress.isBlank()) {
                            snackbarHostState.showSnackbar("Ingresa la dirección del recinto")
                            return@launch
                        }

                        // Validación 7: Capacidad
                        if (venueCapacity.isBlank() || venueCapacity.toIntOrNull() == null) {
                            snackbarHostState.showSnackbar("Ingresa una capacidad válida")
                            return@launch
                        }

                        // Obtener coordenadas desde la dirección
                        val coordinates = GoogleMapsService.getLatLngFromAddress(venueAddress)
                        if (coordinates == null) {
                            snackbarHostState.showSnackbar("No se pudo geocodificar la dirección")
                            return@launch
                        }

                        val (lat, lng) = coordinates

                        // Formatear fecha en ISO 8601 con hora actual (sin milisegundos)
                        val dayFormatted = day.padStart(2, '0')
                        val monthFormatted = month.padStart(2, '0')
                        val currentTime = java.time.LocalTime.now()
                        val hourFormatted = currentTime.hour.toString().padStart(2, '0')
                        val minuteFormatted = currentTime.minute.toString().padStart(2, '0')
                        val secondFormatted = currentTime.second.toString().padStart(2, '0')
                        val dateISO = "$year-$monthFormatted-${dayFormatted}T$hourFormatted:$minuteFormatted:$secondFormatted"

                        val concertRequest = ConcertCreateRequest(
                            title = title,
                            description = description,
                            imageUrl = uploadedUrl!!,
                            date = dateISO,
                            genre = genre,
                            platform = PlatformRequest(platformName, platformImage),
                            venue = VenueRequest(
                                name = venueName,
                                address = venueAddress,
                                latitude = lat,
                                longitude = lng,
                                capacity = venueCapacity.toInt()
                            ),
                            userId = userId
                        )

                        concertVM.createConcert(concertRequest) { success ->
                            coroutineScope.launch {
                                if (success) {
                                    snackbarHostState.showSnackbar("Concierto creado correctamente 🎉")
                                    navController.popBackStack()
                                } else {
                                    snackbarHostState.showSnackbar("Error al crear concierto. Verifica los datos.")
                                }
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C0F1A)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text(
                    "Crear",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontFamily = InterFontFamily,
                    fontSize = 20.sp
                )
            }
        }
    }
}
