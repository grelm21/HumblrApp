package com.example.humblr.ui

import android.content.Intent
import android.net.Uri
import android.net.wifi.rtt.CivicLocationKeys.STATE
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import com.example.humblr.R
import com.example.humblr.viewmodels.LoginViewModel
import net.openid.appauth.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity() {

    private val _viewModel: LoginViewModel by viewModel()

    private val _serviceConfig = AuthorizationServiceConfiguration(
        Uri.parse(AUTHORIZE_LINK), //перенаправление на авторизацию
        Uri.parse(TOKEN_LINK) //получение токена
    )

    private val _redirectUri = Uri.parse(REDIRECT_URI)

    private val _authRequestBuilder = AuthorizationRequest.Builder(
        _serviceConfig, CLIENT_ID, ResponseTypeValues.CODE, _redirectUri
    )

    private val _authRequest = _authRequestBuilder
//        .setState("")
        .setScopes(SCOPES)
        .build()

    private val _authService: AuthorizationService by lazy {
        AuthorizationService(this)
    }

    private val _customTabIntent = CustomTabsIntent.Builder().build()

    private val _authResponse =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        }

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

            val logInRequest = _authService.getAuthorizationRequestIntent(
                _authRequest, _customTabIntent
            )

            if (intent.data != null) {

                // если активити запущено уже после авторизации и нужно получить токен
                val code = Uri.parse(intent.data.toString()).getQueryParameter("code")
                val clientAuth: ClientAuthentication = ClientSecretBasic("")
                _authService.performTokenRequest(
                    TokenRequest.Builder(_serviceConfig, CLIENT_ID)
                        .setAuthorizationCode(code)
                        .setRedirectUri(REDIRECT_URI.toUri())
                        .setGrantType(GrantTypeValues.AUTHORIZATION_CODE)
                        .build()
                    , clientAuth
                ) { response, exception -> // если авторизация успешна, вернется token в response, если ошибка, вернется exception
                    if (response != null) {
                        Log.d(
                            "TOKEN", response.accessToken.toString()
                        )

                        Log.d(
                            "TOKEN_REFRESH", response.refreshToken.toString()
                        )

                        Log.d(
                            "TOKEN_SCOPE", response.scope.toString()
                        )
                        _viewModel.putToken(response.accessToken.toString())

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        // авторизация успешна
                    }
                    if (exception != null) {
                        // ошибка авторизации
                        Log.d("TOKEN_EX", exception.toString())
                    }
                }
            }

            findViewById<Button>(R.id.login_button).setOnClickListener {
                _authResponse.launch(logInRequest)
            }
    }

    companion object {
        const val AUTHORIZE_LINK = "https://www.reddit.com/api/v1/authorize"
        const val TOKEN_LINK = "https://www.reddit.com/api/v1/access_token"
        const val REDIRECT_URI = "app.humblr.grigory://humblrredirect.com/auth"
        const val CLIENT_ID = "H5PvLaki4-q4oC743Dfn9A"
//        const val STATE = "1000"
        const val DURATION = "permanent"

        //        const val SECRET_KEY = "Y7G6v145033UpNW54I43__FIX7dwdj3JP0Xb3x_uEu0"
        const val SCOPES =
            "identity " + "edit " + "flair " + "history " + "modconfig " + "modflair " + "modlog " + "modposts " + "modwiki " + "mysubreddits " + "privatemessages " + "read " + "report " + "save " + "submit " + "subscribe " + "vote " + "wikiedit " + "wikiread "
        const val RC_AUTH = 0
    }
}