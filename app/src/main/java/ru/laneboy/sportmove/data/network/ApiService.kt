package ru.laneboy.sportmove.data.network

import ru.laneboy.sportmove.data.network.wrapper.ApiResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import ru.laneboy.sportmove.data.network.requests.AddCompetitionDataRequest
import ru.laneboy.sportmove.data.network.requests.AddRequest
import ru.laneboy.sportmove.data.network.requests.ChangeStatusRequest
import ru.laneboy.sportmove.data.network.requests.SignInDataRequest
import ru.laneboy.sportmove.data.network.requests.SignUpDataRequest
import ru.laneboy.sportmove.data.network.responses.AuthDataResponse
import ru.laneboy.sportmove.data.network.responses.CompetitionItemResponse
import ru.laneboy.sportmove.data.network.responses.GameDiagramRequest
import ru.laneboy.sportmove.data.network.responses.RequestItemResponse

interface ApiService {

    //Authentication
    @POST("signin")
    suspend fun singIn(@Body singInData: SignInDataRequest): ApiResponse<AuthDataResponse>

    @POST("signup")
    suspend fun signUp(@Body signUpData: SignUpDataRequest): ApiResponse<AuthDataResponse>

    //Competition
    @POST("api/competition")
    suspend fun addCompetition(@Body addCompetitionData: AddCompetitionDataRequest): ApiResponse<Any>

    @GET("api/competitions")
    suspend fun getCompetitionsList(): ApiResponse<List<CompetitionItemResponse>>

    @GET("api/user/{user_id}/requests")
    suspend fun getRequestList(
        @Path("user_id") userId: Int
    ): ApiResponse<List<RequestItemResponse>>

    @GET("api/competition/{competition_id}/requests")
    suspend fun getRequestsByCompetitionId(
        @Path("competition_id") competitionId: Int
    ): ApiResponse<List<RequestItemResponse>>

    @PATCH("api/request")
    suspend fun changeRequestStatus(
        @Body changeStatusRequest: ChangeStatusRequest
    ): ApiResponse<Any>

    @POST("api/request")
    suspend fun addRequest(
        @Body request: AddRequest
    ): ApiResponse<RequestItemResponse>

    @GET("/api/games/{competitionId}")
    suspend fun getGameList(
        @Path("competitionId") competitionId: Int
    ): ApiResponse<List<GameDiagramRequest>>

    @POST("/api/game")
    suspend fun createGame(
        @Body gameList: List<GameDiagramRequest>
    ): ApiResponse<Unit>

    @GET("/api/game/{competition_id}/{game_id}")
    suspend fun getGame(
        @Path("competition_id") competitionId: Int,
        @Path("game_id") gameId: Int
    ): ApiResponse<GameDiagramRequest>

    @PUT("/api/game")
    suspend fun updateGame(
        @Body gameList: GameDiagramRequest
    ): ApiResponse<Unit>
}