@file:Suppress("unused")
package com.shiftboard.schedulepro.core

private val BASE_SCOPES = listOf(
    "offline_access",
    "openid"
)

interface EnvironmentConfig {
    val baseUrl: String
    val basePath: String
    val feedbackEmail: String
    val clientId: String
    val apiKey: String
    val redirectUrl: String
    val scopes: List<String>
    val authEndpoint: String
    val tokenEndpoint: String
}

enum class Environment(
    override val basePath: String,
    override val clientId: String,
    override val apiKey: String,
    override val authEndpoint: String,
    override val tokenEndpoint: String,

    private val additionalScopes: List<String>,

    override val baseUrl: String = "api.scheduleproweb.com",
    override val feedbackEmail: String = "androidfeedback@shiftboard.com",
    override val redirectUrl: String = "msauth://com.shiftboard.schedulepro/oauth/redirect",
) : EnvironmentConfig {

    DEV(
        basePath = "/exp",
        clientId = BuildConfig.DEV_ENV_CLIENT_ID,
        apiKey = BuildConfig.DEV_ENV_API_KEY,
        authEndpoint = "https://spropreviewb2c.b2clogin.com/spropreviewb2c.onmicrosoft.com/oauth2/v2.0/authorize?p=b2c_1a_signin",
        tokenEndpoint = "https://spropreviewb2c.b2clogin.com/spropreviewb2c.onmicrosoft.com/oauth2/v2.0/token?p=b2c_1a_signin",
        additionalScopes = listOf(
            "https://spropreviewb2c.onmicrosoft.com/api/spro.read",
            "https://spropreviewb2c.onmicrosoft.com/api/spro.write",
        ),
    ),

    QA(
        basePath = "/testsapi",
        clientId = BuildConfig.QA_ENV_CLIENT_ID,
        apiKey = BuildConfig.QA_ENV_API_KEY,
        authEndpoint = "https://sprotestb2c.b2clogin.com/sprotestb2c.onmicrosoft.com/oauth2/v2.0/authorize?p=b2c_1a_signin",
        tokenEndpoint = "https://sprotestb2c.b2clogin.com/sprotestb2c.onmicrosoft.com/oauth2/v2.0/token?p=b2c_1a_signin",
        additionalScopes = listOf(
            "https://sprotestb2c.onmicrosoft.com/api/spro.read",
            "https://sprotestb2c.onmicrosoft.com/api/spro.write",
        ),
    ),

    PRODUCTION(
        basePath = "/sapi",
        clientId = BuildConfig.PROD_ENV_CLIENT_ID,
        apiKey = BuildConfig.PROD_ENV_API_KEY,
        authEndpoint = "https://sprob2c.b2clogin.com/sprob2c.onmicrosoft.com/oauth2/v2.0/authorize?p=b2c_1a_signin",
        tokenEndpoint = "https://sprob2c.b2clogin.com/sprob2c.onmicrosoft.com/oauth2/v2.0/token?p=b2c_1a_signin",
        additionalScopes = listOf(
            "https://sprob2c.onmicrosoft.com/api/spro.read",
            "https://sprob2c.onmicrosoft.com/api/spro.write"
        ),
    );

    override val scopes: List<String> = additionalScopes + BASE_SCOPES
}
