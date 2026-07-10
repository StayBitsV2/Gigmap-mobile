package com.example.gigmap_frontend_sprint1.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
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
import com.example.gigmap_frontend_sprint1.viewmodel.ForumViewModel
import com.example.gigmap_frontend_sprint1.viewmodel.UserViewModel
import kotlinx.coroutines.launch

private val threadReactions = listOf(
    "LIKE" to "👍",
    "LOVE" to "❤️",
    "LAUGH" to "😂",
    "WOW" to "😮",
    "SAD" to "😢"
)

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ThreadDetail(
    navController: NavHostController,
    threadId: Int,
    forumVM: ForumViewModel = viewModel(),
    userVM: UserViewModel = viewModel()
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(threadId) {
        forumVM.getThreadDetail(threadId)
    }

    val detail = forumVM.threadDetail
    val isLoading = forumVM.isLoading

    var commentText by remember { mutableStateOf("") }
    var showReportDialog by remember { mutableStateOf(false) }
    var reportReason by remember { mutableStateOf("") }
    var reportTargetId by remember { mutableStateOf<Int?>(null) }
    var reportIsThread by remember { mutableStateOf(true) }

    if (isLoading && detail == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF5C0F1A))
        }
        return
    }

    if (detail == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Hilo no encontrado")
        }
        return
    }

    if (showReportDialog) {
        AlertDialog(
            onDismissRequest = {
                showReportDialog = false
                reportReason = ""
            },
            title = { Text("Reportar contenido") },
            text = {
                Column {
                    Text("¿Por qué reportas este contenido?")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = reportReason,
                        onValueChange = { reportReason = it },
                        placeholder = { Text("Motivo del reporte") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (reportReason.isNotBlank()) {
                            val uid = userVM.currentUserId
                            if (uid == 0) return@TextButton
                            if (reportIsThread) {
                                forumVM.reportThread(reportTargetId ?: threadId, reportReason, uid) { }
                            } else {
                                forumVM.reportComment(reportTargetId ?: 0, reportReason, uid) { }
                            }
                            showReportDialog = false
                            reportReason = ""
                        }
                    }
                ) {
                    Text("Enviar reporte", color = Color(0xFF5C0F1A))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showReportDialog = false
                    reportReason = ""
                }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { padding ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(Color.White)
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            // Header del hilo
            item {
                Column(modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)) {
                    Text(
                        text = detail.thread.title ?: "Sin título",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Usuario #${detail.thread.userId}",
                            color = Color(0xFF5C0F1A),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable {
                                navController.navigate("user/${detail.thread.userId}")
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Hace poco", color = Color.Gray, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = detail.thread.content, fontSize = 14.sp)
                    detail.thread.image?.takeIf { it.isNotBlank() }?.let { img ->
                        Spacer(modifier = Modifier.height(12.dp))
                        GlideImage(
                            model = img,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            // Barra de reacciones
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    threadReactions.forEach { (apiEmoji, displayEmoji) ->
                        val count = detail.reactions.count { it.emoji == apiEmoji }
                        val userActiveReaction = detail.reactions.find { it.userId == userVM.currentUserId }
                        val isSelected = userActiveReaction?.emoji == apiEmoji
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = displayEmoji,
                                fontSize = 24.sp,
                                modifier = Modifier
                                    .clickable {
                                        val uid = userVM.currentUserId
                                        if (uid == 0) {
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Debes iniciar sesión para reaccionar")
                                            }
                                            return@clickable
                                        }
                                        forumVM.toggleThreadReaction(threadId, apiEmoji, uid) { success ->
                                            coroutineScope.launch {
                                                if (!success) {
                                                    snackbarHostState.showSnackbar("No se pudo actualizar la reacción")
                                                }
                                            }
                                        }
                                    }
                                    .background(
                                        if (isSelected) Color(0x22F5C0F1A) else Color.Transparent,
                                        CircleShape
                                    )
                                    .padding(6.dp)
                            )
                            Text(
                                text = "$count",
                                fontSize = 11.sp,
                                color = if (isSelected) Color(0xFF5C0F1A) else Color.Gray,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
                HorizontalDivider(color = Color(0xFFE0E0E0))
            }

            // Acción de reportar hilo
            item {
                TextButton(
                    onClick = {
                        reportTargetId = threadId
                        reportIsThread = true
                        showReportDialog = true
                    },
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Text("Reportar hilo", color = Color.Gray, fontSize = 13.sp)
                }
                HorizontalDivider(color = Color(0xFFE0E0E0))
            }

            // Título de comentarios
            item {
                Text(
                    text = "Comentarios (${detail.comments.size})",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }

            // Lista de comentarios
            if (detail.comments.isEmpty()) {
                item {
                    Text(
                        "No hay comentarios aún. ¡Sé el primero!",
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
            } else {
                items(detail.comments) { comment ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF5C0F1A)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = comment.userName.take(1).uppercase(),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = comment.userName,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        modifier = Modifier.clickable {
                                            navController.navigate("user/${comment.userId}")
                                        }
                                    )
                                    Text(
                                        text = comment.createdAt ?: "Hace poco",
                                        color = Color.Gray,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = comment.content, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            TextButton(
                                onClick = {
                                    reportTargetId = comment.id
                                    reportIsThread = false
                                    showReportDialog = true
                                },
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("Reportar", color = Color.Gray, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }

        // Campo de comentario fijo al fondo
        HorizontalDivider(color = Color(0xFFE0E0E0))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = commentText,
                onValueChange = { commentText = it },
                placeholder = { Text("Escribe un comentario...") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF5C0F1A),
                    unfocusedBorderColor = Color(0xFF5C0F1A)
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (commentText.isBlank()) return@IconButton
                    val uid = userVM.currentUserId
                    if (uid == 0) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Debes iniciar sesión para comentar")
                        }
                        return@IconButton
                    }
                    val textToSend = commentText
                    forumVM.createComment(threadId, textToSend, uid) { success, _ ->
                        coroutineScope.launch {
                            if (success) {
                                commentText = ""
                            } else {
                                snackbarHostState.showSnackbar("No se pudo publicar el comentario")
                            }
                        }
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF5C0F1A))
            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = "Enviar",
                    tint = Color.White
                )
            }
        }
    }
    }
}
