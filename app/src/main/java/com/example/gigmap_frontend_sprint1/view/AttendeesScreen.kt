package com.example.gigmap_frontend_sprint1.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.gigmap_frontend_sprint1.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun AttendeesScreen(
    concertId: Int,
    navController: NavHostController,
    concertVM: ConcertViewModel = viewModel(),
    userVM: UserViewModel = viewModel()
) {
    var concertState by remember { mutableStateOf<Concerts?>(null) }

    LaunchedEffect(concertId) {
        if (userVM.listaUsers.isEmpty()) {
            userVM.getUsers()
        }
        concertVM.getConcertById(concertId) { concert ->
            concertState = concert
        }
    }

    val attendeeIds = concertState?.attendees ?: emptyList()
    val attendeesList = userVM.listaUsers.filter { it.id in attendeeIds }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Asistentes",
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = BurgundyDark
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = BurgundyDark
                )
            )
        }
    ) { padding ->
        if (attendeesList.isEmpty() && concertState == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = BurgundyDark)
            }
        } else if (attendeesList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
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
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(attendeesList) { user ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate("user/${user.id}")
                            },
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
                }
            }
        }
    }
}
