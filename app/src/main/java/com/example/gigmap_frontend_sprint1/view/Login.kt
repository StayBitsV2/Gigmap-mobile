package com.example.gigmap_frontend_sprint1.view

import android.content.Context
import android.widget.Toast
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gigmap_frontend_sprint1.R
import com.example.gigmap_frontend_sprint1.viewmodel.UserViewModel

@Composable
fun Login( userViewModel: UserViewModel,
           nav: NavHostController,
           context: Context
){

    //awar
    LaunchedEffect(Unit) {
        userViewModel.loadAuthFromPrefs(context)
    }
    //awar
    LaunchedEffect(userViewModel.currentUserId) {
        if (userViewModel.currentUserId != 0) {
            nav.navigate("homecontent") {
                popUpTo("login") { inclusive = true }
            }
        }
    }




    val pref = context.getSharedPreferences("pref1", Context.MODE_PRIVATE)

    val check: Boolean = pref!!.getBoolean("chk", false)
    val email: String= pref.getString("email", "")!!
    val password: String= pref.getString("pass", "")!!

    var txtEmail by remember { mutableStateOf(email) }
    var txtPass by remember { mutableStateOf(password) }
    var chk by remember { mutableStateOf(check) }

    val editor = pref.edit()

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

        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = painterResource(id = R.drawable.logo_gigmap),
            contentDescription = null,
            modifier = Modifier
                .padding(vertical = 20.dp)
                .size(100.dp)
                .clip(CircleShape)
        )

        Text(
            text = "Inicia sesión",
            color = Color(0xFF5C0F1A),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 40.dp)
        )

        Text(
            text = "Email",
            color = Color.Black,
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 4.dp)
        )

        OutlinedTextField(
            value = txtEmail,
            onValueChange = { txtEmail = it },
            placeholder = { Text("Ingresa tu correo electrónico") },
            singleLine = true,
            shape = RoundedCornerShape(25.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF5C0F1A),
                unfocusedBorderColor = Color.Gray,
                cursorColor = Color(0xFF5C0F1A)
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Contraseña",
            color = Color.Black,
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 4.dp)
        )

        OutlinedTextField(
            value = txtPass,
            onValueChange = { txtPass = it },
            placeholder = { Text("Ingresa tu contraseña") },
            singleLine = true,
            shape = RoundedCornerShape(25.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF5C0F1A),
                unfocusedBorderColor = Color.Gray,
                cursorColor = Color(0xFF5C0F1A)
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(160.dp))


        Button(
            onClick = {
                when {
                    txtEmail.isBlank() || txtPass.isBlank() -> {
                        Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        userViewModel.login(
                            emailOrUsername = txtEmail,
                            password = txtPass,
                            context = context
                        ) {
                            val editor = pref.edit()

                            if (chk) {
                                editor.putString("email", txtEmail)
                                editor.putString("pass", txtPass)
                                editor.putBoolean("chk", true)
                            }

                            editor.apply()
                            nav.navigate("homecontent")
                        }
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C0F1A)),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RectangleShape
        ) {
            Text(
                text = "Iniciar sesión",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
        }

        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ){
            Switch(
                checked = chk,
                onCheckedChange = {
                    chk=it
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF5C0F1A),
                    checkedTrackColor = Color(0xFF8B2B3A),
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = Color.LightGray,
                    uncheckedBorderColor = Color.Transparent
                )
            )
            Text(
                text = "Recordar credenciales",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

        }
        Spacer(modifier = Modifier.height(20.dp))


        Row {
            Text(text = "¿No tienes una cuenta? ", color = Color.Black)
            Text(
                text = "Regístrate",
                color = Color(0xFF5C0F1A),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    nav.navigate("signin")
                }
            )
        }


    }

}