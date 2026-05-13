package com.example.gigmap_frontend_sprint1.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.gigmap_frontend_sprint1.model.Concerts
import com.example.gigmap_frontend_sprint1.viewmodel.ConcertViewModel
import com.example.gigmap_frontend_sprint1.viewmodel.RelatedEventViewModel
import com.example.gigmap_frontend_sprint1.viewmodel.UserViewModel
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

val BurgundyDark = Color(0xFF5C0F1A)

val CreamBackground = Color(0xFFF6F6F6)



@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ConcertDetails(
    navController: NavHostController,
    concertId: Int,
    concertVM: ConcertViewModel = viewModel(),
    userVM: UserViewModel = viewModel(),
    relatedEventVM: RelatedEventViewModel = viewModel()
) {
    val concert = concertVM.listaConcerts.find { it.id == concertId }

    var showCreateRelatedDialog by remember { mutableStateOf(false) }

    if (concert == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = BurgundyDark)
        }
        return
    }

    val currentUserId = userVM.currentUserId
    val relatedConcerts = emptyList<Concerts>()
    val users = userVM.listaUsers
    val confirmedAttendees = concertVM.getConfirmedUsers(concert, users)


    val relatedEvents = relatedEventVM.listaRelatedEvents


    LaunchedEffect(concert.id) {
        concert.id?.let { id ->
            relatedEventVM.loadRelatedEventsByConcertId(id.toLong())
        }
    }

    var isConfirmed by remember { mutableStateOf(false) }


    LaunchedEffect(concert.attendees, currentUserId) {
        isConfirmed = currentUserId?.let { id -> concert.attendees.contains(id) } ?: false
    }


    LaunchedEffect(Unit) {
        if (userVM.listaUsers.isEmpty()) {
            userVM.getUsers()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
            .verticalScroll(rememberScrollState())
    ) {
        // HEADER IMAGEN DEL CONCIERTO
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                GlideImage(
                    model = concert.image,
                    contentDescription = concert.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.6f)
                                )
                            )
                        )
                )
            }
        }

        // CARD INFO PRINCIPAL
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = concert.name,
                    color = BurgundyDark,
                    fontWeight = FontWeight.Bold,
                    fontFamily = InterFontFamily,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = BurgundyDark,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = concert.venue.name,
                        fontSize = 13.sp,
                        color = Color(0xFF736D6D),
                        fontWeight = FontWeight.Medium,
                        fontFamily = InterFontFamily,
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = BurgundyDark,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = concert.date.take(10).replace("-", " de "),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = InterFontFamily,
                        color = Color(0xFF736D6D)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = concert.description,
                    fontSize = 13.sp,
                    color = Color(0xFF736D6D),
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = InterFontFamily,
                )
            }
        }

        //  BOTONES CONFIRMAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    if (currentUserId == null) return@Button // no logueado

                    val concertIdLong = concertId.toLong()
                    val userIdLong = currentUserId.toLong()

                    if (!isConfirmed) {
                        isConfirmed = true
                        concertVM.addAttendeeToConcert(concertIdLong, userIdLong) { success ->
                            if (!success) isConfirmed = false
                        }
                    } else {
                        isConfirmed = false
                        concertVM.removeAttendeeFromConcert(concertIdLong, userIdLong) { success ->
                            if (!success) isConfirmed = true
                        }
                    }
                },
                enabled = currentUserId != null,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!isConfirmed) BurgundyDark else Color(0xFF2C2C2C)
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                Text(
                    text = if (!isConfirmed) "Confirmar asistencia" else "Cancelar confirmación",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = InterFontFamily
                )
            }


        }

        Spacer(modifier = Modifier.height(20.dp))

        // EVENTOS RELACIONADOS
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = BurgundyDark),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                Text(
                    text = "Eventos relacionados",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = InterFontFamily,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                //  LISTA DE EVENTOS
                if (relatedEvents.isEmpty()) {

                    Text(
                        "No hay eventos relacionados todavía",
                        color = Color.White,
                        fontFamily = InterFontFamily,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                } else {

                    relatedEvents.forEach { relatedEvent ->
                        val isAttending = remember(relatedEvent.participantes, currentUserId) {
                            currentUserId != null && relatedEvent.participantes.contains(currentUserId)
                        }
                        var attending by remember(isAttending) { mutableStateOf(isAttending) }
                        var loading by remember { mutableStateOf(false) }


                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            GlideImage(
                                model = concert.image,
                                contentDescription = relatedEvent.titulo,
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(10.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = relatedEvent.titulo,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = InterFontFamily,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = relatedEvent.venue.address, fontSize = 12.sp, fontFamily = InterFontFamily, color = Color.White)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = formatRelatedEventDate(relatedEvent.datehour), fontSize = 12.sp, fontFamily = InterFontFamily, color = Color.White)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                /* 
                                OutlinedButton(
                                    onClick = {
                                        if (loading) return@OutlinedButton
                                        loading = true
                                        val id = relatedEvent.id ?: return@OutlinedButton
                                        if (attending) {
                                            relatedEventVM.leaveRelatedEvent(id, currentUserId!!) { success ->
                                                if (success) attending = false
                                                loading = false
                                            }
                                        } else {
                                            relatedEventVM.joinRelatedEvent(id, currentUserId!!) { success ->
                                                if (success) attending = true
                                                loading = false
                                            }
                                        }
                                    },
                                    enabled = currentUserId != null && !loading,
                                    border = BorderStroke(1.dp, Color.White),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (attending) Color.White.copy(alpha = 0.15f) else Color.Transparent,
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(50.dp),
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    if (loading) {
                                        CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(14.dp))
                                    } else {
                                        Text(
                                            text = if (attending) "Asistiré" else "Me interesa",
                                            fontSize = 12.sp,
                                            fontFamily = InterFontFamily,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                                */
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = { showCreateRelatedDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = BorderStroke(1.5.dp, Color.White),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text(
                        text = "Crear evento relacionado",
                        modifier = Modifier.padding(vertical = 8.dp),
                        fontSize = 16.sp,
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }


        if (showCreateRelatedDialog) {
            CreateRelatedEventDialog(
                concertId = concert.id ?: concertId,
                currentUserId = currentUserId,
                relatedEventVM = relatedEventVM,
                onDismiss = { showCreateRelatedDialog = false }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Usuarios que confirmaron su asistencia",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = InterFontFamily,
                color = BurgundyDark
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (confirmedAttendees.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay usuarios confirmados aún",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium,
                            fontFamily = InterFontFamily,
                        )
                    }
                }
            } else {
                confirmedAttendees.forEach { user ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            GlideImage(
                                model = user.image ?: "",
                                contentDescription = user.username ?: "Usuario sin username",
                                modifier = Modifier
                                    .size(45.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = user.name ?: "Sin nombre",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = InterFontFamily,
                                    color = BurgundyDark
                                )
                                Text(
                                    text = "@${user.username ?: "sin_usuario"}",
                                    fontSize = 12.sp,
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.Normal,
                                    color = Color(0xFF736D6D)
                                )
                            }

                           
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                if (confirmedAttendees.size > 3) {
                    TextButton(
                        onClick = { },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = "Ver más",
                            color = BurgundyDark,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = InterFontFamily,
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}


@RequiresApi(Build.VERSION_CODES.O)
fun formatRelatedEventDate(dateTimeIso: String): String {
    return try {
        val parsed = java.time.OffsetDateTime.parse(dateTimeIso)
        val formatter = java.time.format.DateTimeFormatter.ofPattern("dd 'de' MMMM yyyy", java.util.Locale("es", "ES"))
        parsed.toLocalDate().format(formatter)
    } catch (e: Exception) {
        dateTimeIso.take(10)
    }
}
