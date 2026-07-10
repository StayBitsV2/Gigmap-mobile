package com.example.gigmap_frontend_sprint1.model.response

import com.example.gigmap_frontend_sprint1.model.AttendeeRequest
import com.example.gigmap_frontend_sprint1.model.CreateAnalyticsEventRequest
import com.example.gigmap_frontend_sprint1.model.Comment
import com.example.gigmap_frontend_sprint1.model.Community
import com.example.gigmap_frontend_sprint1.model.ConcertCreateRequest
import com.example.gigmap_frontend_sprint1.model.ConnectionRequestResource
import com.example.gigmap_frontend_sprint1.model.ConnectionResource
import com.example.gigmap_frontend_sprint1.model.CreateCommentRequest
import com.example.gigmap_frontend_sprint1.model.CreateConnectionRequest
import com.example.gigmap_frontend_sprint1.model.CreateReactionRequest
import com.example.gigmap_frontend_sprint1.model.CreateReportRequest
import com.example.gigmap_frontend_sprint1.model.CreateThreadRequest
import com.example.gigmap_frontend_sprint1.model.ForumDetail
import com.example.gigmap_frontend_sprint1.model.Notification
import com.example.gigmap_frontend_sprint1.model.Concerts
import com.example.gigmap_frontend_sprint1.model.CreateDeviceTokenRequest
import com.example.gigmap_frontend_sprint1.model.LoginRequest
import com.example.gigmap_frontend_sprint1.model.LoginResponse
import com.example.gigmap_frontend_sprint1.model.Post
import com.example.gigmap_frontend_sprint1.model.PostCreateRequest
import com.example.gigmap_frontend_sprint1.model.Reaction
import com.example.gigmap_frontend_sprint1.model.RegisterRequest
import com.example.gigmap_frontend_sprint1.model.RelatedEvent
import com.example.gigmap_frontend_sprint1.model.RelatedEventCreateRequest
import com.example.gigmap_frontend_sprint1.model.RelatedEventParticipantRequest
import com.example.gigmap_frontend_sprint1.model.Report
import com.example.gigmap_frontend_sprint1.model.ArtistStats
import com.example.gigmap_frontend_sprint1.model.UserEditRequest
import com.example.gigmap_frontend_sprint1.model.Users
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface WebService {

    @POST("api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<Users>

    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    @GET("api/v1/concerts")
    suspend fun getConcerts(): Response<List<Concerts>>

    @GET("api/v1/concerts/{id}")
    suspend fun getConcertById(@Path("id") id: Long): Response<Concerts>

    @POST("api/v1/concerts")
    suspend fun createConcert(@Body concert: ConcertCreateRequest): Response<Concerts>


    // en tu interface Retrofit (ej: WebService.kt)
// ConcertService.kt
// ConcertService.kt
    @POST("api/v1/concerts/attendees")
    suspend fun addAttendee(@Body request: AttendeeRequest): Response<Void>

    @HTTP(method = "DELETE", path = "api/v1/concerts/attendees", hasBody = true)
    suspend fun removeAttendee(@Body request: AttendeeRequest): Response<Void>



    @GET("api/v1/users")
    suspend fun getUsers(): Response<List<Users>>

    @PUT("api/v1/users/{userId}")
    suspend fun updateUser(
        @Path("userId") userId: Long,
        @Body request: UserEditRequest
    ): Response<Users>

    @GET("api/v1/users/{userId}")
    suspend fun getUserById(
        @Path("userId") userId: Long
    ): Response<Users>

    @GET("api/v1/communities")
    suspend fun getCommunities(): Response<List<Community>>

    @POST("api/v1/communities")
    suspend fun createCommunity(@Body community: Community): Response<Community>

    // unir a comunidad
    @POST("api/v1/communities/{communityId}/join")
    suspend fun joinCommunity(
        @Path("communityId") communityId: Long,
        @Query("userId") userId: Long
    ): Response<Void>

    // salir de comunidad
    @DELETE("api/v1/communities/{communityId}/leave")
    suspend fun leaveCommunity(
        @Path("communityId") communityId: Long,
        @Query("userId") userId: Long
    ): Response<Void>


    @GET("api/v1/posts")
    suspend fun getPosts(): Response<List<Post>>


    @POST("api/v1/posts")
    suspend fun createPost(@Body postRequest: PostCreateRequest): Response<Post>

    @POST("api/v1/posts/{postId}/like")
    suspend fun likePost(
        @Path("postId") postId: Long,
        @Query("userId") userId: Long
    ): Response<Void>

    @DELETE("api/v1/posts/{postId}/unlike")
    suspend fun unlikePost(
        @Path("postId") postId: Long,
        @Query("userId") userId: Long
    ): Response<Void>



    @GET("api/v1/concerts/artist/{artistId}")
    suspend fun getConcertsByArtist(
        @Path("artistId") artistId: Long
    ): Response<List<Concerts>>

    @GET("api/v1/posts/liked_by/{userId}")
    suspend fun getPostsLikedByUser(
        @Path("userId") userId: Long
    ): Response<List<Post>>


    @GET("api/v1/related-events")
    suspend fun getRelatedEvents(): Response<List<RelatedEvent>>

    @POST("api/v1/related-events")
    suspend fun createRelatedEvent(
        @Body request: RelatedEventCreateRequest
    ): Response<RelatedEvent>

    @GET("api/v1/related-events/concert/{concertId}")
    suspend fun getRelatedEventsByConcertId(
        @Path("concertId") concertId: Long
    ): Response<List<RelatedEvent>>

    @POST("api/v1/related-events/participants")
    suspend fun joinRelatedEvent(@Body request: RelatedEventParticipantRequest): Response<Void>

    @HTTP(method = "DELETE", path = "api/v1/related-events/participants", hasBody = true)
    suspend fun leaveRelatedEvent(@Body request: RelatedEventParticipantRequest): Response<Void>

    @GET("api/v1/concerts/genre/{genre}")
    suspend fun getConcertsByGenre(@Path("genre") genre: String): Response<List<Concerts>>

    @POST("api/v1/device_tokens")
    suspend fun createDeviceToken(@Body deviceTokenRequest: CreateDeviceTokenRequest): Response<Unit>

    @GET("api/v1/notifications/user/{userId}")
    suspend fun getAllNotificationsByUserId(@Path("userId") userId: Int) : Response<List<Notification>>

    @GET("api/v1/artists/{artistId}/stats")
    suspend fun getArtistStats(
        @Path("artistId") artistId: Long
    ): Response<ArtistStats>

    @PUT("api/v1/users/{userId}/follow/{artistId}")
    suspend fun followArtist(
        @Path("userId") userId: Long,
        @Path("artistId") artistId: Long
    ): Response<Void>

    @PUT("api/v1/users/{userId}/unfollow/{artistId}")
    suspend fun unfollowArtist(
        @Path("userId") userId: Long,
        @Path("artistId") artistId: Long
    ): Response<Void>

    @GET("api/v1/users/{userId}/following/{artistId}")
    suspend fun isFollowingArtist(
        @Path("userId") userId: Long,
        @Path("artistId") artistId: Long
    ): Response<Boolean>

    @GET("api/v1/users/{userId}/following")
    suspend fun getFollowedArtists(
        @Path("userId") userId: Long
    ): Response<List<Users>>

    // ── Connections ──────────────────────────────────────────────────────────
    @POST("api/v1/connections/requests")
    suspend fun createConnectionRequest(@Body request: CreateConnectionRequest): Response<ConnectionRequestResource>

    @GET("api/v1/connections/requests/incoming")
    suspend fun getIncomingConnectionRequests(@Query("userId") userId: Long): Response<List<ConnectionRequestResource>>

    @GET("api/v1/connections/requests/outgoing")
    suspend fun getOutgoingConnectionRequests(@Query("userId") userId: Long): Response<List<ConnectionRequestResource>>

    @PUT("api/v1/connections/requests/{requestId}/accept")
    suspend fun acceptConnectionRequest(@Path("requestId") requestId: Long): Response<Void>

    @DELETE("api/v1/connections/requests/{requestId}/reject")
    suspend fun rejectConnectionRequest(@Path("requestId") requestId: Long): Response<Void>

    @GET("api/v1/connections")
    suspend fun getUserConnections(@Query("userId") userId: Long): Response<List<ConnectionResource>>

    @GET("api/v1/connections/check")
    suspend fun checkConnection(@Query("userId1") userId1: Long, @Query("userId2") userId2: Long): Response<Boolean>

    // ── Forums (US40) ───────────────────────────────────────────
    @GET("api/v1/forums")
    suspend fun getForums(): Response<List<Community>>

    @GET("api/v1/forums/{forumId}/threads")
    suspend fun getForumThreads(@Path("forumId") forumId: Int): Response<List<Post>>

    @GET("api/v1/forums/threads/{threadId}")
    suspend fun getThreadDetail(@Path("threadId") threadId: Int): Response<ForumDetail>

    @POST("api/v1/forums/{forumId}/threads")
    suspend fun createThread(@Path("forumId") forumId: Int, @Body request: CreateThreadRequest): Response<Post>

    @POST("api/v1/forums/threads/{threadId}/comments")
    suspend fun createComment(@Path("threadId") threadId: Int, @Body request: CreateCommentRequest): Response<Comment>

    @POST("api/v1/forums/threads/{threadId}/reactions")
    suspend fun addReaction(@Path("threadId") threadId: Int, @Body request: CreateReactionRequest): Response<Reaction>

    @DELETE("api/v1/forums/threads/{threadId}/reactions/{reactionId}")
    suspend fun removeThreadReaction(@Path("threadId") threadId: Int, @Path("reactionId") reactionId: Int): Response<Void>

    @POST("api/v1/forums/threads/{threadId}/reports")
    suspend fun reportThread(@Path("threadId") threadId: Int, @Body request: CreateReportRequest): Response<Report>

    @POST("api/v1/forums/comments/{commentId}/reactions")
    suspend fun addCommentReaction(@Path("commentId") commentId: Int, @Body request: CreateReactionRequest): Response<Reaction>

    @DELETE("api/v1/forums/comments/{commentId}/reactions/{reactionId}")
    suspend fun removeCommentReaction(@Path("commentId") commentId: Int, @Path("reactionId") reactionId: Int): Response<Void>

    @POST("api/v1/forums/comments/{commentId}/reports")
    suspend fun reportComment(@Path("commentId") commentId: Int, @Body request: CreateReportRequest): Response<Report>

    // ── Analytics ───────────────────────────────────────────────
    @POST("api/v1/analytics/events")
    suspend fun createAnalyticsEvent(@Body request: CreateAnalyticsEventRequest): Response<Unit>
}