package com.example.gigmap_frontend_sprint1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gigmap_frontend_sprint1.model.ConnectionRequestResource
import com.example.gigmap_frontend_sprint1.model.ConnectionResource
import com.example.gigmap_frontend_sprint1.model.CreateConnectionRequest
import com.example.gigmap_frontend_sprint1.model.client.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConnectionViewModel : ViewModel() {

    fun sendConnectionRequest(targetId: Long, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.webService.createConnectionRequest(CreateConnectionRequest(targetId))
                withContext(Dispatchers.Main) {
                    onResult(response.isSuccessful)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onResult(false) }
            }
        }
    }

    fun getIncomingRequests(userId: Long, onResult: (List<ConnectionRequestResource>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.webService.getIncomingConnectionRequests(userId)
                withContext(Dispatchers.Main) {
                    onResult(response.body() ?: emptyList())
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onResult(emptyList()) }
            }
        }
    }

    fun getOutgoingRequests(userId: Long, onResult: (List<ConnectionRequestResource>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.webService.getOutgoingConnectionRequests(userId)
                withContext(Dispatchers.Main) {
                    onResult(response.body() ?: emptyList())
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onResult(emptyList()) }
            }
        }
    }

    fun acceptRequest(requestId: Long, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.webService.acceptConnectionRequest(requestId)
                withContext(Dispatchers.Main) {
                    onResult(response.isSuccessful)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onResult(false) }
            }
        }
    }

    fun rejectRequest(requestId: Long, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.webService.rejectConnectionRequest(requestId)
                withContext(Dispatchers.Main) {
                    onResult(response.isSuccessful)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onResult(false) }
            }
        }
    }

    fun getConnections(userId: Long, onResult: (List<ConnectionResource>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.webService.getUserConnections(userId)
                withContext(Dispatchers.Main) {
                    onResult(response.body() ?: emptyList())
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onResult(emptyList()) }
            }
        }
    }

    fun checkConnection(userId1: Long, userId2: Long, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.webService.checkConnection(userId1, userId2)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        onResult(response.body()!!)
                    } else {
                        onResult(false)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onResult(false) }
            }
        }
    }
}
