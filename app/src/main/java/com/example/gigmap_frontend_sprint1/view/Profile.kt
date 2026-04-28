package com.example.gigmap_frontend_sprint1.view

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import coil.compose.AsyncImage
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.gigmap_frontend_sprint1.model.Concerts
import com.example.gigmap_frontend_sprint1.model.Post
import com.example.gigmap_frontend_sprint1.viewmodel.CommunityViewModel
import com.example.gigmap_frontend_sprint1.viewmodel.ConcertViewModel
import com.example.gigmap_frontend_sprint1.viewmodel.PostViewModel
import com.example.gigmap_frontend_sprint1.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun Profile(
    rootNav: NavHostController,
    innerNav: NavHostController,
    viewedUserId: Int? = null,
    userVM: UserViewModel = viewModel(),
    concertVM: ConcertViewModel = viewModel(),
    postVM: PostViewModel = viewModel(),
    communityVM: CommunityViewModel = viewModel(),
    context: Context,
    onOpenConcertFromProfile: (Int) -> Unit = {},
    onOpenCommunityFromProfile: (Int) -> Unit = {},
    onSelectTab: (Int) -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val loggedUserId = userVM.currentUserId
    val profileUserId = viewedUserId ?: loggedUserId
    val isOwner = profileUserId == loggedUserId

    val currentUser = remember(userVM.listaUsers, profileUserId) {
        userVM.listaUsers.find { it.id == profileUserId }
    }

    val isArtist = remember(currentUser) { (currentUser?.role ?: "").uppercase() == "ARTIST" }

    val tabOptions = remember(isArtist) {
        if (isArtist) listOf("Conciertos", "Comunidades", "Likes")
        else listOf("GigList", "Comunidades", "Likes")
    }

    var selectedTab by remember { mutableStateOf(0) }

    var concertsByUser by remember { mutableStateOf<List<Concerts>>(emptyList()) }
    var likedPosts by remember { mutableStateOf<List<Post>>(emptyList()) }

    val users = userVM.listaUsers
    val userById = remember(users) { users.associateBy { it.id } }

    var isFollowing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (concertVM.listaConcerts.isEmpty()) {
            concertVM.getConcerts()
        }
        if (communityVM.listaCommunities.isEmpty()) {
            communityVM.getCommunities()
        }
    }

    LaunchedEffect(profileUserId) {
        if (profileUserId != 0) {
            if (isArtist) {
                concertVM.getConcertsByArtist(profileUserId.toLong()) { result ->
                    result?.let { concertsByUser = it }
                }
            } else {
                concertsByUser = emptyList()
            }

            postVM.getPostsLikedByUser(profileUserId.toLong()) { result ->
                result?.let { likedPosts = it }
            }
        } else {
            concertsByUser = emptyList()
            likedPosts = emptyList()
        }
    }

    val gigList = remember(concertVM.listaConcerts, profileUserId, isArtist) {
        if (!isArtist && profileUserId != 0) {
            concertVM.listaConcerts.filter { concert ->
                try {
                    concert.attendees?.contains(profileUserId) == true
                } catch (e: Exception) {
                    false
                }
            }
        } else emptyList()
    }

    val joinedCommunities = remember(communityVM.listaCommunities, profileUserId) {
        if (profileUserId != 0) {
            communityVM.listaCommunities.filter { community ->
                val members = community.members ?: emptyList<Any>()
                members.any { m ->
                    m.toString().toIntOrNull() == profileUserId
                }
            }
        } else emptyList()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // HEADER
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val imageUrl = currentUser?.image ?: ""
                    AsyncImage(
                        model = if (imageUrl.isNotBlank()) imageUrl else null,
                        contentDescription = "Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEDEDED), CircleShape)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = currentUser?.name ?: "Nombre de usuario",
                        fontSize = 20.sp,
                        color = Color.Black,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = currentUser?.username?.let { "@$it" } ?: "@username",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (isOwner) {
                        Button(
                            onClick = { innerNav.navigate("editProfile") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C0F1A)),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(text = "Editar perfil", color = Color.White)
                        }
                    } else {
                        Button(
                            onClick = {
                                isFollowing = !isFollowing
                                coroutineScope.launch {
                                    if (isFollowing) snackbarHostState.showSnackbar("✅ Sigues a ${currentUser?.name ?: "usuario"}")
                                    else snackbarHostState.showSnackbar("Has dejado de seguir a ${currentUser?.name ?: "usuario"}")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isFollowing) Color.Gray else Color(0xFF5C0F1A)
                            ),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = if (isFollowing) "Siguiendo" else "Seguir",
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // TABS
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier
                            .tabIndicatorOffset(tabPositions[selectedTab])
                            .height(3.dp),
                        color = Color.DarkGray
                    )
                }
            ) {
                tabOptions.forEachIndexed { index, title ->
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

            // CONTENIDO
            when (selectedTab) {
                // 0: Conciertos (ARTIST) / GigList (FAN)
                0 -> {
                    if (isArtist) {
                        if (concertsByUser.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No hay conciertos publicados", color = Color.Gray)
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                items(concertsByUser) { concert ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                innerNav.navigate("concert/${concert.id}")
                                            },
                                        shape = RoundedCornerShape(16.dp),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.White)
                                    ) {
                                        Column {
                                            GlideImage(
                                                model = concert.image,
                                                contentDescription = concert.name,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(160.dp),
                                                contentScale = ContentScale.Crop
                                            )
                                            Column(Modifier.padding(12.dp)) {
                                                Text(
                                                    text = concert.name ?: "Concierto",
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF5C0F1A),
                                                    fontSize = 16.sp
                                                )
                                                Spacer(Modifier.height(4.dp))
                                                Text(
                                                    text = concert.venue?.name ?: "",
                                                    color = Color(0xFF736D6D),
                                                    fontSize = 14.sp
                                                )
                                                Spacer(Modifier.height(2.dp))
                                                Text(
                                                    text = (concert.date ?: "")
                                                        .take(10)
                                                        .replace("-", "/"),
                                                    color = Color.Gray,
                                                    fontSize = 13.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // FAN: GigList = conciertos confirmados
                        if (gigList.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Aún no has confirmado asistencia a ningún concierto",
                                    color = Color.Gray
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                items(gigList) { concert ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                //  No navegamos directo, avisamos al Home
                                                concert.id?.let { id ->
                                                    onOpenConcertFromProfile(id)
                                                }
                                            },
                                        shape = RoundedCornerShape(16.dp),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.White)
                                    ) {
                                        Column {
                                            GlideImage(
                                                model = concert.image,
                                                contentDescription = concert.name,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(160.dp),
                                                contentScale = ContentScale.Crop
                                            )
                                            Column(Modifier.padding(12.dp)) {
                                                Text(
                                                    text = concert.name ?: "Concierto",
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF5C0F1A),
                                                    fontSize = 16.sp
                                                )
                                                Spacer(Modifier.height(4.dp))
                                                Text(
                                                    text = concert.venue?.name ?: "",
                                                    color = Color(0xFF736D6D),
                                                    fontSize = 14.sp
                                                )
                                                Spacer(Modifier.height(2.dp))
                                                Text(
                                                    text = (concert.date ?: "")
                                                        .take(10)
                                                        .replace("-", "/"),
                                                    color = Color.Gray,
                                                    fontSize = 13.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 1: Comunidades
                1 -> {
                    if (joinedCommunities.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Todavía no te has unido a ninguna comunidad",
                                color = Color.Gray
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(joinedCommunities) { community ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {

                                                onOpenCommunityFromProfile(community.id)


                                        },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        GlideImage(
                                            model = community.image,
                                            contentDescription = community.name,
                                            modifier = Modifier
                                                .size(56.dp)
                                                .clip(RoundedCornerShape(10.dp)),
                                            contentScale = ContentScale.Crop
                                        )

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Column(
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = community.name,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF111111)
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = community.description,
                                                fontSize = 12.sp,
                                                color = Color(0xFF666666),
                                                maxLines = 2
                                            )
                                        }

                                        Icon(
                                            imageVector = Icons.Default.ArrowForward,
                                            contentDescription = "Ver comunidad",
                                            tint = Color(0xFF5C0F1A)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 2: Likes
                2 -> {
                    if (likedPosts.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No hay publicaciones en Likes", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(likedPosts) { post ->
                                val author = userById[post.userId]
                                PostCard(
                                    nav = innerNav,
                                    post = post,
                                    authorId = author?.id,
                                    authorName = author?.name,
                                    authorImage = author?.image,
                                    postVm = postVM,
                                    currentUserId = loggedUserId,
                                    onClick = { }
                                )
                            }
                        }
                    }
                }
            }
        }

        if (isOwner) {
            IconButton(
                onClick = {
                    val pref = context.getSharedPreferences("pref1", Context.MODE_PRIVATE)
                    pref.edit().clear().apply()
                    userVM.currentUserId = 0
                    rootNav.navigate("login") { popUpTo(0) { inclusive = true } }
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cerrar sesión",
                    tint = Color(0xFF5C0F1A)
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
