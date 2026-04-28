
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

            // Selector de Role (ARTIST / FAN)
            Text(text = "Tipo de usuario", modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.SemiBold)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { role = "ARTIST" },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (role == "ARTIST") Color(0xFF5C0F1A) else Color.Transparent,
                        contentColor = if (role == "ARTIST") Color.White else Color.Black
                    ),
                    border = BorderStroke(1.dp, Color(0xFF5C0F1A)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Artista")
                }

                Button(
                    onClick = { role = "FAN" },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (role == "FAN") Color(0xFF5C0F1A) else Color.Transparent,
                        contentColor = if (role == "FAN") Color.White else Color.Black
                    ),
                    border = BorderStroke(1.dp, Color(0xFF5C0F1A)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Fan")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

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
                        descripcion = descripcion.ifBlank { null }
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
