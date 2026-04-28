package com.example.gigmap_frontend_sprint1.view

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gigmap_frontend_sprint1.R
import com.example.gigmap_frontend_sprint1.viewmodel.UserViewModel

@Composable
fun SignIn(nav: NavHostController, viewModel: UserViewModel) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("") }

    val context = LocalContext.current


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 30.dp, vertical = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { nav.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Regresar",
                    tint = Color(0xFF33363F)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Image(
            painter = painterResource(id = R.drawable.logo_gigmap),
            contentDescription = null,
            modifier = Modifier
                .padding(vertical = 20.dp)
                .size(100.dp)
                .clip(CircleShape)
        )

        Text(
            text = "Regístrate",
            color = Color(0xFF5C0F1A),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Email
        Text(text = "Email", fontSize = 14.sp, color = Color.Black,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 4.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("Ingresa tu correo electrónico") },
            singleLine = true,
            shape = RoundedCornerShape(15.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF5C0F1A),
                unfocusedBorderColor = Color.Gray,
                cursorColor = Color(0xFF5C0F1A)
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = "Contraseña",
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 4.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("Ingresa tu contraseña") },
            singleLine = true,
            shape = RoundedCornerShape(15.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF5C0F1A),
                unfocusedBorderColor = Color.Gray,
                cursorColor = Color(0xFF5C0F1A)
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "8–32 caracteres, incluyendo letras, números y caracteres especiales",
            color = Color.Gray,
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.Start).padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = "Tipo de usuario",
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 4.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { selectedRole = "ARTIST" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedRole == "ARTIST") Color(0xFF5C0F1A) else Color.Transparent,
                    contentColor = if (selectedRole == "ARTIST") Color.White else Color.Black
                ),
                border = BorderStroke(1.dp, Color(0xFF5C0F1A)),
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("Artista")
            }

            Button(
                onClick = { selectedRole = "FAN" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedRole == "FAN") Color(0xFF5C0F1A) else Color.Transparent,
                    contentColor = if (selectedRole == "FAN") Color.White else Color.Black
                ),
                border = BorderStroke(1.dp, Color(0xFF5C0F1A)),
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("Fan")
            }
        }

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = "Username",
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            placeholder = { Text("Ingresa tu username") },
            singleLine = true,
            shape = RoundedCornerShape(15.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF5C0F1A),
                unfocusedBorderColor = Color.Gray,
                cursorColor = Color(0xFF5C0F1A)
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Elige tu username. Puedes cambiarlo después en configuración.",
            color = Color.Gray,
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.Start).padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))


        Button(
            onClick = {
                when {
                    email.isBlank() || password.isBlank() || username.isBlank() || selectedRole.isBlank() -> {
                        Toast.makeText(
                            context,
                            "Por favor, completa todos los campos antes de continuar.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    password.length < 6 -> {
                        Toast.makeText(
                            context,
                            "La contraseña debe tener al menos 6 caracteres.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> {

                        viewModel.register(
                            email = email,
                            username = username,
                            password = password,
                            role = selectedRole,
                            context = context
                        ) {

                            nav.navigate("login")
                        }
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C0F1A)),
            shape = RectangleShape,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                text = "Siguiente",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
        }

        Row {
            Text(text = "¿Ya tienes una cuenta? ", color = Color.Black)
            Text(
                text = "Inicia sesión",
                color = Color(0xFF5C0F1A),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    nav.navigate("login")
                }
            )
        }
    }
}