package com.shiftboard.schedulepro.core.persistence

import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZonedDateTime
import java.io.IOException

interface Mappable<T> {
    fun map(cachedAt: OffsetDateTime = OffsetDateTime.now()): T
}

class MappingException: IOException("Unable to map object")