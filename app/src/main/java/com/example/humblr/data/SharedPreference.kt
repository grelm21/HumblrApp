package com.example.humblr.data

import android.content.SharedPreferences
import com.example.humblr.data.SharedPreference.SharedPreference.Constants.TOKEN_KEY

class SharedPreference {
    class SharedPreference(private val _pref: SharedPreferences) {
        fun addKeyToPreferences(token: String){
            _pref.edit().putString(TOKEN_KEY, token).apply()
        }

        fun getKeyFromPreferences(): String?{
            val token = _pref.getString(TOKEN_KEY, null)
            return if (token != null){
                "Bearer $token"
            }else{
                null
            }
//        return _pref.getString(TOKEN_KEY, null)
        }

        fun clearToken() {
            _pref.edit().putString(TOKEN_KEY, null).apply()
        }

        object Constants {
            const val PREFERENCE_KEY = "shared_preference"
            const val TOKEN_KEY = "token_key"
        }
    }
}