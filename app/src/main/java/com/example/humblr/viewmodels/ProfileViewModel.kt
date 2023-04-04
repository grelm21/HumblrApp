package com.example.humblr.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.humblr.models.MyAccountModel
import com.example.humblr.models.PostsModel
import com.example.humblr.repository.ProfileRepository
import com.example.humblr.repository.RepositorySharedPreferences
import com.example.humblr.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val _repo: ProfileRepository,
    private val _repoPref: RepositorySharedPreferences
): ViewModel() {

    private val _profile = MutableLiveData<Resource<MyAccountModel>>()
    val profile = _profile.asFlow()

    fun clearToken() {
        _repoPref.clearToken()
    }

    fun getProfile(){
        val coroutine = CoroutineScope(Dispatchers.IO)

        coroutine.launch {
            viewModelScope.launch {
                _profile.value = _repo.getProfile()
            }
        }
    }
}