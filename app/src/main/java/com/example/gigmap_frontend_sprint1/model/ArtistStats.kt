package com.example.gigmap_frontend_sprint1.model

import com.google.gson.annotations.SerializedName

data class ArtistStats(
    @SerializedName("artistId") val artistId: Long,
    val weeks: List<WeeklyStats>,
    @SerializedName("hasHistoricalData") val hasHistoricalData: Boolean,
    val message: String?
)

data class WeeklyStats(
    @SerializedName("weekStart") val weekStart: String,
    @SerializedName("weekEnd") val weekEnd: String,
    @SerializedName("newFollowers") val newFollowers: Long,
    @SerializedName("profileViews") val profileViews: Long,
    @SerializedName("externalLinkClicks") val externalLinkClicks: ExternalLinkClicks
)

data class ExternalLinkClicks(
    val spotify: Long,
    val instagram: Long,
    val youtube: Long
)
