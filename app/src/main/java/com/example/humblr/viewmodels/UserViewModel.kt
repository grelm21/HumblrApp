package com.example.humblr.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.humblr.models.UserCommentsModel
import com.example.humblr.models.UserModel
import com.example.humblr.repository.PostRepository
import com.example.humblr.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(
    private val _repo: PostRepository
) : ViewModel() {

    private val _user = MutableLiveData<Resource<UserModel>>()
    val user = _user.asFlow()

    private val _comments = MutableLiveData<Resource<UserCommentsModel>>()
    val comments = _comments.asFlow()

    fun getUser(author: String) {
        val coroutine = CoroutineScope(Dispatchers.IO)
        coroutine.launch {
            val post = _repo.getUser(author)

            viewModelScope.launch {
                _user.value = post
            }
        }
    }

    fun getUserCommentsCheck(author: String, after: String? = null) {
        val coroutine = CoroutineScope(Dispatchers.IO)

        coroutine.launch {
            viewModelScope.launch {
                _comments.value = _repo.getUserCommentsCheck(author, after)
            }
        }
    }
}