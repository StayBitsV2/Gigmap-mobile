package com.example.gigmap_frontend_sprint1.view

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.gigmap_frontend_sprint1.model.PostCreateRequest
import com.example.gigmap_frontend_sprint1.services.CloudinaryService
import com.example.gigmap_frontend_sprint1.viewmodel.PostViewModel
import com.example.gigmap_frontend_sprint1.viewmodel.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePost(
    navController: NavHostController,
    communityId: Int,
    postVM: PostViewModel = viewModel(),
    userVM: UserViewModel = viewModel()

) {

    val userId = userVM.currentUserId


    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()


    var content by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf<Uri?>(null) }
    var uploadedUrl by remember { mutableStateOf<String?>(null) }
    var isUploadingImage by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUrl = it
            isUploadingImage = true
            coroutineScope.launch {
                try {
                    val inputStream = context.contentResolver.openInputStream(it)
                    val tempFile = File(context.cacheDir, "temp_post_${System.currentTimeMillis()}.jpg")
                    inputStream?.use { input ->
                        FileOutputStream(tempFile).use { output ->
                            input.copyTo(output)
                        }
                    }

                    val url = withContext(Dispatchers.IO) {
                        CloudinaryService.uploadImage(tempFile)
                    }

                    if (url != null) {
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

    Scaffold(


        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F6F6))
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                "Crear publicación",
                color = Color(0xFF5C0F1A),
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Contenido
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Escribe tu publicación...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                shape = RoundedCornerShape(12.dp),
                maxLines = 6
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Imagen
            Button(
                onClick = { imagePickerLauncher.launch("image/*") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C0F1A)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !isUploadingImage
            ) {
                if (isUploadingImage) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Subiendo imagen...", color = Color.White)
                } else {
                    Icon(Icons.Default.Image, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Seleccionar imagen", color = Color.White)
                }
            }

            uploadedUrl?.let {
                Text("Imagen lista para publicar", color = Color(0xFF2E7D32), fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Botón para publicar
            Button(
                onClick = {
                    if (content.isNotBlank()) {
                        val postRequest = PostCreateRequest(
                            content = content,
                            imageUrl = uploadedUrl ?: "",
                            communityId = communityId,
                            userId = userId
                        )
                        postVM.createPost(postRequest) { success ->
                            if (success) {
                                coroutineScope.launch {


                                    println("userId actual: $userId")
                                    println("community actual: $communityId")
                                    snackbarHostState.showSnackbar("✅ Publicación creada correctamente")
                                    navController.popBackStack()
                                }
                            } else {
                                coroutineScope.launch {


                                    println("userId actual: $userId")
                                    println("community actual: $communityId")
                                    snackbarHostState.showSnackbar("❌ Error al crear la publicación")

                                }
                            }
                        }
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("⚠️ Agrega texto e imagen antes de publicar")
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C0F1A)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Publicar", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
