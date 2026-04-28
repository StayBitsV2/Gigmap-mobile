package com.example.gigmap_frontend_sprint1.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gigmap_frontend_sprint1.model.client.RetrofitClient
import com.example.gigmap_frontend_sprint1.model.RelatedEvent
import com.example.gigmap_frontend_sprint1.model.RelatedEventCreateRequest
import com.example.gigmap_frontend_sprint1.model.RelatedEventParticipantRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RelatedEventViewModel : ViewModel() {

    var listaRelatedEvents = mutableStateListOf<RelatedEvent>()


    fun getRelatedEvents() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.webService.getRelatedEvents()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        listaRelatedEvents.clear()
                        listaRelatedEvents.addAll(response.body()!!)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    fun loadRelatedEventsByConcertId(
        concertId: Long,
        onResult: (List<RelatedEvent>) -> Unit = {}
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.webService.getRelatedEventsByConcertId(concertId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val eventsForConcert = response.body()!!
                        listaRelatedEvents.clear()
                        listaRelatedEvents.addAll(eventsForConcert)
                        onResult(eventsForConcert)
                    } else {
                        listaRelatedEvents.clear()
                        onResult(emptyList())
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    listaRelatedEvents.clear()
                    onResult(emptyList())
                }
            }
        }
    }

    /** Si ya llamaste antes a getRelatedEvents(), puedes usar esto sin volver a pegarle a la API */
    fun relatedEventsForConcert(concertId: Long): List<RelatedEvent> {
        return listaRelatedEvents.filter { it.concertId.toLong() == concertId }
    }

    fun createRelatedEvent(
        request: RelatedEventCreateRequest,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("RelatedEventVM", "createRelatedEvent: request=$request")
                val response = RetrofitClient.webService.createRelatedEvent(request)

                Log.d("RelatedEventVM", "Response code: ${response.code()}")
                Log.d("RelatedEventVM", "Response body: ${response.body()}")
                Log.d("RelatedEventVM", "Response error: ${response.errorBody()?.string()}")

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val newRelated = response.body()!!
                        listaRelatedEvents.add(newRelated)
                        onResult(true)
                    } else {
                        onResult(false)
                    }
                }
            } catch (e: Exception) {
                Log.e("RelatedEventVM", "Exception: ${e.message}")
                e.printStackTrace()
                withContext(Dispatchers.Main) { onResult(false) }
            }
        }
    }
    fun joinRelatedEvent(relatedEventId: Int, userId: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.webService.joinRelatedEvent(
                    RelatedEventParticipantRequest(relatedEventId, userId)
                )
                withContext(Dispatchers.Main) { onResult(response.isSuccessful) }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) { onResult(false) }
            }
        }
    }

    fun leaveRelatedEvent(relatedEventId: Int, userId: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.webService.leaveRelatedEvent(
                    RelatedEventParticipantRequest(relatedEventId, userId)
                )
                withContext(Dispatchers.Main) { onResult(response.isSuccessful) }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) { onResult(false) }
            }
        }
    }
}
