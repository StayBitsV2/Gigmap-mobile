    package com.example.gigmap_frontend_sprint1.view
    // imports
    import androidx.compose.foundation.background
    import androidx.compose.foundation.border
    import androidx.compose.foundation.clickable
    import androidx.compose.foundation.layout.Arrangement
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.mutableStateOf
    import androidx.compose.runtime.remember
    import androidx.compose.foundation.layout.Box
    import androidx.compose.foundation.layout.Column
    import androidx.compose.foundation.layout.PaddingValues
    import androidx.compose.foundation.layout.Row
    import androidx.compose.foundation.layout.Spacer
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.foundation.layout.fillMaxWidth
    import androidx.compose.foundation.layout.height
    import androidx.compose.foundation.layout.padding
    import androidx.compose.foundation.layout.size
    import androidx.compose.foundation.layout.width
    import androidx.compose.foundation.lazy.LazyRow
    import androidx.compose.foundation.lazy.items
    import androidx.compose.foundation.rememberScrollState
    import androidx.compose.foundation.shape.CircleShape
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.foundation.verticalScroll
    import androidx.compose.material.CircularProgressIndicator
    import androidx.compose.material.FloatingActionButton
    import androidx.compose.material.Icon
    import androidx.compose.material.IconButton
    import androidx.compose.material.Text
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.filled.ArrowForward
    import androidx.compose.material.icons.filled.CatchingPokemon
    import androidx.compose.material.icons.filled.Chat
    import androidx.compose.material.icons.filled.Update
    import androidx.compose.material3.Card
    import androidx.compose.material3.CardDefaults
    import androidx.compose.material3.IconButton
    import androidx.compose.runtime.*
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
    import androidx.navigation.NavHostController
    import androidx.test.espresso.base.Default
    import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
    import com.bumptech.glide.integration.compose.GlideImage
    import com.example.gigmap_frontend_sprint1.view.chat.M1AUChatModal
    import com.example.gigmap_frontend_sprint1.viewmodel.ConcertViewModel
    import com.example.gigmap_frontend_sprint1.viewmodel.M1AUChatViewModel
    import com.example.gigmap_frontend_sprint1.viewmodel.PostViewModel
    import com.example.gigmap_frontend_sprint1.viewmodel.UserViewModel
    import compose.icons.FontAwesomeIcons
    import compose.icons.fontawesomeicons.Solid
    import compose.icons.fontawesomeicons.solid.Cat
    import compose.icons.fontawesomeicons.solid.Icons

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    fun HomeContent(
        nav: NavHostController,
        concertVM: ConcertViewModel = viewModel(),
        userVM: UserViewModel = viewModel(),
        postVM: PostViewModel = viewModel(),
        chatViewModel: M1AUChatViewModel = viewModel()
    ) {
        val concerts = concertVM.listaConcerts
        val users    = userVM.listaUsers
        val posts    = postVM.listaPosts
        val artists  = userVM.listaArtists
        LaunchedEffect(Unit) {
            if (concerts.isEmpty()) concertVM.getConcerts()
            if (users.isEmpty())    userVM.getUsers()
            if (artists.isEmpty())  userVM.getArtists()
            if (posts.isEmpty())    postVM.getPosts()
        }

        val userById = remember(users) { users.associateBy { it.id } }

        val isChatOpen = remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 90.dp)
            ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    /// discover new concertss
                    text = "Descubre nuevos conciertos",
                    color = Color(0xFF5C0F1A),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )

                IconButton(onClick = { nav.navigate("concertsList") }) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Ver más conciertos",
                        tint = Color(0xFF5C0F1A)
                    )
                }
            }

            if (concerts.isEmpty()) {
                Box(Modifier.fillMaxWidth().height(180.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF5C0F1A))
                }
            } else {
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp)
                ) {
                    items(concerts) { concert ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            modifier = Modifier.width(280.dp).height(160.dp)
                        ) {
                            Box(Modifier.fillMaxSize()) {
                                GlideImage(
                                    model = concert.image,
                                    contentDescription = concert.name,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Box(
                                    Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.55f))
                                            )
                                        )
                                )
                                Text(
                                    text = concert.name,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    modifier = Modifier.align(Alignment.BottomStart).padding(12.dp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            Text(
                "Nuevos artistas en GigMap",
                color = Color(0xFF5C0F1A),
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 20.dp, top = 16.dp)
            )

            if (artists.isEmpty()) {
                Box(Modifier.fillMaxWidth().height(90.dp), contentAlignment = Alignment.Center) {
                    Text("Sin artistas por ahora", color = Color.Gray)
                }
            } else {
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp)
                ) {
                    items(artists) { artist ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally,) {
                            GlideImage(
                                model = artist.image,
                                contentDescription = artist.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, Color(0xFF5C0F1A), CircleShape)
                                    .clickable { nav.navigate("user/${artist.id}") }
                            )
                            Text(
                                text = artist.username,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(top = 6.dp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

                Text(
                    "Nuevos posts",
                    color = Color(0xFF5C0F1A),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 20.dp, top = 16.dp)
                )

            Spacer(Modifier.height(10.dp))
            if (posts.isEmpty()) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Sin publicaciones todavía", color = Color.Gray)
                }
            } else {
                posts.forEach { post ->
                    val author = userById[post.userId]
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {

                            // --- Header del post (foto y nombre del autor) ---
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                GlideImage(
                                    model = author?.image ?: "",
                                    contentDescription = author?.name,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .clickable { nav.navigate("user/${author?.id}") }
                                        .border(1.dp, Color(0xFF5C0F1A), CircleShape),

                                    contentScale = ContentScale.Crop

                                )
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text(
                                        author?.name ?: "Usuario",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }


                            Spacer(Modifier.height(10.dp))




                            // --- Contenido del post ---
                            Text(
                                post.content,
                                fontSize = 14.sp,
                                color = Color.Black
                            )

                            // --- Mostrar imagen solo si existe ---
                            if (!post.image.isNullOrBlank()) {
                                Spacer(Modifier.height(10.dp))
                                GlideImage(
                                    model = post.image,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )
                            }
                        }
                    }
                }
            }


        }


        
    }
    }
