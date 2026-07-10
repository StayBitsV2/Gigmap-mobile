package com.example.gigmap_frontend_sprint1.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gigmap_frontend_sprint1.model.ArtistStats
import com.example.gigmap_frontend_sprint1.model.client.RetrofitClient

@Composable
fun ArtistStatsView(userId: Int) {
    var artistStats by remember { mutableStateOf<ArtistStats?>(null) }

    LaunchedEffect(userId) {
        if (userId != 0) {
            try {
                val response = RetrofitClient.webService.getArtistStats(userId.toLong())
                if (response.isSuccessful) {
                    artistStats = response.body()
                }
            } catch (_: Exception) { }
        }
    }

    if (artistStats == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF5C0F1A))
        }
    } else {
        val stats = artistStats!!
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Mis Estadísticas",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5C0F1A)
            )
            Spacer(Modifier.height(16.dp))
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(stats.weeks) { week ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                "${week.weekStart} - ${week.weekEnd}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF5C0F1A)
                            )
                            Spacer(Modifier.height(8.dp))
                            StatRow("Nuevos seguidores", week.newFollowers)
                            StatRow("Visitas al perfil", week.profileViews)
                            Spacer(Modifier.height(4.dp))
                            Text("Clics en enlaces:", fontSize = 13.sp, color = Color.Gray)
                            StatRow("  Spotify", week.externalLinkClicks.spotify)
                            StatRow("  Instagram", week.externalLinkClicks.instagram)
                            StatRow("  YouTube", week.externalLinkClicks.youtube)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: Long) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 13.sp, color = Color.DarkGray)
        Text(
            value.toString(),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF5C0F1A)
        )
    }
}
