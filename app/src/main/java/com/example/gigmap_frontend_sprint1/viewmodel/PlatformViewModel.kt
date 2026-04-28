package com.example.gigmap_frontend_sprint1.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gigmap_frontend_sprint1.model.Platform
import com.example.gigmap_frontend_sprint1.model.client.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlatformViewModel : ViewModel() {

    var listaPlatforms: ArrayList<Platform> by mutableStateOf(arrayListOf())

    fun getPlatforms() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = RetrofitClient.webService.getConcerts() // plataformas vienen dentro
            withContext(Dispatchers.Main) {
                if (response.body() != null) {
                    val concerts = response.body() ?: emptyList()
                    listaPlatforms = ArrayList(concerts.map { it.platform })
                }
            }
        }
    }
}