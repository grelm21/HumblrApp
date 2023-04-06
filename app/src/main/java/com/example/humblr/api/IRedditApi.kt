package com.example.humblr.api

import com.example.humblr.models.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface IRedditApi {

    @GET("/{category}")
    suspend fun getListOfPosts(
        @Path("category") category: String,
        @Query("after") after: String? = null,
        @Query("limit") limit: Int = 25
    ): Response<PostsModel>

    @GET("/search")
    suspend fun search(
//        @Query("limit") limit: Int = 25,
        @Query("type") type: String = "link",
        @Query("limit") limit: Int = 50,
        @Query("q") query: String
    ): Response<PostsModel>


    @POST("/api/vote")
    fun vote(@Query("id") id: String, @Query("dir") dir: Int): Call<ResponseBody>

    @GET("/comments/{id}/.json")
    suspend fun getPost(
        @Path("id") id: String,
        @Query("depth") depth: Int = 1,
        @Query("context") context: Int = 2,
        @Query("showmedia") showmedia: Boolean = false,
        @Query("limit") limit: Int = 10
    ): Response<List<PostsModel?>>

    @GET("/user/{author}/about")
    suspend fun getUser(
        @Path("author") author: String
    ): Response<UserModel>

    @GET("/r/{subreddit}/about")
    suspend fun getSubreddit(
        @Path("subreddit") subreddit: String
    ): Response<SubredditModel>

    @GET("/user/{author}/comments")
    suspend fun getUserComments(
        @Path("author") author: String,
        @Query("type") type: String = "comments",
        @Query("limit") limit: Int = 10,
        @Query("after") after: String? = null
    ): Response<UserCommentsModel>

    @GET("/api/v1/me")
    suspend fun getMyAccount(): Response<MyAccountModel>

    @GET("/user/{username}/saved/")
    suspend fun getSavedPostsUser(@Path("username") username: String, @Query("type") type: String = "links"): Response<PostsModel>

    @GET("/subreddits/mine/subscriber")
    suspend fun getSavedSubredditsUser(): Response<SubredditList>

    /**
     * action - sub, unsub
     */
    @POST("/api/subscribe")
    suspend fun subscribe(
        @Query("sr_name") sr_name: String,
        @Query("action") action: String
    ): Response<Unit>

    @POST("/api/save")
    suspend fun savePost(
        @Query("id") id: String
    ): Response<Unit>

    @POST("/api/unsave")
    suspend fun unsavePost(
        @Query("id") id: String
    ): Response<Unit>

    companion object {
        const val BASE_URL = "https://oauth.reddit.com"
    }
}