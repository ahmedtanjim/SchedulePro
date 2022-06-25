package com.shiftboard.schedulepro.core.persistence

import org.threeten.bp.OffsetDateTime


object Cache {
    const val LONG_CHECK = "datetime(cachedAt) > datetime('now', 'now', '-1 hours')"
    const val SHORT_CHECK = "datetime(cachedAt) > datetime('now', 'now', '-1 hours')"

    const val LONG_CACHE = 3600L // 1 hour in seconds
    const val SHORT_CACHE = 600L // 10 min in seconds

    val longTimestamp: OffsetDateTime get() = OffsetDateTime.now().minusSeconds(LONG_CACHE)
    val shortTimestamp: OffsetDateTime get() = OffsetDateTime.now().minusSeconds(SHORT_CACHE)
}