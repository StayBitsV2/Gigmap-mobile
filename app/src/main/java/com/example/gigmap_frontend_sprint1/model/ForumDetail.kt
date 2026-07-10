package com.example.gigmap_frontend_sprint1.model

data class ForumDetail(
    val thread: Post,
    val comments: List<Comment>,
    val reactions: List<Reaction>
)
