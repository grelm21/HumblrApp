package com.example.humblr.repository

import android.util.Log
import com.example.humblr.api.IRedditApi
import com.example.humblr.models.MyAccountModel
import com.example.humblr.utils.Resource
import retrofit2.HttpException
import java.io.IOException

class ProfileRepository(
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
}