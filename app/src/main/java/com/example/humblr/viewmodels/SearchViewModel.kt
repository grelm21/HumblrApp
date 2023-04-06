package com.example.humblr.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.humblr.models.PostsModel
import com.example.humblr.repository.FeedRepository
import com.example.humblr.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchViewModel(private val _repo: FeedRepository): ViewModel() {

    private val _search = MutableLiveData<Resource<PostsModel>>()
    val search = _search.asFlow()

    fun voteUp(id: String){
        _repo.voteUp(id)
    }

    fun voteDown(id: String){
        _repo.voteDown(id)
    }

    fun performSearch(query: String) {
        val coroutine = CoroutineScope(Dispatchers.IO)

        coroutine.launch {
            val rsp = _repo.searchTry(query = query)

            if (rsp.isSuccessful){
                Log.i("SEARCH_RESULTS", rsp.body().toString())
            }else{
                Log.i("SEARCH_RESULTS", rsp.message())
            }

            val reddit = _repo.search(query = query)
            viewModelScope.launch {
                Log.i("SEARCH_RESULTS", reddit.data.toString())
                _search.value = reddit
            }
        }
    }
}