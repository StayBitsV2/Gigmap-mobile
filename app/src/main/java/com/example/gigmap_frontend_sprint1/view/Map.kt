package com.example.gigmap_frontend_sprint1.view

import android.Manifest
import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import com.example.gigmap_frontend_sprint1.model.Concerts
import com.example.gigmap_frontend_sprint1.model.CreateAnalyticsEventRequest
import com.example.gigmap_frontend_sprint1.model.client.RetrofitClient
import com.example.gigmap_frontend_sprint1.util.LocationUtils
import com.example.gigmap_frontend_sprint1.viewmodel.ConcertViewModel
import com.example.gigmap_frontend_sprint1.viewmodel.UserViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.android.compose.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

private data class ConcertCluster(
    val concerts: List<Concerts>,
    val center: LatLng
)

private fun computeClusters(concerts: List<Concerts>, thresholdKm: Double = 5.0): List<ConcertCluster> {
    val clusters = mutableListOf<ConcertCluster>()
    for (c in concerts) {
        val pos = LatLng(c.venue.latitude, c.venue.longitude)
        var added = false
        for (i in clusters.indices) {
            val cluster = clusters[i]
            val dist = LocationUtils.haversineDistance(
                cluster.center.latitude, cluster.center.longitude,
                pos.latitude, pos.longitude
            )
            if (dist < thresholdKm) {
                val allItems = cluster.concerts + c
                val avgLat = allItems.map { it.venue.latitude }.average()
                val avgLng = allItems.map { it.venue.longitude }.average()
                clusters[i] = ConcertCluster(allItems, LatLng(avgLat, avgLng))
                added = true
                break
            }
        }
        if (!added) {
            clusters.add(ConcertCluster(listOf(c), pos))
        }
    }
    return clusters
}

