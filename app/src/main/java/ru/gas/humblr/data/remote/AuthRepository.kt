package ru.gas.humblr.data.remote

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import net.openid.appauth.*
import ru.gas.humblr.authorization.AuthConfig
import ru.gas.humblr.data.remote.models.TokenStorage
import ru.gas.humblr.data.remote.models.TokensModel
import javax.inject.Singleton
import kotlin.coroutines.suspendCoroutine

@Singleton
class AuthRepository {

    val serviceConfiguration = AuthorizationServiceConfiguration(
        Uri.parse(AuthConfig.AUTH_URI),
        Uri.parse(AuthConfig.TOKEN_URI),
        null, // registration endpoint
        Uri.parse(AuthConfig.END_SESSION_URI)
    )

    fun getAuthRequest(): AuthorizationRequest {
        val redirectUri = Uri.parse(AuthConfig.CALLBACK_URL)

        return AuthorizationRequest.Builder(
            serviceConfiguration,
            AuthConfig.CLIENT_ID,
            AuthConfig.RESPONSE_TYPE,
            redirectUri
        )
            .setState(AuthConfig.STATE)
            .setAdditionalParameters(mapOf(Pair(DURATION, AuthConfig.DURATION)))
            .setScope(AuthConfig.SCOPE)
            .build()

    }


    fun sendAuthRequest(authService: AuthorizationService): Intent {
        val authRequest = getAuthRequest()
        val customTabsIntent = CustomTabsIntent.Builder().build()
        return authService.getAuthorizationRequestIntent(authRequest, customTabsIntent)
    }


    fun performRequestToken(code: String, authService: AuthorizationService, onSuccess:()->Unit) {

        val clientAuth: ClientAuthentication = ClientSecretBasic(AuthConfig.CLIENT_SECRET)
        authService.performTokenRequest(
            TokenRequest.Builder(serviceConfiguration, AuthConfig.CLIENT_ID)
                .setAuthorizationCode(code)
                .setRedirectUri(AuthConfig.CALLBACK_URL.toUri())
                .setGrantType(GrantTypeValues.AUTHORIZATION_CODE)
                .build(),
            clientAuth
        ) { response, exception ->
            if (response != null) {
                TokenStorage.accessToken = response.accessToken
                TokenStorage.refreshToken = response.refreshToken
                onSuccess()
            }
            if (exception != null) {
                Log.d("Auth1", "Auth exception ${response?.accessToken.toString()}")

            }
        }

    }


    suspend fun performRefreshTokenSuspend(
        authService: AuthorizationService,
    ): TokensModel {
        val clientAuth: ClientAuthentication = ClientSecretBasic(AuthConfig.CLIENT_SECRET)
        return suspendCoroutine { continuation ->
            authService.performTokenRequest(
                TokenRequest.Builder(serviceConfiguration, AuthConfig.CLIENT_ID)
                    .setGrantType(GrantTypeValues.REFRESH_TOKEN)
                    .setRefreshToken(TokenStorage.refreshToken)
                    .build(),
                clientAuth
            ) { response, ex ->
                when {
                    response != null -> {
                        val tokens = TokensModel(
                            accessToken = response.accessToken.orEmpty(),
                            refreshToken = response.refreshToken.orEmpty(),
                            idToken = response.idToken.orEmpty()
                        )
                        continuation.resumeWith(Result.success(tokens))
                    }
                    ex != null -> {
                        continuation.resumeWith(Result.failure(ex))
                    }
                    else -> error("unreachable")
                }
            }
        }
    }
    companion object {
        const val DURATION = "duration"
    }
}
