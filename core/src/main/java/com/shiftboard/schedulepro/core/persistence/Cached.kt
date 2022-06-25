package com.shiftboard.schedulepro.core.persistence

import org.threeten.bp.OffsetDateTime

interface Cached {
    val cachedAt: OffsetDateTime
}