package com.shiftboard.schedulepro.common.prefs

import com.shiftboard.schedulepro.core.common.utils.EncryptedPrefsContainer
import com.shiftboard.schedulepro.core.common.utils.PreferenceTransformer
import net.openid.appauth.*
import java.util.*

object CredentialPrefs: EncryptedPrefsContainer("credential_prefs") {
    var userId by SharedPref("")
    var firstName by SharedPref("")
    var lastName by SharedPref("")
    var databaseToken by SharedPref(UUID.randomUUID().toString())

    var keyState by SharedPref(
        AuthState(),
        transformer = object : PreferenceTransformer<AuthState>() {
            override fun encode(value: AuthState?): String {
                return value?.jsonSerializeString() ?: ""
            }

            override fun decode(value: String?): AuthState {
                return value.takeUnless { it.isNullOrBlank() }
                    ?.let { AuthState.jsonDeserialize(it) } ?: AuthState()
            }
        })

    fun getCurrent(): AuthState {
        return keyState
    }

    fun replace(state: AuthState): AuthState {
        keyState = state
        return keyState
    }
}

fun CredentialPrefs.update(response: AuthorizationResponse?, ex: AuthorizationException?) {
    val state = getCurrent()
    state.update(response, ex)
    replace(state)
}

fun CredentialPrefs.update(response: TokenResponse?, ex: AuthorizationException?) {
    val state = getCurrent()
    state.update(response, ex)
    replace(state)
}