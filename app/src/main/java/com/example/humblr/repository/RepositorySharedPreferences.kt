package com.example.humblr.repository

import com.example.humblr.data.SharedPreference

class RepositorySharedPreferences(private val _pref: SharedPreference.SharedPreference) {

    fun putToken(token: String) {
        _pref.addKeyToPreferences(token)
    }

    fun getToken(): String? {
        return _pref.getKeyFromPreferences()
    }

    fun clearToken() {
        _pref.clearToken()
    }
}