private suspend fun sendAnalyticsEvent(eventType: String, userId: Long, metadata: String? = null) {
    withContext(Dispatchers.IO) {
        try {
            RetrofitClient.webService.createAnalyticsEvent(
                CreateAnalyticsEventRequest(eventType, userId, metadata)
            )
        } catch (_: Exception) { }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun Map(internalNav: NavHostController, userVM: UserViewModel) {
    val concertVM: ConcertViewModel = viewModel()
    val scope = rememberCoroutineScope()

    var concerts by remember { mutableStateOf<List<Concerts>>(emptyList()) }
    var isLoadingConcerts by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        concertVM.getConcerts()
        repeat(50) {
            val curr = concertVM.listaConcerts.toList()
            if (curr.isNotEmpty()) {
                concerts = curr
                isLoadingConcerts = false
                return@LaunchedEffect
            }
            delay(100)
        }
        concerts = concertVM.listaConcerts.toList()
        isLoadingConcerts = false
    }

    LaunchedEffect(concerts) {
        if (concerts.isNotEmpty()) {
            userVM.currentUserId?.let { uid ->
                sendAnalyticsEvent("MAP_VIEWED", uid.toLong())
            }
        }
    }

    val context = LocalContext.current
    val fused = remember { LocationServices.getFusedLocationProviderClient(context) }
    var hasLocationPermission by remember { mutableStateOf(false) }
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { res ->
        val granted = res[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                res[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        hasLocationPermission = granted
        if (granted) {
            userVM.currentUserId?.let { uid ->
                scope.launch { sendAnalyticsEvent("GEOLOCATION_ENABLED", uid.toLong()) }
            }
        }
    }
    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    val camera = rememberCameraPositionState()
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var selectedClusterIdx by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            val cached = fused.lastLocation.await()
            val loc = cached ?: fused.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                CancellationTokenSource().token
            ).await()
            loc?.let {
                userLocation = LatLng(it.latitude, it.longitude)
                withContext(Dispatchers.Main) {
                    camera.animate(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(it.latitude, it.longitude),
                            13f
                        ),
                        700
                    )
                }
            }
        }
    }

    var selectedGenre by remember { mutableStateOf<String?>(null) }
    val genres = remember(concerts) {
        concerts.map { it.genre }.filter { it.isNotBlank() }.distinct().sorted()
    }

    val filteredConcerts = remember(concerts, userLocation, selectedGenre) {
        var result = concerts
        if (userLocation != null) {
            result = result.filter { c ->
                LocationUtils.haversineDistance(
                    userLocation!!.latitude, userLocation!!.longitude,
                    c.venue.latitude, c.venue.longitude
                ) <= 10.0
            }
        }
        if (selectedGenre != null) {
            result = result.filter { it.genre == selectedGenre }
        }
        result
    }

    val useClustering = filteredConcerts.size > 20
    val clusters = remember(filteredConcerts, useClustering) {
        if (useClustering) computeClusters(filteredConcerts) else emptyList()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item("headline") {
            Text(
                text = "Descubre conciertos cerca de ti",
                color = Color(0xFF5C0F1A),
                fontWeight = FontWeight.SemiBold
            )
        }

        item("genre_chips") {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = selectedGenre == null,
                        onClick = { selectedGenre = null },
                        label = { Text("Todos") }
                    )
                }
                items(genres) { genre ->
                    FilterChip(
                        selected = selectedGenre == genre,
                        onClick = { selectedGenre = genre },
                        label = { Text(genre) }
                    )
                }
            }
        }

        item("map_box") {
            GoogleMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(645.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFEFEFEF)),
                cameraPositionState = camera,
                properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
                uiSettings = MapUiSettings(
                    myLocationButtonEnabled = true,
                    zoomControlsEnabled = true
                ),
                onMapClick = { selectedIndex = null; selectedClusterIdx = null }
            ) {
                if (useClustering) {
                    clusters.forEachIndexed { idx, cluster ->
                        val pos = cluster.center
                        MarkerInfoWindowContent(
                            state = MarkerState(position = pos),
                            onClick = {
                                selectedClusterIdx = idx
                                selectedIndex = null
                                userVM.currentUserId?.let { uid ->
                                    scope.launch { sendAnalyticsEvent("EVENT_MARKER_CLICKED", uid.toLong()) }
                                }
                                false
                            }
                        ) {
                            if (selectedClusterIdx == idx) {
                                ClusterInfoCard(cluster.concerts)
                            }
                        }
                    }
                } else {
                    filteredConcerts.forEachIndexed { idx, c ->
                        val pos = LatLng(c.venue.latitude, c.venue.longitude)
                        MarkerInfoWindowContent(
                            state = MarkerState(position = pos),
                            onClick = {
                                selectedIndex = idx
                                selectedClusterIdx = null
                                userVM.currentUserId?.let { uid ->
                                    scope.launch { sendAnalyticsEvent("EVENT_MARKER_CLICKED", uid.toLong()) }
                                }
                                false
                            }
                        ) {
                            if (selectedIndex == idx) {
                                InfoCard(
                                    concert = c,
                                    userLocation = userLocation,
                                    onClick = {
                                        c.id?.let { id ->
                                            internalNav.navigate("concert/$id")
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        item("list_title") {
            Text(
                text = "Lista de conciertos cerca tuyo",
                color = Color(0xFF5C0F1A),
                fontWeight = FontWeight.SemiBold
            )
        }

        if (isLoadingConcerts && concerts.isEmpty()) {
            item("loading") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(color = Color(0xFF5C0F1A)) }
            }
        } else if (filteredConcerts.isEmpty()) {
            item("empty") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No hay conciertos cercanos",
                        color = Color(0xFF5C0F1A),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = if (selectedGenre != null) {
                            "No encontramos conciertos de $selectedGenre en un radio de 10 km."
                        } else {
                            "No encontramos conciertos en un radio de 10 km desde tu ubicación."
                        },
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            itemsIndexed(
                items = filteredConcerts,
                key = { i, it -> "${it.id}_$i" }
            ) { index, item ->
                SimpleConcertRow(
                    concert = item,
                    isSelected = selectedIndex == index,
                    onClick = {
                        selectedIndex = index
                        scope.launch {
                            withContext(Dispatchers.Main) {
                                camera.animate(
                                    CameraUpdateFactory.newLatLngZoom(
                                        LatLng(item.venue.latitude, item.venue.longitude),
                                        16f
                                    ),
                                    500
                                )
                            }
                        }
                    }
                )
            }
        }

        item("bottom_spacer") { Spacer(Modifier.height(24.dp)) }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun InfoCard(concert: Concerts, userLocation: LatLng?, onClick: () -> Unit) {
    val distance = userLocation?.let {
        LocationUtils.haversineDistance(
            it.latitude, it.longitude,
            concert.venue.latitude, concert.venue.longitude
        )
    }
    val distanceText = distance?.let { String.format("%.1f km", it) } ?: ""

    Box(
        modifier = Modifier
            .shadow(8.dp, RoundedCornerShape(16.dp), clip = false)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .widthIn(min = 280.dp, max = 300.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            GlideImage(
                model = concert.image,
                contentDescription = concert.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                Text(
                    text = concert.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = Color(0xFF111827),
                    maxLines = 1
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${concert.date.take(10).replace("-", "/")}, ${concert.venue.name}",
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280),
                    maxLines = 1
                )
                if (distanceText.isNotBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = distanceText,
                        fontSize = 11.sp,
                        color = Color(0xFF5C0F1A),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun ClusterInfoCard(concerts: List<Concerts>) {
    Box(
        modifier = Modifier
            .shadow(8.dp, RoundedCornerShape(16.dp), clip = false)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF5C0F1A))
            .widthIn(min = 200.dp, max = 260.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "${concerts.size} conciertos en esta zona",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = Color.White
            )
            Spacer(Modifier.height(6.dp))
            concerts.take(3).forEach { c ->
                Text(
                    text = "• ${c.name}",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    maxLines = 1
                )
            }
            if (concerts.size > 3) {
                Text(
                    text = "y ${concerts.size - 3} más...",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun SimpleConcertRow(
    concert: Concerts,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(if (isSelected) 4.dp else 1.dp)
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(concert.name, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(2.dp))
            Text(
                "${concert.date.take(10).replace("-", "/")}, ${concert.venue.name}",
                color = Color.Gray
            )
        }
    }
}