package com.example.gigmap_frontend_sprint1.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gigmap_frontend_sprint1.components.BottomBar
import com.example.gigmap_frontend_sprint1.components.TopBar
import com.example.gigmap_frontend_sprint1.viewmodel.CommunityViewModel
import com.example.gigmap_frontend_sprint1.viewmodel.ConcertViewModel
import com.example.gigmap_frontend_sprint1.viewmodel.NotificationViewModel
import com.example.gigmap_frontend_sprint1.viewmodel.PostViewModel
import com.example.gigmap_frontend_sprint1.viewmodel.UserViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Home(nav: NavHostController) {
    var selectedItem by remember { mutableIntStateOf(0) }
    val internalNav = rememberNavController()

    val concertVM: ConcertViewModel = viewModel()
    val userVM: UserViewModel = viewModel()
    val postVM: PostViewModel = viewModel()
    val communityVm: CommunityViewModel = viewModel()
    val notificationVm: NotificationViewModel = viewModel()
    val context = LocalContext.current
    val navBackStackEntry by internalNav.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Concierto giglist
    var pendingConcertFromProfile by remember { mutableStateOf<Int?>(null) }

    // Comunidad list
    var pendingCommunityFromProfile by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        userVM.loadAuthFromPrefs(context)
    }

    Scaffold(
        topBar = {
            TopBar(
                onBackClick = {
                    if (!internalNav.popBackStack()) {
                        nav.popBackStack()
                    }
                },
                onNotificationClick = {
                    internalNav.navigate("notifications")
                },
                showNotificationIcon = currentRoute != "notifications"
            )
        },
        bottomBar = {
            BottomBar(
                selectedItem = selectedItem,
                onItemSelected = { selectedItem = it }
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedItem) {

                // ---------- TAB 0: FEED / CONCIERTOS ----------
                0 -> {
                    // Si venimos desde el perfil con un concierto pendiente, abrimos detalle aquí
                    LaunchedEffect(pendingConcertFromProfile) {
                        val id = pendingConcertFromProfile
                        if (id != null) {
                            internalNav.navigate("concert/$id")
                            pendingConcertFromProfile = null
                        }
                    }

                    NavHost(
                        navController = internalNav,
                        startDestination = "homecontent"
                    ) {
                        composable("homecontent") {
                            HomeContent(
                                internalNav,
                                concertVM = concertVM,
                                userVM = userVM,
                                postVM = postVM
                            )
                        }

                        composable(
                            route = "user/{userId}",
                            arguments = listOf(navArgument("userId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val userId = backStackEntry.arguments?.getInt("userId") ?: 0

                            Profile(
                                rootNav = nav,
                                innerNav = internalNav,
                                userVM = userVM,
                                concertVM = concertVM,
                                postVM = postVM,
                                communityVM = communityVm,
                                viewedUserId = userId,
                                context = LocalContext.current,
                                onOpenConcertFromProfile = { id ->
                                    // desde perfil → queremos abrir concierto en tab 0
                                    pendingConcertFromProfile = id
                                    selectedItem = 0
                                },
                                onOpenCommunityFromProfile = { id ->
                                    // desde perfil → queremos abrir comunidad en tab 2
                                    pendingCommunityFromProfile = id
                                    selectedItem = 2
                                },
                                onSelectTab = { index ->
                                    selectedItem = index
                                }
                            )
                        }

                        composable("concertsList") {
                            ConcertsList(
                                internalNav,
                                concertVM = concertVM,
                                userVm = userVM
                            )
                        }

                        composable("createConcert") {
                            CreateConcert(
                                internalNav,
                                concertVM = concertVM,
                                userVM = userVM
                            )
                        }

                        composable(
                            route = "concert/{concertId}",
                            arguments = listOf(navArgument("concertId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val concertId = backStackEntry.arguments?.getInt("concertId") ?: 0
                            ConcertDetails(
                                navController = internalNav,
                                concertId = concertId,
                                concertVM = concertVM,
                                userVM = userVM
                            )
                        }

                        composable(
                            route = "community/{communityId}",
                            arguments = listOf(navArgument("communityId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val communityId = backStackEntry.arguments?.getInt("communityId") ?: 0
                            Community(
                                navController = internalNav,
                                communityId = communityId,
                                userVm = userVM,
                                postVm = postVM,
                                communityVm = communityVm
                            )
                        }

                        composable("notifications") {
                            NotificationsList(notificationVm)
                        }
                    }
                }

                // ---------- TAB 1: MAPA ----------
                1 -> Map(nav)

                // ---------- TAB 2: COMMUNITIES ----------
                2 -> {
                    // Si venimos desde el perfil con una comunidad pendiente, abrimos detalle aquí
                    LaunchedEffect(pendingCommunityFromProfile) {
                        val id = pendingCommunityFromProfile
                        if (id != null) {
                            internalNav.navigate("community/$id")
                            pendingCommunityFromProfile = null
                        }
                    }

                    NavHost(
                        navController = internalNav,
                        startDestination = "communitiesList"
                    ) {
                        composable("communitiesList") {
                            CommunitiesList(nav = internalNav)
                        }

                        composable(
                            route = "user/{userId}",
                            arguments = listOf(navArgument("userId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val userId = backStackEntry.arguments?.getInt("userId") ?: 0

                            Profile(
                                rootNav = nav,
                                innerNav = internalNav,
                                userVM = userVM,
                                concertVM = concertVM,
                                postVM = postVM,
                                communityVM = communityVm,
                                viewedUserId = userId,
                                context = LocalContext.current,
                                onOpenConcertFromProfile = { id ->
                                    pendingConcertFromProfile = id
                                    selectedItem = 0
                                },
                                onOpenCommunityFromProfile = { id ->
                                    pendingCommunityFromProfile = id
                                    selectedItem = 2
                                },
                                onSelectTab = { index ->
                                    selectedItem = index
                                }
                            )
                        }

                        composable(
                            route = "community/{communityId}",
                            arguments = listOf(navArgument("communityId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val communityId = backStackEntry.arguments?.getInt("communityId") ?: 0
                            Community(
                                navController = internalNav,
                                communityId = communityId,
                                userVm = userVM,
                                postVm = postVM,
                                communityVm = communityVm
                            )
                        }

                        composable("createCommunity") {
                            CreateCommunity(
                                nav = internalNav,
                                viewModel = communityVm
                            )
                        }

                        composable(
                            route = "createPost/{communityId}",
                            arguments = listOf(navArgument("communityId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val communityId = backStackEntry.arguments?.getInt("communityId") ?: 0
                            CreatePost(
                                navController = internalNav,
                                communityId = communityId,
                                postVM = postVM,
                                userVM = userVM
                            )
                        }
                    }
                }

                // ---------- TAB 3: PROFILE ----------
                3 -> {
                    NavHost(
                        navController = internalNav,
                        startDestination = "profile"
                    ) {
                        composable("profile") {
                            Profile(
                                rootNav = nav,
                                innerNav = internalNav,
                                userVM = userVM,
                                concertVM = concertVM,
                                postVM = postVM,
                                communityVM = communityVm,
                                context = LocalContext.current,
                                onOpenConcertFromProfile = { id ->
                                    pendingConcertFromProfile = id
                                    selectedItem = 0
                                },
                                onOpenCommunityFromProfile = { id ->
                                    pendingCommunityFromProfile = id
                                    selectedItem = 2
                                },
                                onSelectTab = { index ->
                                    selectedItem = index
                                }
                            )
                        }

                        composable(
                            route = "concert/{concertId}",
                            arguments = listOf(navArgument("concertId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val concertId = backStackEntry.arguments?.getInt("concertId") ?: 0
                            ConcertDetails(
                                navController = internalNav,
                                concertId = concertId,
                                concertVM = concertVM,
                                userVM = userVM
                            )
                        }

                        composable(
                            route = "community/{communityId}",
                            arguments = listOf(navArgument("communityId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val communityId = backStackEntry.arguments?.getInt("communityId") ?: 0
                            Community(
                                navController = internalNav,
                                communityId = communityId,
                                userVm = userVM,
                                postVm = postVM,
                                communityVm = communityVm
                            )
                        }

                        composable("editProfile") {
                            EditProfile(
                                nav = internalNav,
                                userVM = userVM
                            )
                        }
                    }
                }
            }
        }
    }
}
