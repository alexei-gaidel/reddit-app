package ru.gas.humblr.authorization


import kotlinx.coroutines.runBlocking
import net.openid.appauth.AuthorizationService
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.closeQuietly
import ru.gas.humblr.data.remote.AuthRepository
import ru.gas.humblr.data.remote.models.TokenStorage
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class AuthorizationFailedInterceptor @Inject constructor(
    private val authorizationService: AuthorizationService,
    private val tokenStorage: TokenStorage,
    private val authRepository: AuthRepository
) : Interceptor {


    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequestTimestamp = System.currentTimeMillis()

        val originalResponse = chain.proceed(chain.request())
        return originalResponse
            .takeIf { it.code != 401 }
            ?: handleUnauthorizedResponse(chain, originalResponse, originalRequestTimestamp)
    }

    private fun handleUnauthorizedResponse(
        chain: Interceptor.Chain,
        originalResponse: Response,
        requestTimestamp: Long
    ): Response {
        val latch = getLatch()
        return when {
            latch != null && latch.count > 0 -> handleTokenIsUpdating(
                chain,
                latch,
                requestTimestamp,
                originalResponse
            )
                ?: originalResponse
            tokenUpdateTime > requestTimestamp -> updateTokenAndProceedChain(
                chain,
                originalResponse
            )
            else -> handleTokenNeedRefresh(chain, originalResponse) ?: originalResponse
        }
    }

    private fun handleTokenIsUpdating(
        chain: Interceptor.Chain,
        latch: CountDownLatch,
        requestTimestamp: Long,
        originalResponse: Response
    ): Response? {
        return if (latch.await(REQUEST_TIMEOUT, TimeUnit.SECONDS)
            && tokenUpdateTime > requestTimestamp
        ) {
            updateTokenAndProceedChain(chain, originalResponse)
        } else {
            null
        }
    }

    private fun handleTokenNeedRefresh(
        chain: Interceptor.Chain,
        originalResponse: Response
    ): Response? {
        return if (refreshToken()) {
            updateTokenAndProceedChain(chain, originalResponse)
        } else {
            null
        }
    }

    private fun updateTokenAndProceedChain(
        chain: Interceptor.Chain,
        originalResponse: Response
    ): Response {
        originalResponse.body?.closeQuietly()
        val newRequest = updateOriginalCallWithNewToken(chain.request())

        return chain.proceed(newRequest)
    }

    private fun refreshToken(): Boolean {
        initLatch()

        val tokenRefreshed = runBlocking {
            runCatching {
                authRepository.performRefreshTokenSuspend(authorizationService)
            }
                .getOrNull()
                ?.let { tokens ->
                    TokenStorage.accessToken = tokens.accessToken
                    TokenStorage.refreshToken = tokens.refreshToken
                    TokenStorage.idToken = tokens.idToken
                    true
                } ?: false
        }

        if (tokenRefreshed) {
            tokenUpdateTime = System.currentTimeMillis()
        }
        getLatch()?.countDown()
        return tokenRefreshed
    }

    private fun updateOriginalCallWithNewToken(request: Request): Request {
        return tokenStorage.accessToken?.let { newAccessToken ->
            request
                .newBuilder()
                .header("Authorization", newAccessToken)
                .build()
        } ?: request
    }

    companion object {

        private const val REQUEST_TIMEOUT = 30L

        @Volatile
        private var tokenUpdateTime: Long = 0L

        private var countDownLatch: CountDownLatch? = null

        @Synchronized
        fun initLatch() {
            countDownLatch = CountDownLatch(1)
        }

        @Synchronized
        fun getLatch() = countDownLatch
    }
}
