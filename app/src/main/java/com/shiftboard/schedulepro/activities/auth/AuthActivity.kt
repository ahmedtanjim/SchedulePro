package com.shiftboard.schedulepro.activities.auth

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.shiftboard.schedulepro.FlavorSettings
import net.openid.appauth.*
import com.shiftboard.schedulepro.R
import com.shiftboard.schedulepro.core.EnvironmentConfig
import com.shiftboard.schedulepro.common.prefs.CredentialPrefs
import com.shiftboard.schedulepro.common.prefs.update
import com.shiftboard.schedulepro.activities.splash.SplashActivity
import com.shiftboard.schedulepro.databinding.AuthActivityBinding
import org.json.JSONException
import org.koin.android.ext.android.inject
import timber.log.Timber


class AuthActivity : AppCompatActivity() {
    private val authService by lazy { AuthorizationService(this) }
    private val environment by inject<EnvironmentConfig>()
    private lateinit var boundView: AuthActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        boundView = AuthActivityBinding.inflate(layoutInflater)
        setContentView(boundView.root)

        getAuthStateFromIntent(intent)?.let {
            val response = AuthorizationResponse.fromIntent(intent)
            val ex = AuthorizationException.fromIntent(intent)
            CredentialPrefs.update(response, ex)

            showTokenState()
            response?.let { exchangeAuthorizationCode(it) }
        } ?: run { showStartState() }

        with(boundView) {
            login.setOnClickListener {
                if (!(emailEdit.text?.toString()).isNullOrBlank()) {
                    showTokenState()
                    makeAuthRequest(
                        emailEdit.text?.toString() ?: "",
                        AuthorizationServiceConfiguration(
                            Uri.parse(environment.authEndpoint),
                            Uri.parse(environment.tokenEndpoint)
                        )
                    )
                }
            }

            if (FlavorSettings.canUseDebugMode()) {
                if (FlavorSettings.debugUsers().isNotEmpty()) {
                    logo.setOnClickListener {
                        MaterialDialog(this@AuthActivity).show {
                            listItemsSingleChoice(items = FlavorSettings.debugUsers()) { _, index, _ ->
                                makeAuthRequest(
                                    FlavorSettings.debugUsers()[index],
                                    AuthorizationServiceConfiguration(
                                        Uri.parse(environment.authEndpoint),
                                        Uri.parse(environment.tokenEndpoint)
                                    )
                                )
                            }
                            positiveButton(R.string.ok)
                        }
                    }
                }
            }
        }
    }

    private fun showTokenState() {
        boundView.login.isEnabled = false
    }

    private fun showStartState() {
        boundView.login.isEnabled = true
    }

    private fun showLoginState() {
        boundView.login.isEnabled = true
    }

    private fun makeAuthRequest(
        email: String,
        serviceConfig: AuthorizationServiceConfiguration,
    ) {
        CredentialPrefs.replace(AuthState(serviceConfig))

        val authRequest = AuthorizationRequest.Builder(serviceConfig,
            environment.clientId, ResponseTypeValues.CODE, Uri.parse(environment.redirectUrl))
            .setLoginHint(email)
            .setScopes(environment.scopes)
            .setPrompt("login")
            .build()

        val intent = authService.getAuthorizationRequestIntent(authRequest,
            authService.createCustomTabsIntentBuilder()
                .setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .build())

        startActivityForResult(intent, AUTH_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTH_REQUEST) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    if (data == null) {
                        showAuthError(null)
                        return
                    }
                    val authResult = AuthorizationResponse.fromIntent(data)
                    val authException = AuthorizationException.fromIntent(data)
                    CredentialPrefs.update(authResult, authException)

                    if (authResult != null) {
                        exchangeAuthorizationCode(authResult)
                    } else {
                        showAuthError(authException?.errorDescription)
                    }
                }
                else -> {
                    if (data == null) {
                        showAuthError(null)
                    } else {
                        val authException = AuthorizationException.fromIntent(data)
                        showAuthError(authException?.errorDescription)
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun showAuthError(error: String?) {
        showLoginState()
    }

    override fun onDestroy() {
        super.onDestroy()
        authService.dispose()
    }

    private fun exchangeAuthorizationCode(authorizationResponse: AuthorizationResponse) {
        performTokenRequest(authorizationResponse.createTokenExchangeRequest())
    }

    private fun performTokenRequest(request: TokenRequest) {
        val clientAuthentication: ClientAuthentication = try {
            CredentialPrefs.getCurrent().clientAuthentication
        } catch (ex: ClientAuthentication.UnsupportedAuthenticationMethod) {
            return
        }
        authService.performTokenRequest(
            request,
            clientAuthentication) { tokenResponse, ex ->
            receivedTokenResponse(tokenResponse, ex)
        }
    }

    private fun receivedTokenResponse(
        tokenResponse: TokenResponse?,
        authException: AuthorizationException?,
    ) {
        CredentialPrefs.update(tokenResponse, authException)

        startActivity(Intent(this, SplashActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NO_HISTORY
            if (intent.extras != null) {
                putExtras(intent)
            }
        })
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    companion object {
        private const val KEY_AUTH_STATE = "authState"
        private const val AUTH_REQUEST = 2001

        private fun getAuthStateFromIntent(intent: Intent): AuthState? {
            if (intent.hasExtra(KEY_AUTH_STATE)) {
                return try {
                    intent.getStringExtra(KEY_AUTH_STATE)?.let { AuthState.jsonDeserialize(it) }
                } catch (ex: JSONException) {
                    Timber.e(ex, "Malformed AuthState JSON saved")
                    throw IllegalArgumentException("The AuthState instance is missing in the intent.")
                }
            }
            return null
        }
    }
}