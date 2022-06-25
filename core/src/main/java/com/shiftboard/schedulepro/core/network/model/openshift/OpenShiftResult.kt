package com.shiftboard.schedulepro.core.network.model.openshift

import com.shiftboard.schedulepro.core.persistence.model.schedule.OpenShiftDetailsElement
import org.threeten.bp.LocalDate


sealed class OpenShiftResult {
    data class Success(val date: LocalDate): OpenShiftResult()
    data class Failure(val throwable: Throwable?): OpenShiftResult()
}

