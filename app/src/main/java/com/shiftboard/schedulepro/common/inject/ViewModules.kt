package com.shiftboard.schedulepro.common.inject

import androidx.lifecycle.SavedStateHandle
import com.shiftboard.schedulepro.activities.root.RootActivityViewModel
import com.shiftboard.schedulepro.activities.root.SharedStateViewModel
import com.shiftboard.schedulepro.activities.router.RouterViewModel
import com.shiftboard.schedulepro.activities.splash.SplashActivity
import com.shiftboard.schedulepro.activities.splash.SplashActivityViewModel
import com.shiftboard.schedulepro.content.details.leave.LeaveDetailViewModel
import com.shiftboard.schedulepro.content.details.projected.leave.ProjectedLeaveDetailViewModel
import com.shiftboard.schedulepro.content.details.projected.shift.ProjectedShiftDetailViewModel
import com.shiftboard.schedulepro.content.details.request.RequestDetailViewModel
import com.shiftboard.schedulepro.content.group.GroupViewModel
import com.shiftboard.schedulepro.content.leaves.request.LeaveRequestViewModel
import com.shiftboard.schedulepro.content.notifications.ui.NotificationViewModel
import com.shiftboard.schedulepro.content.openShifts.OpenShiftDialogViewModel
import com.shiftboard.schedulepro.content.overtime.SignupViewModel
import com.shiftboard.schedulepro.content.profile.ProfileViewModel
import com.shiftboard.schedulepro.content.profile.repo.ResourceProvider
import com.shiftboard.schedulepro.content.profile.unavailability.viewmodels.AddUnavailabilityScreenViewModel
import com.shiftboard.schedulepro.content.profile.unavailability.viewmodels.SharedViewModel
import com.shiftboard.schedulepro.content.profile.unavailability.viewmodels.UnavailabilityScreenViewModel
import com.shiftboard.schedulepro.content.schedule.ui.ScheduleViewModel
import com.shiftboard.schedulepro.content.trade.TradeViewModel
import corm.shiftboard.schedulepro.content.details.shift.ShiftDetailViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object ViewModules {
    val module = module {
        viewModel { ShiftDetailViewModel(shiftRepo = get(), userCacheRepo = get()) }
        viewModel { TradeViewModel(tradesRepo = get(), groupsRepo = get(), profileRepo = get()) }
        viewModel { GroupViewModel(groupsRepo = get(), userCacheRepo = get()) }
        viewModel { LeaveDetailViewModel(detailsRepo = get()) }
        viewModel { RequestDetailViewModel(detailsRepo = get()) }
        viewModel { ProjectedShiftDetailViewModel(detailsRepo = get()) }
        viewModel { ProjectedLeaveDetailViewModel(detailsRepo = get()) }
        viewModel { UnavailabilityScreenViewModel(unavailabilityRepo = get()) }
        viewModel { AddUnavailabilityScreenViewModel(unavailabilityRepo = get(), resourceProvider = ResourceProvider(androidContext())) }
        viewModel { SharedViewModel() }
        viewModel { RootActivityViewModel(sharedRepo = get()) }
        viewModel { SharedStateViewModel(sharedRepo = get(), userCacheRepo = get()) }
        viewModel { NotificationViewModel(notificationRepo = get()) }
        viewModel { LeaveRequestViewModel(leaveRepo = get()) }
        viewModel { ScheduleViewModel(scheduleRepo = get()) }
        viewModel { ProfileViewModel(profileRepo = get()) }
        viewModel { RouterViewModel(sharedRepo = get()) }

        viewModel { SignupViewModel(signupRepo = get()) }
        viewModel { OpenShiftDialogViewModel(openShiftRepo = get()) }

        scope<SplashActivity> {
            viewModel { SplashActivityViewModel(db = get(), userRepo = get()) }
        }
    }
}