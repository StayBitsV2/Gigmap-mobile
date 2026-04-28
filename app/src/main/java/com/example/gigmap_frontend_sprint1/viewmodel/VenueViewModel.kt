package com.example.gigmap_frontend_sprint1.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gigmap_frontend_sprint1.model.Venue
import com.example.gigmap_frontend_sprint1.model.client.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VenueViewModel : ViewModel() {

    var listaVenues: ArrayList<Venue> by mutableStateOf(arrayListOf())

    fun getVenues() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = RetrofitClient.webService.getConcerts() // venues dentro de conciertos
            withContext(Dispatchers.Main) {
                if (response.body() != null) {
                    val concerts = response.body() ?: emptyList()
                    listaVenues = ArrayList(concerts.map { it.venue })
                }
            }
        }
    }
}