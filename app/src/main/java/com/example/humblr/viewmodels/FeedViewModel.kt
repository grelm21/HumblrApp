package com.example.humblr.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.humblr.data.SharedPreference
import com.example.humblr.models.PostsModel
import com.example.humblr.repository.FeedRepository
import com.example.humblr.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FeedViewModel(
    private val _repo: FeedRepository,
    private val _prefs: SharedPreference.SharedPreference
) : ViewModel() {

    private val _feed = MutableLiveData<Resource<PostsModel>>()
    val feed = _feed.asFlow()

    private val _paginationState = MutableStateFlow(false)
    val paginationState = _paginationState.asStateFlow()

    fun clearToken() {
        _prefs.clearToken()
    }

    fun voteUp(id: String){
        _repo.voteUp(id)
    }

    fun voteDown(id: String){
        _repo.voteDown(id)
    }

    fun getToken() {
        Log.i("HEADER", _prefs.getKeyFromPreferences()!!)
    }

    fun getSubreddits(after: String?, category: String) {
        val coroutine = CoroutineScope(Dispatchers.IO)

        coroutine.launch {
            val reddit = _repo.getFeed(after = after, category = category)
            viewModelScope.launch {
                _feed.value = reddit
            }
        }
    }
}