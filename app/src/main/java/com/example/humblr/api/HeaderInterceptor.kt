package com.example.humblr.api

import com.example.humblr.data.SharedPreference
import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor(
    private val _pref: SharedPreference.SharedPreference
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request = request?.newBuilder()
            ?.addHeader("Authorization", _pref.getKeyFromPreferences())
            ?.build()

        return chain.proceed(request)
    }
}