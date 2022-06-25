package com.shiftboard.schedulepro.content.profile.repo

import com.shiftboard.schedulepro.core.network.common.Maybe
import com.shiftboard.schedulepro.core.network.model.profile.Unavailability

interface UnavailabilityRepository {
    suspend fun getUnavailabilityRepository(): Maybe<Unavailability>
}