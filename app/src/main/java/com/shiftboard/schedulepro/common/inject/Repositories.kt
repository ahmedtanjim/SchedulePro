package com.shiftboard.schedulepro.common.inject

import com.shiftboard.schedulepro.activities.auth.repo.AuthRepo
import com.shiftboard.schedulepro.activities.root.repo.SharedRepo
import com.shiftboard.schedulepro.activities.splash.repo.UserCacheRepo
import com.shiftboard.schedulepro.content.details.repo.DetailsRepo
import com.shiftboard.schedulepro.content.group.domain.GroupsRepo
import com.shiftboard.schedulepro.content.leaves.repo.LeaveRepo
import com.shiftboard.schedulepro.content.notifications.repo.NotificationRepo
import com.shiftboard.schedulepro.content.openShifts.OpenShiftRepo
import com.shiftboard.schedulepro.content.overtime.SignupRepo
import com.shiftboard.schedulepro.content.profile.repo.ProfileRepo
import com.shiftboard.schedulepro.content.profile.repo.UnavailabilityRepo
import com.shiftboard.schedulepro.content.schedule.repo.ScheduleRepo
import com.shiftboard.schedulepro.content.trade.domain.repository.TradesRepo
import com.shiftboard.schedulepro.core.common.base.BaseNetworkCall
import com.shiftboard.schedulepro.core.common.base.TokenRefreshBaseNetworkCall
import org.koin.dsl.module

object Repositories {
    val module = module {
        single<BaseNetworkCall> { TokenRefreshBaseNetworkCall(get(), get()) }

        single { LeaveRepo(api = get(), networkCallImpl = get()) }
        single { SignupRepo(api = get(), networkCallImpl = get()) }
        single { AuthRepo(api = get(), networkCallImpl = get()) }
        single { DetailsRepo(api = get(), db = get(), permissionRepo = get(), networkCallImpl = get()) }
        single { UnavailabilityRepo(api = get(), networkCallImpl = get()) }
        single { ScheduleRepo(api = get(), db = get(), permissionRepo = get(), lruCache = get(), networkCallImpl = get()) }
        single { SharedRepo(api = get(), userRepo = get(), networkCallImpl = get()) }
        single { UserCacheRepo(api = get(), authRepo = get(), userRepo = get(), permissionsRepo = get(),
            analytics = get(), errorLogger = get(), networkCallImpl = get()) }
        single { NotificationRepo(api = get(), db = get(), networkCallImpl = get()) }

        single { ProfileRepo(userRepo = get(), networkCallImpl = get()) }

        single { OpenShiftRepo(api = get(), networkCallImpl = get()) }

        single { TradesRepo(api = get(), networkCallImpl = get(), userRepo = get())}

        single { GroupsRepo(api = get(), networkCallImpl = get()) }
    }
}