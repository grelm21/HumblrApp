package com.example.humblr.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.humblr.api.IRedditApi
import com.example.humblr.models.MyAccountModel
import com.example.humblr.models.PostsModel
import com.example.humblr.models.SubredditList
import com.example.humblr.repository.FavoritesRepository
import com.example.humblr.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val _repo: FavoritesRepository,
    private val _api: IRedditApi
): ViewModel() {

    private val _saved = MutableLiveData<Resource<PostsModel>>()
    val saved = _saved.asFlow()

    private val _profile = MutableLiveData<Resource<MyAccountModel>>()
    val profile = _profile.asFlow()

    private val _subreddits = MutableLiveData<Resource<SubredditList>>()
    val subreddits = _subreddits.asFlow()

    fun getProfile(){
        val coroutine = CoroutineScope(Dispatchers.IO)

        coroutine.launch {
            viewModelScope.launch {
                _profile.value = _repo.getProfile()
            }
        }
    }

    fun getSavedPosts(username: String) {
        val coroutine = CoroutineScope(Dispatchers.IO)

        coroutine.launch {
            val reddit = _repo.getSavedPosts(username)
            viewModelScope.launch {
                _saved.value = reddit
            }
        }
    }

    fun subscribe(name: String){
        val coroutine = CoroutineScope(Dispatchers.IO)
        coroutine.launch {
            _repo.subscribe(name)
        }
    }

    fun unsubscribe(name: String){
        val coroutine = CoroutineScope(Dispatchers.IO)
        coroutine.launch {
            _repo.unsubscribe(name)
        }
    }

    fun getUserSubreddits(){
        val coroutine = CoroutineScope(Dispatchers.IO)

        coroutine.launch {

            val reddit = _repo.getUserSubreddits()
            Log.i("USER_SUBREDDITS", reddit.message.toString())
            viewModelScope.launch {
                _subreddits.value = reddit
            }
        }
    }
}