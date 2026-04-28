package com.example.gigmap_frontend_sprint1.model.response

import com.example.gigmap_frontend_sprint1.model.AttendeeRequest
import com.example.gigmap_frontend_sprint1.model.Community
import com.example.gigmap_frontend_sprint1.model.ConcertCreateRequest

import com.example.gigmap_frontend_sprint1.model.Notification
import com.example.gigmap_frontend_sprint1.model.Concerts
import com.example.gigmap_frontend_sprint1.model.CreateDeviceTokenRequest
import com.example.gigmap_frontend_sprint1.model.LoginRequest
import com.example.gigmap_frontend_sprint1.model.LoginResponse
import com.example.gigmap_frontend_sprint1.model.Post
import com.example.gigmap_frontend_sprint1.model.PostCreateRequest
import com.example.gigmap_frontend_sprint1.model.RegisterRequest
import com.example.gigmap_frontend_sprint1.model.RelatedEvent
import com.example.gigmap_frontend_sprint1.model.RelatedEventCreateRequest
import com.example.gigmap_frontend_sprint1.model.RelatedEventParticipantRequest
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
}