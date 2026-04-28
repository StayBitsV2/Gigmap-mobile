package com.example.gigmap_frontend_sprint1.view.nav

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.input.key.Key.Companion.Home
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gigmap_frontend_sprint1.view.Community
import com.example.gigmap_frontend_sprint1.view.ConcertDetails

import com.example.gigmap_frontend_sprint1.view.ConcertsList
import com.example.gigmap_frontend_sprint1.view.CreateConcert
import com.example.gigmap_frontend_sprint1.view.CreatePost
import com.example.gigmap_frontend_sprint1.view.Login
import com.example.gigmap_frontend_sprint1.view.SignIn
import com.example.gigmap_frontend_sprint1.view.Welcome
import com.example.gigmap_frontend_sprint1.view.Home
import com.example.gigmap_frontend_sprint1.view.UserView
import com.example.gigmap_frontend_sprint1.viewmodel.CommunityViewModel
import com.example.gigmap_frontend_sprint1.viewmodel.ConcertViewModel
import com.example.gigmap_frontend_sprint1.viewmodel.PostViewModel
import com.example.gigmap_frontend_sprint1.viewmodel.RelatedEventViewModel
import com.example.gigmap_frontend_sprint1.viewmodel.UserViewModel
@Composable
fun Navi(
    userViewModel: UserViewModel,
    concertViewModel: ConcertViewModel,
    postViewModel: PostViewModel,
    communityViewModel: CommunityViewModel,
    context: Context,
    relatedEventViewModel: RelatedEventViewModel,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "welcome"
    ) {

        composable("welcome") {
            Welcome(navController)
        }


        composable("login") {
            Login(userViewModel, navController, context)
        }

        composable("signin") {
            SignIn(navController, userViewModel)
        }


        composable("homecontent") {
            Home(
                nav = navController
            )
        }

        composable("concertsList") {
            ConcertsList(navController, concertViewModel)
        }

        composable("createConcert") {
            CreateConcert(navController = navController, concertVM = concertViewModel, userVM = userViewModel )
        }


        composable(
            route = "concert/{concertId}",
            arguments = listOf(navArgument("concertId") { type = NavType.IntType })
        ) { backStackEntry ->
            val concertId = backStackEntry.arguments?.getInt("concertId") ?: 0
            ConcertDetails(
                navController = navController,
                concertId = concertId,
                concertVM = concertViewModel,
                userVM = userViewModel,
                relatedEventVM = relatedEventViewModel
            )
        }

        // user

        composable(
            route = "user/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            UserView(userId = userId, userViewModel = userViewModel)
        }


        /*
        // importa NavType si no lo tienes: ya lo usas arriba

        composable(
            route = "community/{communityId}",
            arguments = listOf(navArgument("communityId") { type = NavType.IntType })
        ) { backStackEntry ->
            val communityId = backStackEntry.arguments?.getInt("communityId") ?: 0
            // Llamamos a CommunityScreen. Esta función puede obtener sus ViewModels internamente con viewModel()
            Community(
                navController = navController,            // o nav si prefieres
                communityId = communityId,
                communityVm = communityViewModel,              // inyecta VMs si quieres
                postVm = postViewModel
            )
        }

         */


        /*
        composable(
            route = "createPost/{communityId}",
            arguments = listOf(navArgument("communityId") { type = NavType.IntType })
        ) { backStackEntry ->
            val communityId = backStackEntry.arguments?.getInt("communityId") ?: 0
            CreatePost(
                navController = navController,
                communityId = communityId,
                postVM = postViewModel,
                userVM = userViewModel

            )
        }

        */

    }
}