package com.example.humblr.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import com.example.humblr.repository.RepositorySharedPreferences

class LoginViewModel(private val _repoPref: RepositorySharedPreferences): ViewModel() {

    private val _token = MutableLiveData<String?>()
    val token = _token.asFlow()

    fun clearToken() {
        _repoPref.clearToken()
    }

    fun putToken(token: String) {
        _repoPref.putToken(token)
    }

    fun getToken() {
        _token.value = _repoPref.getToken()
    }
}