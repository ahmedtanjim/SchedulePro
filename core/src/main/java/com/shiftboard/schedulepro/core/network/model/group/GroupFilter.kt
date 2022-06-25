package com.shiftboard.schedulepro.core.network.model.group

import com.shiftboard.schedulepro.core.network.model.Employee
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ShiftLeaveCode (
    var id: String? = null,
    var detailedDescription: String? = null,
    var code: String? = null,
    var color: String? = null
)
@JsonClass(generateAdapter = true)
data class GroupFilter (
    var id: String? = null,
    var detailedDescription: String? = null,
    var code: String? = null,
    var color: String? = null
)

@JsonClass(generateAdapter = true)
data class GroupFiltersResponse(
    var positions: MutableList<GroupFilter> = mutableListOf(),
    var locations:MutableList<GroupFilter> = mutableListOf(),
    var shiftTimes: MutableList<GroupFilter> =mutableListOf(),
    var leaveTypes: MutableList<GroupFilter> =mutableListOf(),
    var teams: MutableList<GroupFilter> = mutableListOf(),
    var employees: MutableList<Employee> = mutableListOf()
)
