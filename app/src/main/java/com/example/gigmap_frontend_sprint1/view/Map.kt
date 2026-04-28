package com.example.gigmap_frontend_sprint1.view

import android.Manifest
import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.gigmap_frontend_sprint1.viewmodel.ConcertViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.android.compose.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun Map(nav: NavHostController) {
    val concertVM: ConcertViewModel = viewModel()

    var concerts by remember { mutableStateOf<List<Concerts>>(emptyList()) }
    LaunchedEffect(Unit) {
        concertVM.getConcerts()
        repeat(50) {
            val curr = concertVM.listaConcerts.toList()
            if (curr.isNotEmpty()) { concerts = curr; return@LaunchedEffect }
            delay(100)
        }
        concerts = concertVM.listaConcerts.toList()
    }

    // permisos/ubicación
    val context = LocalContext.current
    val fused = remember { LocationServices.getFusedLocationProviderClient(context) }
    var hasLocationPermission by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { res ->
        hasLocationPermission =
            res[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    res[Manifest.permission.ACCESS_COARSE_LOCATION] == true
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
    val scope = rememberCoroutineScope()

    @SuppressLint("MissingPermission")
    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            val cached = fused.lastLocation.await()
            val loc = cached ?: fused.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                CancellationTokenSource().token
            ).await()
            loc?.let {
                camera.animate(
                    CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 13f),
                    700
                )
            }
        }
    }

    // ===================== UI =====================
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // título arriba (opcional)
        item("headline") {
            Text(
                text = "Descubre conciertos cerca de ti",
                color = Color(0xFF5C0F1A),
                fontWeight = FontWeight.SemiBold
            )
        }

        // MAPA con tamaño fijo (≈ como en tu captura)
        item("map_box") {
            GoogleMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(645.dp)                // ← tamaño fijo del mapa
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFEFEFEF)),
                cameraPositionState = camera,
                properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
                uiSettings = MapUiSettings(
                    myLocationButtonEnabled = true,
                    zoomControlsEnabled = true
                ),
                onMapClick = { selectedIndex = null }
            ) {
                concerts.forEachIndexed { idx, c ->
                    val pos = LatLng(c.venue.latitude, c.venue.longitude)

                    // Marker con InfoWindow custom (tarjeta compacta)
                    MarkerInfoWindowContent(
                        state = MarkerState(position = pos),
                        onClick = { selectedIndex = idx; false }
                    ) {
                        if (selectedIndex == idx) {
                            InfoCard(concert = c)
                        }
                    }
                }
            }
        }

        // título de sección lista
        item("list_title") {
            Text(
                text = "Lista de conciertos  cerca tuyo",
                color = Color(0xFF5C0F1A),
                fontWeight = FontWeight.SemiBold
            )
        }

        // lista simple (scrollable)
        if (concerts.isEmpty()) {
            item("loading") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(color = Color(0xFF5C0F1A)) }
            }
        } else {
            itemsIndexed(
                items = concerts,
                key = { i, it -> "${it.id}_$i" }
            ) { index, item ->
                SimpleConcertRow(
                    concert = item,
                    isSelected = selectedIndex == index,
                    onClick = {
                        selectedIndex = index
                        scope.launch {
                            camera.animate(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(item.venue.latitude, item.venue.longitude), 16f
                                ),
                                500
                            )
                        }
                    }
                )
            }
        }

        // espacio para que no tape la bottom bar
        item("bottom_spacer") { Spacer(Modifier.height(24.dp)) }
    }
}
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun InfoCard(concert: Concerts) {
    // Contenedor con sombra suave y bordes redondeados - diseño horizontal compacto
    Box(
        modifier = Modifier
            .shadow(8.dp, RoundedCornerShape(16.dp), clip = false)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .widthIn(min = 280.dp, max = 300.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Imagen más compacta
            GlideImage(
                model = concert.image,
                contentDescription = concert.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                contentScale = ContentScale.Crop
            )

            // Contenido de texto compacto
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
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
