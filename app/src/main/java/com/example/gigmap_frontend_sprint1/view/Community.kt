
package com.example.gigmap_frontend_sprint1.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.example.gigmap_frontend_sprint1.model.Post
import com.example.gigmap_frontend_sprint1.viewmodel.CommunityViewModel
import com.example.gigmap_frontend_sprint1.viewmodel.ForumViewModel
import com.example.gigmap_frontend_sprint1.viewmodel.PostViewModel
import com.example.gigmap_frontend_sprint1.viewmodel.UserViewModel


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun Community(
    navController: NavHostController,
    communityId: Int,
    communityVm: CommunityViewModel = viewModel(),
    postVm: PostViewModel = viewModel(),
    userVm: UserViewModel = viewModel(),
    forumVm: ForumViewModel = viewModel()
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(communityId) {
        communityVm.getCommunities()
        postVm.getPosts()
        userVm.getUsers()
        forumVm.getForumThreads(communityId)
    }

    LaunchedEffect(selectedTab, communityId) {
        if (selectedTab == 1) {
            forumVm.getForumThreads(communityId)
        }
    }

    val community = communityVm.listaCommunities.find { it.id == communityId }
    val allPosts = postVm.listaPosts
    val allThreads = forumVm.threads
    val users = userVm.listaUsers
    val userById = remember(users) { users.associateBy { it.id } }

    val fanPosts = remember(allPosts, community) {
        if (community == null) emptyList()
        else allPosts.filter { it.communityId == community.id }
    }

    val fanThreads = remember(allThreads, community) {
        if (community == null) emptyList()
        else allThreads.filter { it.communityId == community.id }
    }

    if (community == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Comunidad no encontrada")
        }
        return
    }

    var isJoined by remember { mutableStateOf(false) }
    var showCreateMenu by remember { mutableStateOf(false) }

    LaunchedEffect(community, userVm.currentUserId) {
        val uid = userVm.currentUserId
        isJoined = if (uid == 0) {
            false
        } else {
            val members = community.members ?: emptyList<Any>()
            members.any { member ->
                try {
                    member.toString().toLong() == uid.toLong()
                } catch (_: Exception) {
                    member.toString().toIntOrNull() == uid
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // HEADER (imagen grande)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) {
            GlideImage(
                model = community.image,
                contentDescription = community.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // overlay degradado
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                androidx.compose.ui.graphics.Color.Transparent,
                                androidx.compose.ui.graphics.Color(0x55000000)
                            ),
                            startY = 0f,
                            endY = 220f
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = 40.dp)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${community.members.size} miembros",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = {
                            val userId = userVm.currentUserId
                            if (userId == 0) {
                                Toast.makeText(context, "Debes iniciar sesión", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            if (!isJoined) {
                                isJoined = true
                                communityVm.joinCommunity(communityId.toLong(), userId.toLong()) { success ->
                                    if (!success) {
                                        isJoined = false
                                        Toast.makeText(context, "Error al unirte", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Te uniste", Toast.LENGTH_SHORT).show()
                                        communityVm.getCommunities()
                                    }
                                }
                            } else {
                                isJoined = false
                                communityVm.leaveCommunity(communityId.toLong(), userId.toLong()) { success ->
                                    if (!success) {
                                        isJoined = true
                                        Toast.makeText(context, "Error al dejar la comunidad", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Has dejado la comunidad", Toast.LENGTH_SHORT).show()
                                        communityVm.getCommunities()
                                    }
                                }
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isJoined) Color(0xFF5C0F1A) else Color.White.copy(alpha = 0.95f)
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text(
                            text = if (isJoined) "Dejar de seguir" else "Unirse",
                            color = if (isJoined) Color.White else Color(0xFF5C0F1A),
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    community.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineLarge
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                FloatingActionButton(
                    onClick = { showCreateMenu = true },
                    containerColor = Color(0xFF5C0F1A),
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Crear")
                }
                DropdownMenu(
                    expanded = showCreateMenu,
                    onDismissRequest = { showCreateMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Publicación") },
                        onClick = {
                            showCreateMenu = false
                            navController.navigate("createPost/${communityId}")
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Hilo") },
                        onClick = {
                            showCreateMenu = false
                            navController.navigate("createThread/${communityId}")
                        }
                    )
                }
            }
        }

        // TAB
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier
                        .tabIndicatorOffset(tabPositions[selectedTab])
                        .height(3.dp),
                    color = Color(0xFF5C0F1A)
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Text(
                        "Publicaciones",
                        color = if (selectedTab == 0) Color(0xFF5C0F1A) else Color.Gray,
                        fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Text(
                        "Hilos",
                        color = if (selectedTab == 1) Color(0xFF5C0F1A) else Color.Gray,
                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
        }

        if (selectedTab == 0) {
            if (fanPosts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                    Text(
                        text = "Aún no hay publicaciones.",
                        modifier = Modifier.padding(20.dp),
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(fanPosts, key = { it.id }) { post ->
                        val author = userById[post.userId]
                        PostCard(
                            nav = navController,
                            post = post,
                            authorId = author?.id,
                            authorName = author?.name,
                            authorImage = author?.image,
                            postVm = postVm,
                            currentUserId = userVm.currentUserId,
                            onClick = {}
                        )
                    }
                    item { Spacer(modifier = Modifier.height(20.dp)) }
                }
            }
        } else {
            if (fanThreads.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                    Text(
                        text = "Aún no hay hilos.",
                        modifier = Modifier.padding(20.dp),
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(fanThreads, key = { it.id }) { thread ->
                        val author = userById[thread.userId]
                        ThreadCard(
                            nav = navController,
                            thread = thread,
                            authorName = author?.name
                        )
                    }
                    item { Spacer(modifier = Modifier.height(20.dp)) }
                }
            }
        }

    }
}

@Composable
fun ThreadCard(
    nav: NavHostController,
    thread: Post,
    authorName: String? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { nav.navigate("threadDetail/${thread.id}") },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = thread.title ?: "Sin título",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = thread.content,
                fontSize = 14.sp,
                color = Color.Gray,
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = authorName ?: "Usuario #${thread.userId}",
                    fontSize = 12.sp,
                    color = Color(0xFF5C0F1A)
                )
                Text(
                    text = "${thread.commentCount ?: 0} comentarios",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PostCard(
    nav: NavHostController,
    post: Post,
    authorId: Int? = null,
    authorName: String? = null,
    authorImage: String? = null,
    postVm: PostViewModel,
    currentUserId: Int,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!authorImage.isNullOrBlank()) {
                    GlideImage(
                        model = authorImage,
                        contentDescription = authorName ?: "Avatar",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable { nav.navigate("user/${authorId}") }
                            .border(1.dp, Color(0xFF5C0F1A), CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(text = authorName ?: "Usuario", fontWeight = FontWeight.Bold)
                    Text(
                        text = "Hace poco",
                        color = Color.Gray,
                        fontSize = MaterialTheme.typography.bodySmall.fontSize
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val safeContent = post.content ?: ""
            if (safeContent.isNotBlank()) {
                Text(text = safeContent)
                Spacer(modifier = Modifier.height(8.dp))
            }

            val safeImage = post.image ?: ""
            if (safeImage.isNotBlank()) {
                GlideImage(
                    model = safeImage,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val likesCount = post.likes?.size ?: 0
                Text("$likesCount", color = Color.Gray)

                IconButton(
                    onClick = {
                        if (currentUserId == 0) {
                            println("Usuario no autenticado - no se puede dar like")
                            return@IconButton
                        }
                        postVm.toggleLike(post.id, currentUserId) { success, updatedPost ->
                            if (!success) {
                                println("Error al dar like al post ${post.id}")
                            } else {
                                println("Like toggled. Post actualizado: ${updatedPost?.id}")
                            }
                        }
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    val isLiked = post.likes?.contains(currentUserId) == true
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Dar like",
                        tint = if (isLiked) Color.Red else Color(0xFF2A2A2A)
                    )
                }

                Text("Comentarios", color = Color.Gray)
            }
        }
    }
}