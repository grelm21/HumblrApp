package com.example.humblr.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.humblr.models.SubredditModel
import com.example.humblr.repository.PostRepository
import com.example.humblr.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SubredditViewModel(
    private val _repo: PostRepository
): ViewModel() {

    private val _subreddit = MutableLiveData<Resource<SubredditModel>>()
    val subreddit = _subreddit.asFlow()

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

    fun getSubreddit(subreddit: String){
        val coroutine = CoroutineScope(Dispatchers.IO)
        coroutine.launch {
            val post = _repo.getSubreddit(subreddit)

            viewModelScope.launch {
                _subreddit.value = post
            }
        }
    }
}