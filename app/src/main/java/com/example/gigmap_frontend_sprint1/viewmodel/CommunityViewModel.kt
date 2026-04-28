package com.example.gigmap_frontend_sprint1.viewmodel

import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gigmap_frontend_sprint1.model.Community
import com.example.gigmap_frontend_sprint1.model.client.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

@SuppressLint("MutableCollectionMutableState")

class CommunityViewModel : ViewModel() {

    var listaCommunities: ArrayList<Community> by mutableStateOf(arrayListOf())

    fun getCommunities() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = RetrofitClient.webService.getCommunities()
            withContext(Dispatchers.Main) {
                if (response.body() != null) {
                    listaCommunities = response.body() as ArrayList<Community>
                }
            }
        }
    }


    //DATO: Mejor cambiar todos los ids a Int creo :'v
    fun joinCommunity(communityId: Long, userId: Long, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val resp: Response<Void> = RetrofitClient.webService.joinCommunity(communityId, userId)
                withContext(Dispatchers.Main) {
                    if (resp.isSuccessful) {
                        // actualizar lista local: agregar userId a members si no existe
                        listaCommunities = ArrayList(listaCommunities.map { comm ->
                            if (comm.id.toLong() == communityId) {
                                val newMembers = comm.members.toMutableList()
                                if (!newMembers.contains(userId.toInt())) {
                                    // adaptar tipos según tu modelo (Long/Int)
                                    newMembers.add(userId.toInt())
                                }
                                comm.copy(members = newMembers)
                            } else comm
                        })
                        onResult(true)
                    } else {
                        onResult(false)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onResult(false) }
            }
        }
    }

    // LEAVE
    fun leaveCommunity(communityId: Long, userId: Long, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val resp: Response<Void> = RetrofitClient.webService.leaveCommunity(communityId, userId)
                withContext(Dispatchers.Main) {
                    if (resp.isSuccessful) {
                        // actualizar lista local: remover userId de members
                        listaCommunities = ArrayList(listaCommunities.map { comm ->
                            if (comm.id.toLong() == communityId) {
                                val newMembers = comm.members.toMutableList()
                                newMembers.remove(userId.toInt())
                                comm.copy(members = newMembers)
                            } else comm
                        })
                        onResult(true)
                    } else {
                        onResult(false)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onResult(false) }
            }
        }
    }


    // dentro de CommunityViewModel

    fun createCommunity(
        name: String,
        image: String,
        description: String,
        onResult: (success: Boolean, created: Community?) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // construir un objeto Community para el POST
                // usamos id = 0 (backend debe asignar el id real)
                val newComm = Community(
                    id = 0,
                    name = name,
                    image = image,
                    description = description,
                    posts = listOf(),
                    members = listOf()
                )

                val resp: Response<Community> = RetrofitClient.webService.createCommunity(newComm)
                withContext(Dispatchers.Main) {
                    if (resp.isSuccessful && resp.body() != null) {
                        val created = resp.body()!!
                        // agregar a la lista local (al inicio)
                        val updated = ArrayList<Community>()
                        updated.addAll(listOf(created))
                        updated.addAll(listaCommunities)
                        listaCommunities = updated
                        onResult(true, created)
                    } else {
                        onResult(false, null)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onResult(false, null)
                }
            }
        }
    }


}