
package com.example.gigmap_frontend_sprint1.view

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.gigmap_frontend_sprint1.model.UserEditRequest
import com.example.gigmap_frontend_sprint1.viewmodel.UserViewModel
import com.example.gigmap_frontend_sprint1.services.CloudinaryService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream


@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun EditProfile(
    nav: NavHostController,
    userVM: UserViewModel = viewModel(),
    context: Context = LocalContext.current
) {
    val currentUserId = userVM.currentUserId
    val users = userVM.listaUsers
    val currentUser = remember(users, currentUserId) { users.find { it.id == currentUserId } }

    // Estados del formulario (prellenados si hay usuario)
    var name by remember { mutableStateOf(currentUser?.name ?: "") }
    var username by remember { mutableStateOf(currentUser?.username ?: "") }
    var email by remember { mutableStateOf(currentUser?.email ?: "") }
    var descripcion by remember { mutableStateOf(currentUser?.descripcion ?: "") }
    var role by remember { mutableStateOf(currentUser?.role ?: "") } // "ARTIST" / "FAN"

    // imagen (URI local y URL subida)
    var imageUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var uploadedUrl by remember { mutableStateOf<String?>(currentUser?.image ?: "") }
    var isUploadingImage by remember { mutableStateOf(false) }

    // Nuevos campos del perfil ARTISTA
    var bannerUrl by remember { mutableStateOf(currentUser?.bannerUrl ?: "") }
    var selectedGenre by remember { mutableStateOf(currentUser?.generoMusical ?: "") }
    var sitioWeb by remember { mutableStateOf(currentUser?.sitioWeb ?: "") }
    var spotifyUrl by remember { mutableStateOf(currentUser?.spotifyUrl ?: "") }
    var instagramUrl by remember { mutableStateOf(currentUser?.instagramUrl ?: "") }
    var youtubeUrl by remember { mutableStateOf(currentUser?.youtubeUrl ?: "") }
    var isUploadingBanner by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        uri?.let {
            imageUri = it
            isUploadingImage = true
            coroutineScope.launch {
                try {
                    val inputStream = context.contentResolver.openInputStream(it)
                    val tempFile = File(context.cacheDir, "temp_profile_${System.currentTimeMillis()}.jpg")
                    inputStream?.use { input -> FileOutputStream(tempFile).use { output -> input.copyTo(output) } }

                    val url = withContext(Dispatchers.IO) {
                        CloudinaryService.uploadImage(tempFile)
                    }

                    if (!url.isNullOrBlank()) {
                        uploadedUrl = url
                        snackbarHostState.showSnackbar("✅ Imagen subida correctamente")
                    } else {
                        snackbarHostState.showSnackbar("❌ Error al subir imagen")
                    }

                    tempFile.delete()
                } catch (e: Exception) {
                    snackbarHostState.showSnackbar("Error: ${e.message}")
                } finally {
                    isUploadingImage = false
                }
            }
        }
    }

    val bannerPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        uri?.let {
            isUploadingBanner = true
            coroutineScope.launch {
                try {
                    val inputStream = context.contentResolver.openInputStream(it)
                    val tempFile = File(context.cacheDir, "temp_banner_${System.currentTimeMillis()}.jpg")
                    inputStream?.use { input -> FileOutputStream(tempFile).use { output -> input.copyTo(output) } }

                    val url = withContext(Dispatchers.IO) {
                        CloudinaryService.uploadImage(tempFile)
                    }

                    if (!url.isNullOrBlank()) {
                        bannerUrl = url
                        snackbarHostState.showSnackbar("✅ Portada subida correctamente")
                    } else {
                        snackbarHostState.showSnackbar("❌ Error al subir portada")
                    }

                    tempFile.delete()
                } catch (e: Exception) {
                    snackbarHostState.showSnackbar("Error: ${e.message}")
                } finally {
                    isUploadingBanner = false
                }
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(6.dp))

            // Avatar + cambiar imagen
            Box(modifier = Modifier.size(120.dp), contentAlignment = Alignment.BottomEnd) {
                if (!uploadedUrl.isNullOrBlank()) {
                    GlideImage(
                        model = uploadedUrl,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color(0xFF5C0F1A), CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                    )
                }

                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C0F1A)),
                    modifier = Modifier
                        .height(36.dp)
                ) {
                    if (isUploadingImage) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                        Spacer(Modifier.width(6.dp))
                        Text("Subiendo", color = Color.White, fontSize = 12.sp)
                    } else {
                        Text("Cambiar", color = Color.White, fontSize = 12.sp)
                    }
                }
            }

            // Banner / Portada
            Text(
                text = "Portada",
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                if (bannerUrl.isNotBlank()) {
                    GlideImage(
                        model = bannerUrl,
                        contentDescription = "Banner",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = "Sin portada",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Button(
                onClick = { bannerPickerLauncher.launch("image/*") },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C0F1A)),
                modifier = Modifier.height(36.dp)
            ) {
                if (isUploadingBanner) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                    Spacer(Modifier.width(6.dp))
                    Text("Subiendo", color = Color.White, fontSize = 12.sp)
                } else {
                    Text("Cambiar portada", color = Color.White, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nombre
            Text(text = "Nombre", modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.SemiBold)
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF5C0F1A),
                    unfocusedBorderColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Username
            Text(text = "Username", modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.SemiBold)
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF5C0F1A),
                    unfocusedBorderColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Email
            Text(text = "Email", modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.SemiBold)
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF5C0F1A),
                    unfocusedBorderColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Descripción
            Text(text = "Descripción", modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.SemiBold)
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF5C0F1A),
                    unfocusedBorderColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Campos exclusivos para ARTISTA
            if (role == "ARTIST") {
                // Género musical
                Text(
                    text = "Género musical",
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))

                var expanded by remember { mutableStateOf(false) }
                val genres = listOf(
                    "Rock", "Pop", "Electrónica", "Urbano", "Jazz", "Indie",
                    "Clásico", "Metal", "Folk", "Country", "Reggae", "Blues",
                    "Alternative", "Punk", "Soul", "Funk", "R&B", "Latin",
                    "World", "Hip-Hop", "Other"
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedGenre,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF5C0F1A),
                            unfocusedBorderColor = Color.Gray
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        genres.forEach { genre ->
                            DropdownMenuItem(
                                text = { Text(genre) },
                                onClick = {
                                    selectedGenre = genre
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Sitio web
                Text(
                    text = "Sitio web",
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.SemiBold
                )
                OutlinedTextField(
                    value = sitioWeb,
                    onValueChange = { sitioWeb = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF5C0F1A),
                        unfocusedBorderColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Redes Sociales
                Text(
                    text = "Redes Sociales",
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Spotify",
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.SemiBold
                )
                OutlinedTextField(
                    value = spotifyUrl,
                    onValueChange = { spotifyUrl = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    placeholder = { Text("https://open.spotify.com/...") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF5C0F1A),
                        unfocusedBorderColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Instagram",
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.SemiBold
                )
                OutlinedTextField(
                    value = instagramUrl,
                    onValueChange = { instagramUrl = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    placeholder = { Text("https://instagram.com/...") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF5C0F1A),
                        unfocusedBorderColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "YouTube",
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.SemiBold
                )
                OutlinedTextField(
                    value = youtubeUrl,
                    onValueChange = { youtubeUrl = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    placeholder = { Text("https://youtube.com/@...") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF5C0F1A),
                        unfocusedBorderColor = Color.Gray
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Botón Guardar
            Button(
                onClick = {
                    // validaciones mínimas
                    if (username.isBlank() || email.isBlank()) {
                        Toast.makeText(context, "Completa username y email", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (currentUserId == 0) {
                        Toast.makeText(context, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val request = UserEditRequest(
                        email = email,
                        username = username,
                        name = name,
                        role = role.ifBlank { null },
                        imagenUrl = uploadedUrl,
                        descripcion = descripcion.ifBlank { null },
                        bannerUrl = bannerUrl,
                        generoMusical = selectedGenre,
                        sitioWeb = sitioWeb,
                        spotifyUrl = spotifyUrl,
                        instagramUrl = instagramUrl,
                        youtubeUrl = youtubeUrl
                    )

                    userVM.updateUser(currentUserId, request) { success, updatedUser ->
                        coroutineScope.launch {
                            if (success) {
                                snackbarHostState.showSnackbar("Perfil actualizado ✅")
                                userVM.getUsers()
                                nav.popBackStack()
                            } else {
                                snackbarHostState.showSnackbar("Error al actualizar perfil ❌")
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C0F1A)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Guardar cambios", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
