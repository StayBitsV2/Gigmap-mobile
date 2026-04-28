package com.example.gigmap_frontend_sprint1.view

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.gigmap_frontend_sprint1.services.CloudinaryService
import com.example.gigmap_frontend_sprint1.viewmodel.CommunityViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCommunity(
    nav: NavHostController,
    viewModel: CommunityViewModel = viewModel()
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val vinoColor = Color(0xFF5C0F1A)

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var localImageUri by remember { mutableStateOf<Uri?>(null) }
    var uploadedImageUrl by remember { mutableStateOf<String?>(null) }
    var isUploadingImage by remember { mutableStateOf(false) }
    var isCreating by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            localImageUri = it
            isUploadingImage = true
            coroutineScope.launch {
                try {
                    val inputStream = context.contentResolver.openInputStream(it)
                    val tempFile = File(context.cacheDir, "temp_comm_${System.currentTimeMillis()}.jpg")
                    inputStream?.use { input ->
                        FileOutputStream(tempFile).use { output ->
                            input.copyTo(output)
                        }
                    }

                    val url = withContext(Dispatchers.IO) {
                        CloudinaryService.uploadImage(tempFile)
                    }

                    if (url != null) {
                        uploadedImageUrl = url
                        snackbarHostState.showSnackbar("✅ Imagen subida correctamente")
                    } else {
                        snackbarHostState.showSnackbar("❌ Error al subir imagen")
                    }

                    tempFile.delete()
                } catch (e: Exception) {
                    snackbarHostState.showSnackbar("Error al subir imagen: ${e.message}")
                } finally {
                    isUploadingImage = false
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            // Nombre de la comunidad
            Text(
                "Nombre de la comunidad",
                fontWeight = FontWeight.SemiBold,
                color = vinoColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                textAlign = TextAlign.Start
            )
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("Ingrese el nombre de la comunidad") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true
            )

            // Descripción
            Text(
                "Descripción",
                fontWeight = FontWeight.SemiBold,
                color = vinoColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                textAlign = TextAlign.Start
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("Ingrese una descripción") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Selección de imagen
            Button(
                onClick = { imagePickerLauncher.launch("image/*") },
                enabled = !isUploadingImage,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = vinoColor),
                shape = MaterialTheme.shapes.medium
            ) {
                if (isUploadingImage) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Subiendo imagen...", color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Seleccionar imagen", color = MaterialTheme.colorScheme.onPrimary)
                }
            }

            uploadedImageUrl?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Imagen lista para usar", color = vinoColor)
            }

            Spacer(modifier = Modifier.height(20.dp))

            errorMsg?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Botón Crear comunidad
            Button(
                onClick = {
                    if (name.isBlank()) {
                        errorMsg = "Ingresa un nombre"
                        return@Button
                    }
                    errorMsg = null
                    isCreating = true

                    viewModel.createCommunity(name, uploadedImageUrl ?: "", description) { success, created ->
                        isCreating = false
                        coroutineScope.launch {
                            if (success && created != null) {
                                snackbarHostState.showSnackbar("✅ Comunidad creada")
                                nav.navigate("community/${created.id}") {
                                    popUpTo("communitiesList") { inclusive = false }
                                }
                            } else {
                                snackbarHostState.showSnackbar("❌ Error al crear comunidad")
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(containerColor = vinoColor),
                enabled = !isCreating && !isUploadingImage
            ) {
                if (isCreating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Creando...", color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Crear comunidad", color = MaterialTheme.colorScheme.onPrimary)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Botón cancelar
            OutlinedButton(
                onClick = { nav.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = vinoColor)
            ) {
                Text("Cancelar")
            }
        }
    }
}
