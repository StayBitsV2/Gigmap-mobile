package com.example.gigmap_frontend_sprint1.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gigmap_frontend_sprint1.model.Notification
import com.example.gigmap_frontend_sprint1.model.client.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.collections.arrayListOf

class NotificationViewModel : ViewModel() {

    var notificationsList: ArrayList<Notification> by mutableStateOf(arrayListOf())

    fun getNotificationsByUserId(userId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = RetrofitClient.webService.getAllNotificationsByUserId(userId)

            withContext(Dispatchers.Main) {
                if (response.isSuccessful && response.body() != null) {
                    notificationsList = ArrayList(response.body()!!)
                }
            }
        }
    }
}