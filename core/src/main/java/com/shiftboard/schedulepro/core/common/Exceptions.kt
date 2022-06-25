package com.shiftboard.schedulepro.core.common

import java.io.IOException

class InvalidStateException(override val message: String): Exception()
class EmptyBodyException: IOException("Data was missing from result")
class NetworkError(override val message: String?): IOException()
class ServerError(val status: Int, val title: String, override val message: String): IOException()


// User token is expired and cannot be refreshed
class AuthException: IOException("not_authenticated")
class MobileNotEnabled: IOException()