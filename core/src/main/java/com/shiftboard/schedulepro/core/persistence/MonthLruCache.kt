package com.shiftboard.schedulepro.core.persistence

import androidx.collection.LruCache
import com.shiftboard.schedulepro.core.network.model.schedule.SummaryIconModel
import org.threeten.bp.LocalDate


class MonthLruCache(maxSize: Int): LruCache<LruKey, HashMap<LocalDate, List<SummaryIconModel>>>(maxSize)
data class LruKey(val start: LocalDate, val end: LocalDate, val filterHash: Int)