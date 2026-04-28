package com.example.gigmap_frontend_sprint1.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gigmap_frontend_sprint1.model.Users
import com.example.gigmap_frontend_sprint1.viewmodel.UserViewModel

@Composable
fun UserView(userId: Int, userViewModel: UserViewModel) {
    var user by remember { mutableStateOf<Users?>(null) }
    var hasLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        if (!hasLoaded) {
            userViewModel.getUserById(userId) {
                user = it
                hasLoaded = true
            }
        }
    }

    if (user != null) {
        Text(text = "Username: ${user!!.username}")
    } else {
        Text(text = "Cargando perfil...")
    }
}


