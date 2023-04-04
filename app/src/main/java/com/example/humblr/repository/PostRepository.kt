package com.example.humblr.repository

import android.util.Log
import com.example.humblr.api.IRedditApi
import com.example.humblr.models.PostsModel
import com.example.humblr.models.SubredditModel
import com.example.humblr.models.UserCommentsModel
import com.example.humblr.models.UserModel
import com.example.humblr.utils.Resource
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class PostRepository(
    private val _api: IRedditApi
) {

    suspend fun getTrial(author: String): Response<SubredditModel> {
        return _api.getSubreddit(author)
    }

    fun voteUp(id: String) {
        _api.vote(id, 1)
    }

    fun voteDown(id: String) {
        _api.vote(id, -1)
    }

    suspend fun subscribe(name: String) {
        _api.subscribe(name, "sub")
    }

    suspend fun unsubscribe(name: String) {
        _api.subscribe(name, "unsub")
    }

    suspend fun savePost(id: String){
        _api.savePost(id)
    }

    suspend fun unsavePost(id: String){
        _api.unsavePost(id)
    }

    suspend fun getUserCommentsCheck(author: String, after: String?): Resource<UserCommentsModel>{
        return try {
            val apiResponse = _api.getUserComments(author = author, after = after)
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

    suspend fun getUser(author: String): Resource<UserModel> {
        return try {
            val apiResponse = _api.getUser(author)
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

    suspend fun getPost(id: String): Resource<List<PostsModel?>> {
        return try {
            val apiResponse = _api.getPost(id)
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

    suspend fun getSubreddit(subreddit: String): Resource<SubredditModel> {
        return try {
            val apiResponse = _api.getSubreddit(subreddit)
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