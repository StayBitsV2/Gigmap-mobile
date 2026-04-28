package com.example.gigmap_frontend_sprint1.view

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gigmap_frontend_sprint1.components.InterFontFamily
import com.example.gigmap_frontend_sprint1.model.Notification
import com.example.gigmap_frontend_sprint1.viewmodel.NotificationViewModel
import java.time.Duration
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotificationsList(notificationVm: NotificationViewModel = viewModel()) {
    val context = LocalContext.current
    val sharedPrefs = context.getSharedPreferences("pref1", Context.MODE_PRIVATE)
    val userId = sharedPrefs.getInt("userId", -1)
    val notifications = notificationVm.notificationsList

    LaunchedEffect(userId) {
        if (userId != -1) {
            notificationVm.getNotificationsByUserId(userId)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            items(notifications) { notif ->
                NotificationCard(notif)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotificationCard(item: Notification) {
    val isRead = item.readAt != null

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.hsl(0f, 0f, 0.95f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = InterFontFamily,
                    modifier = Modifier.weight(1f)
                )

                if (!isRead) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Color(0xFF0066FF), RoundedCornerShape(50))
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = item.body,
                fontSize = 15.sp,
                fontFamily = InterFontFamily
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Recibido: ${formatNotificationDate(item.createdAt)}",
                fontSize = 13.sp,
                color = Color.Gray,
                fontFamily = InterFontFamily
            )

            if (isRead) {
                Text(
                    text = "Leído",
                    fontSize = 13.sp,
                    color = Color(0xFF00AA55),
                    fontFamily = InterFontFamily
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatNotificationDate(dateString: String): String {
    val date = try {
        LocalDateTime.parse(dateString)
            .atOffset(ZoneOffset.UTC)
    } catch (e: Exception) {
        return ""
    }

    val now = OffsetDateTime.now(ZoneOffset.UTC)
    val diff = Duration.between(date, now)
    val seconds = diff.seconds

    return when {
        seconds < 60 -> "Hace $seconds segundos"
        seconds < 3600 -> "Hace ${seconds / 60} minutos"
        seconds < 86400 -> "Hace ${seconds / 3600} horas"
        else -> date.format(DateTimeFormatter.ofPattern("dd-MM"))
    }
}