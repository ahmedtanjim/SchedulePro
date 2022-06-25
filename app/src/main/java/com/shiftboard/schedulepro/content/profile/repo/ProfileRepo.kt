package com.shiftboard.schedulepro.content.profile.repo

import com.shiftboard.schedulepro.core.common.base.BaseNetworkCall
import com.shiftboard.schedulepro.core.common.base.BaseRepository
import com.shiftboard.schedulepro.core.persistence.model.UserPrefs
import com.shiftboard.schedulepro.core.persistence.preference.UserPreferences
import kotlinx.coroutines.flow.Flow

class ProfileRepo(
    private val userRepo: UserPreferences,

    networkCallImpl: BaseNetworkCall
) : BaseRepository(networkCallImpl) {

    fun userDetails(): Flow<UserPrefs> = userRepo.observe()
}