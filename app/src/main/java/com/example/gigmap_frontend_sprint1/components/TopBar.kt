package com.example.gigmap_frontend_sprint1.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gigmap_frontend_sprint1.R

val InterFontFamily = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
    Font(R.font.inter_bold, FontWeight.Bold),
    Font(R.font.inter_black, FontWeight.Black)
)

@Composable
fun TopBar(
    onBackClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    showNotificationIcon: Boolean = true
) {
    val backgroundColor = Color.White
    val iconColor = Color(0xFF5C0F1A)
    val textColor = Color(0xFF5C0F1A)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
    ) {
        Spacer(modifier = Modifier.statusBarsPadding())

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(22.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Text(
                text = "GigMap",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                fontFamily = InterFontFamily,
                color = textColor,
                letterSpacing = 0.sp
            )

            if (showNotificationIcon) {
                IconButton(
                    onClick = onNotificationClick,
                    modifier = Modifier.size(22.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notificaciones",
                        tint = iconColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            } else {
                Spacer(modifier = Modifier.size(22.dp))
            }
        }
    }
}

@Composable
fun ScreenWithTopBar(
    onBackClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            TopBar(
                onBackClick = onBackClick,
                onNotificationClick = onNotificationClick
            )
        }
    ) { paddingValues ->
        content(paddingValues)
    }
}

