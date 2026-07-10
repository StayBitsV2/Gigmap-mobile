package com.example.gigmap_frontend_sprint1.viewmodel

import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gigmap_frontend_sprint1.model.Comment
import com.example.gigmap_frontend_sprint1.model.Community
import com.example.gigmap_frontend_sprint1.model.CreateCommentRequest
import com.example.gigmap_frontend_sprint1.model.CreateReactionRequest
import com.example.gigmap_frontend_sprint1.model.CreateReportRequest
import com.example.gigmap_frontend_sprint1.model.CreateThreadRequest
import com.example.gigmap_frontend_sprint1.model.ForumDetail
import com.example.gigmap_frontend_sprint1.model.Post
import com.example.gigmap_frontend_sprint1.model.Reaction
import com.example.gigmap_frontend_sprint1.model.Report
import com.example.gigmap_frontend_sprint1.model.client.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("MutableCollectionMutableState")
class ForumViewModel : ViewModel() {

    var forums: ArrayList<Community> by mutableStateOf(arrayListOf())
    var threads: ArrayList<Post> by mutableStateOf(arrayListOf())
    var threadDetail: ForumDetail? by mutableStateOf(null)
    var isLoading by mutableStateOf(false)
    var selectedGenre by mutableStateOf<String?>(null)

    fun getForums() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading = true
            val response = RetrofitClient.webService.getForums()
            withContext(Dispatchers.Main) {
                if (response.body() != null) {
                    forums = response.body() as ArrayList<Community>
                }
                isLoading = false
            }
        }
    }

    fun getForumThreads(forumId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading = true
            val response = RetrofitClient.webService.getForumThreads(forumId)
            withContext(Dispatchers.Main) {
                if (response.body() != null) {
                    threads = response.body() as ArrayList<Post>
                }
                isLoading = false
            }
        }
    }

    fun getThreadDetail(threadId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading = true
            val response = RetrofitClient.webService.getThreadDetail(threadId)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful && response.body() != null) {
                    threadDetail = response.body()
                }
                isLoading = false
            }
        }
    }

    fun createThread(
        forumId: Int,
        title: String,
        content: String,
        imageUrl: String,
        userId: Int,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = CreateThreadRequest(
                    title = title,
                    content = content,
                    imageUrl = imageUrl,
                    communityId = forumId,
                    userId = userId
                )
                val response = RetrofitClient.webService.createThread(forumId, request)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        threads.add(0, response.body()!!)
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

    fun createComment(
        threadId: Int,
        content: String,
        userId: Int,
        onResult: (Boolean, Comment?) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = CreateCommentRequest(
                    threadId = threadId,
                    userId = userId,
                    content = content
                )
                val response = RetrofitClient.webService.createComment(threadId, request)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val comment = response.body()!!
                        val current = threadDetail
                        if (current != null) {
                            threadDetail = current.copy(comments = current.comments + comment)
                        }
                        onResult(true, comment)
                    } else {
                        onResult(false, null)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onResult(false, null) }
            }
        }
    }

    fun toggleThreadReaction(
        threadId: Int,
        emoji: String,
        userId: Int,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val existing = threadDetail?.reactions?.find { it.userId == userId }

                if (existing?.emoji == emoji) {
                    val response = RetrofitClient.webService.removeThreadReaction(threadId, existing.id)
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            val current = threadDetail
                            if (current != null) {
                                threadDetail = current.copy(
                                    reactions = current.reactions.filter { it.id != existing.id }
                                )
                            }
                            onResult(true)
                        } else {
                            onResult(false)
                        }
                    }
                    return@launch
                }

                if (existing != null) {
                    val removeResponse = RetrofitClient.webService.removeThreadReaction(threadId, existing.id)
                    if (!removeResponse.isSuccessful) {
                        withContext(Dispatchers.Main) { onResult(false) }
                        return@launch
                    }
                    withContext(Dispatchers.Main) {
                        val current = threadDetail
                        if (current != null) {
                            threadDetail = current.copy(
                                reactions = current.reactions.filter { it.id != existing.id }
                            )
                        }
                    }
                }

                val request = CreateReactionRequest(
                    threadId = threadId,
                    userId = userId,
                    emoji = emoji
                )
                val response = RetrofitClient.webService.addReaction(threadId, request)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val reaction = response.body()!!
                        val current = threadDetail
                        if (current != null) {
                            threadDetail = current.copy(reactions = current.reactions + reaction)
                        }
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

    fun addReaction(
        threadId: Int,
        emoji: String,
        userId: Int,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = CreateReactionRequest(
                    threadId = threadId,
                    userId = userId,
                    emoji = emoji
                )
                val response = RetrofitClient.webService.addReaction(threadId, request)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val reaction = response.body()!!
                        val current = threadDetail
                        if (current != null) {
                            threadDetail = current.copy(reactions = current.reactions + reaction)
                        }
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

    fun removeReaction(
        threadId: Int,
        reactionId: Int,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.webService.removeThreadReaction(threadId, reactionId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val current = threadDetail
                        if (current != null) {
                            threadDetail = current.copy(
                                reactions = current.reactions.filter { it.id != reactionId }
                            )
                        }
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

    fun addCommentReaction(
        commentId: Int,
        emoji: String,
        userId: Int,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = CreateReactionRequest(
                    commentId = commentId,
                    userId = userId,
                    emoji = emoji
                )
                val response = RetrofitClient.webService.addCommentReaction(commentId, request)
                withContext(Dispatchers.Main) {
                    onResult(response.isSuccessful)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onResult(false) }
            }
        }
    }

    fun removeCommentReaction(
        commentId: Int,
        reactionId: Int,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.webService.removeCommentReaction(commentId, reactionId)
                withContext(Dispatchers.Main) {
                    onResult(response.isSuccessful)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onResult(false) }
            }
        }
    }

    fun reportThread(
        threadId: Int,
        reason: String,
        userId: Int,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = CreateReportRequest(reason = reason, userId = userId)
                val response = RetrofitClient.webService.reportThread(threadId, request)
                withContext(Dispatchers.Main) {
                    onResult(response.isSuccessful)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onResult(false) }
            }
        }
    }

    fun reportComment(
        commentId: Int,
        reason: String,
        userId: Int,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = CreateReportRequest(reason = reason, userId = userId)
                val response = RetrofitClient.webService.reportComment(commentId, request)
                withContext(Dispatchers.Main) {
                    onResult(response.isSuccessful)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onResult(false) }
            }
        }
    }
}
