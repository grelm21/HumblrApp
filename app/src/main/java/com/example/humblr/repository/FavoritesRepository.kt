package com.example.humblr.repository

import android.util.Log
import com.example.humblr.api.IRedditApi
import com.example.humblr.models.MyAccountModel
import com.example.humblr.models.PostsModel
import com.example.humblr.models.SubredditList
import com.example.humblr.utils.Resource
import retrofit2.HttpException
import java.io.IOException

class FavoritesRepository(
    private val _api: IRedditApi
) {

    suspend fun getProfile(): Resource<MyAccountModel> {
        return try {
            val apiResponse = _api.getMyAccount()
            return if (apiResponse.isSuccessful) {
                Resource.Success(apiResponse.body()!!)
            } else {
                Log.i("ERRROR", apiResponse.message())
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

    suspend fun getSavedPosts(username: String): Resource<PostsModel> {
        return try {
            val apiResponse = _api.getSavedPostsUser(username)
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

    suspend fun subscribe(name: String) {
        _api.subscribe(name, "sub")
    }

    suspend fun unsubscribe(name: String) {
        _api.subscribe(name, "unsub")
    }

    suspend fun getUserSubreddits(): Resource<SubredditList>{
        return try {
            val apiResponse = _api.getSavedSubredditsUser()
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