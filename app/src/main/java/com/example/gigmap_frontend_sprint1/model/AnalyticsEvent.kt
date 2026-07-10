package com.example.gigmap_frontend_sprint1.model

data class CreateAnalyticsEventRequest(
    val eventType: String,
    val userId: Long,
    val metadata: String? = null
)
