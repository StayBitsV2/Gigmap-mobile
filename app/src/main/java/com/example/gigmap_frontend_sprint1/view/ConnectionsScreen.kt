package com.example.gigmap_frontend_sprint1.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import coil.compose.AsyncImage
import com.example.gigmap_frontend_sprint1.model.ConnectionRequestResource
import com.example.gigmap_frontend_sprint1.model.ConnectionResource
import com.example.gigmap_frontend_sprint1.viewmodel.ConnectionViewModel
import com.example.gigmap_frontend_sprint1.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectionsScreen(
    userId: Long,
    connectionVM: ConnectionViewModel,
    userVM: UserViewModel,
    innerNav: androidx.navigation.NavHostController
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    var connections by remember { mutableStateOf<List<ConnectionResource>>(emptyList()) }
    var incomingRequests by remember { mutableStateOf<List<ConnectionRequestResource>>(emptyList()) }

    LaunchedEffect(userId) {
        connectionVM.getConnections(userId) { connections = it }
        connectionVM.getIncomingRequests(userId) { incomingRequests = it }
    }

    val users = userVM.listaUsers
    val userById = remember(users) { users.associateBy { it.id } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Conexiones",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF5C0F1A)
        )

        Spacer(modifier = Modifier.height(16.dp))

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier
                        .tabIndicatorOffset(tabPositions[selectedTab])
                        .height(3.dp),
                    color = Color.DarkGray
                )
            }
        ) {
            listOf("Conexiones", "Solicitudes").forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            title,
                            color = if (selectedTab == index) Color(0xFF5C0F1A) else Color.Gray
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (selectedTab) {
            0 -> {
                if (connections.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No tienes conexiones todavía", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(connections) { connection ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        innerNav.navigate("user/${connection.connectedUserId}")
                                    },
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = connection.connectedUserImage?.ifBlank { null },
                                        contentDescription = connection.connectedUsername,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFEDEDED), CircleShape)
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = connection.connectedUsername ?: "Usuario",
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF5C0F1A),
                                            fontSize = 15.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            1 -> {
                if (incomingRequests.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No tienes solicitudes pendientes", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(incomingRequests) { request ->
                            val requester = userById[request.requesterId.toInt()]
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        AsyncImage(
                                            model = requester?.image?.ifBlank { null },
                                            contentDescription = requester?.name,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFEDEDED), CircleShape)
                                        )
                                        Spacer(Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = requester?.name ?: "Usuario",
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF5C0F1A),
                                                fontSize = 15.sp
                                            )
                                            Text(
                                                text = requester?.username?.let { "@$it" } ?: "",
                                                color = Color.Gray,
                                                fontSize = 13.sp
                                            )
                                        }
                                    }
                                    Spacer(Modifier.height(8.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Button(
                                            onClick = {
                                                connectionVM.acceptRequest(request.id) { success ->
                                                    if (success) {
                                                        incomingRequests = incomingRequests.filter { it.id != request.id }
                                                        connectionVM.getConnections(userId) { connections = it }
                                                    }
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C0F1A)),
                                            shape = MaterialTheme.shapes.small,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Aceptar", color = Color.White)
                                        }
                                        OutlinedButton(
                                            onClick = {
                                                connectionVM.rejectRequest(request.id) { success ->
                                                    if (success) {
                                                        incomingRequests = incomingRequests.filter { it.id != request.id }
                                                    }
                                                }
                                            },
                                            shape = MaterialTheme.shapes.small,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Rechazar", color = Color(0xFF5C0F1A))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
