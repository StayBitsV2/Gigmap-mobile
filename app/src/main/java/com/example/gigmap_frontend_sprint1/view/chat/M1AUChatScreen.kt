package com.example.gigmap_frontend_sprint1.view.chat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.gigmap_frontend_sprint1.model.m1au.M1AUConcertCard
import com.example.gigmap_frontend_sprint1.viewmodel.ChatMessage
import com.example.gigmap_frontend_sprint1.viewmodel.M1AUChatViewModel
import kotlinx.coroutines.launch
import androidx.compose.ui.draw.blur
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.example.gigmap_frontend_sprint1.R
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.graphicsLayer

val InterFontFamily = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
    Font(R.font.inter_bold, FontWeight.Bold)
)

@Composable
fun M1AUChatModal(
    isOpen: MutableState<Boolean>,
    viewModel: M1AUChatViewModel = viewModel(),
    onNavigateToArtist: (String?, String?) -> Unit = { _, _ -> },
    onNavigateToConcert: (String?, String?) -> Unit = { _, _ -> }
) {
    val uiState = viewModel.uiState
    M1AUChatDialog(
        isOpen = isOpen.value,
        onDismiss = { isOpen.value = false },
        messages = uiState.messages,
        concerts = uiState.concerts,
        isLoading = uiState.isLoading,
        error = uiState.error,
        onSend = { message -> viewModel.sendMessage(message) },
        onErrorDismiss = { viewModel.dismissError() },
        onNavigateToArtist = onNavigateToArtist,
        onNavigateToConcert = onNavigateToConcert
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun M1AUChatDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    messages: List<ChatMessage>,
    concerts: List<M1AUConcertCard>,
    isLoading: Boolean,
    error: String?,
    onSend: (String) -> Unit,
    onErrorDismiss: () -> Unit,
    onNavigateToArtist: (String?, String?) -> Unit,
    onNavigateToConcert: (String?, String?) -> Unit
) {
    // 🎯 Animaciones
    val offsetX by animateDpAsState(
        targetValue = if (isOpen) 0.dp else 400.dp,
        animationSpec = tween(
            durationMillis = 260,          // puedes subir/bajar la duración
            easing = FastOutSlowInEasing   // curva suave, sin rebote
        ),
        label = "slide"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isOpen) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "fade"
    )

    if (alpha > 0f) {
        val input = remember { mutableStateOf("") }
        val scope = rememberCoroutineScope()

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // ══════════════════════════════════════════
            // 🌫️ CAPA 1: Fondo con BLUR
            // ══════════════════════════════════════════
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .blur(25.dp)
                    .graphicsLayer { this.alpha = alpha }
                    .clickable(
                        onClick = onDismiss,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    )
            )

            // ══════════════════════════════════════════
            // ✨ CAPA 2: Modal translúcido con animación
            // ══════════════════════════════════════════
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Card(
                    shape = RoundedCornerShape(
                        topStart = 24.dp,
                        bottomStart = 24.dp,
                        topEnd = 0.dp,
                        bottomEnd = 0.dp
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1A1A1A).copy(alpha = 0.95f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.90f)
                        .fillMaxHeight(0.75f)
                        .offset(x = offsetX) // 👈 Slide desde la derecha
                        .graphicsLayer { this.alpha = alpha } // 👈 Fade
                        .clickable(
                            onClick = {},
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        // ═══════════════════════════════════════
                        // 1️⃣ HEADER
                        // ═══════════════════════════════════════
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Chat con M1AU",
                                style = MaterialTheme.typography.titleLarge,
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            IconButton(onClick = onDismiss) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Cerrar",
                                    tint = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // ═══════════════════════════════════════
                        // 2️⃣ CHAT
                        // ═══════════════════════════════════════
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(messages) { message ->
                                ChatBubble(message = message)
                            }

                            if (concerts.isNotEmpty()) {
                                items(concerts) { concert ->
                                    ConcertCard(
                                        concert = concert,
                                        onNavigateToArtist = onNavigateToArtist,
                                        onNavigateToConcert = onNavigateToConcert
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // ═══════════════════════════════════════
                        // 3️⃣ INPUT + BOTÓN
                        // ═══════════════════════════════════════
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextField(
                                value = input.value,
                                onValueChange = { input.value = it },
                                placeholder = {
                                    Text(
                                        "Pregúntale a M1AU...",
                                        color = Color.White.copy(alpha = 0.5f),
                                        fontFamily = InterFontFamily,
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.White.copy(alpha = 0.1f),
                                    unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                                    disabledContainerColor = Color.White.copy(alpha = 0.1f),
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    cursorColor = Color(0xFF5C0F1A)
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    val text = input.value.trim()
                                    if (text.isNotEmpty()) {
                                        scope.launch {
                                            onSend(text)
                                            input.value = ""
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF5C0F1A),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                enabled = !isLoading
                            ) {
                                Text(
                                    text = if (isLoading) "Miau pensando..." else "Enviar",
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            error?.let {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = it,
                                    color = Color(0xFFFF6B6B),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    val background = if (message.isUser) Color(0xFF2C2C2C) else Color(0xFF5C0F1A)
    val alignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = alignment
    ) {
        Text(
            text = message.text,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(background)
                .padding(12.dp),
            color = Color.White,
            fontSize = 14.sp,
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ConcertCard(
    concert: M1AUConcertCard,
    onNavigateToArtist: (String?, String?) -> Unit,
    onNavigateToConcert: (String?, String?) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Imagen principal
            AsyncImage(
                model = concert.imageUrl,
                contentDescription = concert.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentScale = ContentScale.Crop
            )

            // Overlay con gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.75f)
                            ),
                            startY = 80f
                        )
                    )
            )

            // Contenido sobre la imagen
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header con info del artista
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(
                            Color.Black.copy(alpha = 0.3f),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = concert.artistUsername ?: concert.artistName ?: "Artista",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Footer con info del concierto
                Column {
                    Text(
                        text = concert.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Fecha
                        concert.formattedDate?.let {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = it,
                                    fontSize = 11.sp,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Venue
                        concert.venueName?.let {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = it,
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 🎯 BOTONES INTEGRADOS EN EL FOOTER
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onNavigateToArtist(concert.artistId, concert.artistUsername) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White.copy(alpha = 0.2f),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(vertical = 8.dp),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Artista",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Button(
                            onClick = { onNavigateToConcert(concert.id, concert.title) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF5C0F1A),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.OpenInNew,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Ver más",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}