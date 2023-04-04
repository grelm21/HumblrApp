package com.example.humblr.repository

import android.util.Log
import com.example.humblr.api.IRedditApi
import com.example.humblr.models.PostsModel
import com.example.humblr.utils.Resource
import retrofit2.HttpException
import java.io.IOException

class FeedRepository(
    private val _api: IRedditApi
) {

    fun voteUp(id: String){
        _api.vote(id, 1)
    }

    fun voteDown(id: String){
        _api.vote(id, -1)
    }

    suspend fun getFeed(
        after: String? = null, category: String): Resource<PostsModel>{
        return try {
            val apiResponse = _api.getListOfPosts(after = after, category = category)
            return if (apiResponse.isSuccessful) {
                Resource.Success(apiResponse.body()!!)
            } else {
                Resource.Error(apiResponse.message())
            }
        } catch (e: HttpException) {
            Resource.Error(errorMessage = e.message ?: "Something went wrong")
        } catch (e: IOException) {
            Resource.Error("Please check your network connection")
        } catch (e: Exception) {
            Resource.Error(errorMessage = "Something went wrong")
        }
    }
}