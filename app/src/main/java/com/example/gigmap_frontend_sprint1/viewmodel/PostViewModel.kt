package com.example.gigmap_frontend_sprint1.viewmodel

import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gigmap_frontend_sprint1.model.Post
import com.example.gigmap_frontend_sprint1.model.PostCreateRequest
import com.example.gigmap_frontend_sprint1.model.client.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
@SuppressLint("MutableCollectionMutableState")
class PostViewModel : ViewModel() {

    var listaPosts: ArrayList<Post> by mutableStateOf(arrayListOf())

    fun getPosts() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = RetrofitClient.webService.getPosts()
            withContext(Dispatchers.Main) {
                if (response.body() != null) {
                    listaPosts = response.body() as ArrayList<Post>
                }
            }
        }
    }

    fun createPost(postRequest: PostCreateRequest, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = RetrofitClient.webService.createPost(postRequest)
            println("Response body: ${response.body()}")
            withContext(Dispatchers.Main) {
                onResult(response.isSuccessful)
                if (response.isSuccessful) {
                    response.body()?.let { newPost ->
                        listaPosts.add(0, newPost)
                    }
                }
            }
        }
    }




    fun toggleLike(postId: Int, userId: Int, onResult: (Boolean, Post?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val idx = listaPosts.indexOfFirst { it.id == postId }
                if (idx < 0) {
                    withContext(Dispatchers.Main) { onResult(false, null) }
                    return@launch
                }

                val current = listaPosts[idx]

                val currentLikes = current.likes?.toMutableList() ?: mutableListOf<Int>()
                val alreadyLiked = currentLikes.contains(userId)

                val optimisticLikes = currentLikes.toMutableList()
                if (!alreadyLiked) optimisticLikes.add(userId) else optimisticLikes.remove(userId)

                val optimisticPost = try {
                    current.copy(likes = optimisticLikes)
                } catch (e: Exception) {

                    val newPost = current

                    newPost
                }


                val newList = ArrayList(listaPosts)
                newList[idx] = optimisticPost
                withContext(Dispatchers.Main) { listaPosts = newList }

                // --- Llamada al backend ---
                val response = if (!alreadyLiked) {
                    RetrofitClient.webService.likePost(postId.toLong(), userId.toLong())
                } else {
                    RetrofitClient.webService.unlikePost(postId.toLong(), userId.toLong())
                }

                println("likePost -> code: ${response.code()}")
                val success = response.isSuccessful

                withContext(Dispatchers.Main) {
                    if (success) {

                        onResult(true, optimisticPost)
                    } else {

                        val revertList = ArrayList(listaPosts)
                        revertList[idx] = current
                        listaPosts = revertList

                        val err = response.errorBody()?.string()
                        println("likePost -> errorBody: $err")
                        onResult(false, null)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()

                withContext(Dispatchers.Main) {

                    val idx2 = listaPosts.indexOfFirst { it.id == postId }
                    if (idx2 >= 0) {
                        val revertList = ArrayList(listaPosts)

                    }
                    onResult(false, null)
                }
            }
        }
    }


    fun getPostsLikedByUser(userId: Long, onResult: (List<Post>?) -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.webService.getPostsLikedByUser(userId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        onResult(response.body())
                    } else {
                        onResult(null)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onResult(null) }
            }
        }
    }




}