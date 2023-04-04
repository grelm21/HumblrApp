package com.example.humblr.viewmodels

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.humblr.models.PostsModel
import com.example.humblr.models.Subreddit
import com.example.humblr.models.SubredditModel
import com.example.humblr.models.UserModel
import com.example.humblr.repository.PostRepository
import com.example.humblr.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PostViewModel(private val _repo: PostRepository): ViewModel() {

    private val _post = MutableLiveData<Resource<List<PostsModel?>>>()
    val post = _post.asFlow()

    private val _user = MutableLiveData<Resource<UserModel>>()
    val user = _user.asFlow()

    private val _subreddit = MutableLiveData<Resource<SubredditModel>>()
    val subreddit = _subreddit.asFlow()

//    fun getPOst(id: String){
//        val coroutine = CoroutineScope(Dispatchers.IO)
//        coroutine.launch {
//            val response = _repo.getPostTrial(id)
//            if (response.isSuccessful){
//                Log.i("TRIAL_RESPONSE", response.body().toString())
//            }else{
//                Log.i("TRIAL_RESPONSE", response.message())
//            }
//        }
//    }

    fun getUserTrial(subreddit: String){
        val coroutine = CoroutineScope(Dispatchers.IO)
        coroutine.launch {
            val response = _repo.getTrial(subreddit)

            if (response.isSuccessful){
                Log.i("TRIAL_RESPONSE", response.body().toString())
            }else{
                Log.i("TRIAL_RESPONSE", response.message())
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

    fun savePost(id: String){
        val coroutine = CoroutineScope(Dispatchers.IO)
        coroutine.launch {
            _repo.savePost(id)
        }
    }

    fun unsavePost(id: String){
        val coroutine = CoroutineScope(Dispatchers.IO)
        coroutine.launch {
            _repo.unsavePost(id)
        }
    }

    fun voteUp(id: String){
        _repo.voteUp(id)
    }

    fun voteDown(id: String){
        _repo.voteDown(id)
    }


    @SuppressLint("SuspiciousIndentation")
    fun getPost(id: String){
        val coroutine = CoroutineScope(Dispatchers.IO)
        coroutine.launch {
            val post = _repo.getPost(id)

                viewModelScope.launch {
                    _post.value = post
                }
        }
    }

    fun getUser(author: String){
        val coroutine = CoroutineScope(Dispatchers.IO)
        coroutine.launch {
            val post = _repo.getUser(author)

            viewModelScope.launch {
                _user.value = post
            }
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