package com.shiftboard.schedulepro.content.schedule.ui

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.shiftboard.schedulepro.R
import com.shiftboard.schedulepro.common.prefs.AppPrefs
import com.shiftboard.schedulepro.core.common.analytics.BindingViewHolder
import com.shiftboard.schedulepro.core.common.analytics.inflate
import com.shiftboard.schedulepro.core.common.utils.isSameDay
import com.shiftboard.schedulepro.core.persistence.model.schedule.*
import com.shiftboard.schedulepro.databinding.*
import com.shiftboard.schedulepro.resources.PendingLeaveStatus
import com.shiftboard.schedulepro.resources.ScheduleListActions
import com.shiftboard.schedulepro.ui.ShiftListElementBackground
import com.shiftboard.schedulepro.ui.formatting.TimeFormatters
import com.shiftboard.schedulepro.ui.tryWith
import org.threeten.bp.LocalDate


class ScheduleListAdapter(private val clickListener: ElementClickListener) :
    ListAdapter<ScheduleItemElement, RecyclerView.ViewHolder>(object :
        DiffUtil.ItemCallback<ScheduleItemElement>() {
        override fun areItemsTheSame(
            oldItem: ScheduleItemElement,
            newItem: ScheduleItemElement,
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: ScheduleItemElement,
            newItem: ScheduleItemElement,
        ): Boolean = oldItem == newItem
    }) {

    private val expandedSet = hashSetOf<String>()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val element = getItem(position) ?: return

        when (holder) {
            is ShiftElementViewHolder -> {
                when (val e = element.scheduleItem) {
                    is ShiftElement -> holder.bind(e, element.startOfDay)
                    is ProjectedShiftElement -> holder.bindProjected(e, element.startOfDay)
                    else -> { /* pass */
                    }
                }
            }
            is LeaveElementViewHolder -> {
                when (val e = element.scheduleItem) {
                    is LeaveElement -> holder.bind(e, element.startOfDay)
                    is ProjectedLeaveElement -> holder.bindProjected(e, element.startOfDay)
                    else -> { /* pass */
                    }
                }
            }
            is PendingElementViewHolder -> (element.scheduleItem as? PendingLeaveElement)?.let {
                holder.bind(it, element.startOfDay)
            }
            is OffActionElementViewHolder -> (element.scheduleItem as? ActionsItem)?.let {
                holder.bind(it, element.startOfDay)
            }
            is SignupElementViewHolder -> (element.scheduleItem as? SignupWithEvents)?.let {
                holder.bind(it, it.signup.id !in expandedSet, element.startOfDay)
            }
            is HolidayElementViewHolder -> (element.scheduleItem as? HolidayItem)?.let {
                holder.bind(it, element.startOfDay)
            }
            is OpenShiftElementViewHolder -> (element.scheduleItem as? OpenShiftWithEvents)?.let {
                holder.bind(it, it.openShift.id !in expandedSet, element.startOfDay)
            }
            is TradeElementViewHolder -> (element.scheduleItem as? TradeEvent)?.let {
                holder.bind(it, element.startOfDay)
            }

        }
    }

    fun tryToFindDate(date: LocalDate): Int {
        currentList.forEachIndexed { index, scheduleItemElement ->
            if (scheduleItemElement.date.isSameDay(date)) {
                return index
            }
        }

        return RecyclerView.NO_POSITION
    }

    fun getDateForPosition(position: Int) = if (position >= 0) getItem(position)?.date else null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.schedule_element_shift -> ShiftElementViewHolder(parent).apply {
                viewBinding.contentCard.setOnClickListener { _ ->
                    absoluteAdapterPosition.takeIf { it >= 0 }?.let { getItem(it) }
                        ?.let {
                            when (val item = it.scheduleItem) {
                                is ShiftElement -> clickListener.onShiftElementClickLister(item)
                                is ProjectedShiftElement -> clickListener.onProjectedShiftElementClickLister(
                                    item)
                            }
                        }
                }
            }
            R.layout.schedule_element_leave -> LeaveElementViewHolder(parent).apply {
                viewBinding.contentCard.setOnClickListener { _ ->
                    absoluteAdapterPosition.takeIf { it >= 0 }?.let { getItem(it) }
                        ?.let {
                            when (val item = it.scheduleItem) {
                                is LeaveElement -> clickListener.onLeaveElementClickLister(item)
                                is ProjectedLeaveElement -> clickListener.onProjectedLeaveElementClickLister(
                                    item)
                            }
                        }
                }
            }
            R.layout.schedule_element_off -> OffActionElementViewHolder(parent).apply {
                viewBinding.leaveRequest.setOnClickListener { _ ->
                    absoluteAdapterPosition.takeIf { it >= 0 }?.let { getItem(it) }
                        ?.let { clickListener.onCreateLeaveClickLister(it.date) }
                }
                viewBinding.signupRequest.setOnClickListener { _ ->
                    absoluteAdapterPosition.takeIf { it >= 0 }?.let { getItem(it) }
                        ?.let { clickListener.onCreateSignupClickLister(it.date) }
                }
                viewBinding.bidRequest.setOnClickListener { _ ->
                    absoluteAdapterPosition.takeIf { it >= 0 }?.let { getItem(it) }
                        ?.let { clickListener.onCreateBidClickListener(it.date) }
                }
            }
            R.layout.schedule_element_leave_pending -> PendingElementViewHolder(parent).apply {
                viewBinding.contentCard.setOnClickListener { _ ->
                    absoluteAdapterPosition.takeIf { it >= 0 }?.let { getItem(it) }
                        ?.let { clickListener.onPendingElementClickLister(it.scheduleItem as PendingLeaveElement) }
                }
            }
            R.layout.schedule_element_holiday -> HolidayElementViewHolder(parent)

            R.layout.schedule_element_trade -> TradeElementViewHolder(parent).apply {
                viewBinding.contentCard.setOnClickListener { _ ->
                    absoluteAdapterPosition.takeIf { it >= 0 }?.let { getItem(it) }
                        ?.let {
                            when (val item = it.scheduleItem) {
                                is TradeEvent -> clickListener.onTradeElementClickListener(item)
                            }
                        }
                }
            }

            R.layout.schedule_element_overtime_collapsed -> SignupElementViewHolder(parent).apply {
                viewBinding.overlay.setOnClickListener {
                    absoluteAdapterPosition.takeIf { it >= 0 }
                        ?.let { getItem(it)?.scheduleItem as? SignupWithEvents }
                        ?.let {
                            if (it.signup.id in expandedSet) {
                                expandedSet.remove(it.signup.id)
                            } else {
                                expandedSet.add(it.signup.id)
                            }
                            notifyItemChanged(absoluteAdapterPosition)
                        }
                }
            }
            R.layout.schedule_element_open_shifts_collapsed -> OpenShiftElementViewHolder(parent).apply {
                viewBinding.overlay.setOnClickListener {
                    absoluteAdapterPosition.takeIf { it >= 0 }?.let { getItem(it).scheduleItem as? OpenShiftWithEvents }
                        ?.let {
                            if (it.openShift.id in expandedSet) {
                                expandedSet.remove(it.openShift.id)
                            } else {
                                expandedSet.add(it.openShift.id)
                            }
                            notifyItemChanged(absoluteAdapterPosition)
                        }
                }
            }

            else -> throw RuntimeException("invalid view-type $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)?.scheduleItem) {
            is ProjectedShiftElement,
            is ShiftElement,
            -> R.layout.schedule_element_shift
            is ProjectedLeaveElement,
            is LeaveElement,
            -> R.layout.schedule_element_leave
            is PendingLeaveElement -> R.layout.schedule_element_leave_pending
            is SignupWithEvents -> R.layout.schedule_element_overtime_collapsed
            is HolidayItem -> R.layout.schedule_element_holiday
            is OpenShiftWithEvents -> R.layout.schedule_element_open_shifts_collapsed
            is TradeEvent -> R.layout.schedule_element_trade
            else -> R.layout.schedule_element_off
        }
    }

    inner class SignupElementViewHolder(parent: ViewGroup) :
        BindingViewHolder<ScheduleElementOvertimeCollapsedBinding>(
            ScheduleElementOvertimeCollapsedBinding.bind(parent.inflate(R.layout.schedule_element_overtime_collapsed))
        ) {
        fun bind(element: SignupWithEvents, collapsed: Boolean, showDate: Boolean) {
            tryWith(viewBinding) {
                fun populateEvents() {
                    element.events.sortedBy { it.startTime }.forEach {
                        val binding =
                            ScheduleElementOvertimeExpandedBinding.inflate(LayoutInflater.from(
                                itemView.context), collapseContainer, true)
                        binding.colorBar.setBackgroundColor(it.color)
                        binding.code.text = it.shiftTimeCode
                        binding.time.text = TimeFormatters.spannedRange(binding.time.context,
                            it.startTime,
                            it.endTime, AppPrefs.militaryTime)

                        binding.contentCard.setOnClickListener {
                            clickListener.onCreateSignupClickLister(element.date)
                        }
                    }
                }

                dayTag.setDate(element.date)
                dayTag.visibility = if (showDate) View.VISIBLE else View.INVISIBLE

                collapseContainer.removeAllViews()
                if (element.events.size <= 3) {
                    contentCard.isVisible = false
                    spacing.isVisible = false
                    populateEvents()

                    collapseContainer.isVisible = true
                } else {
                    contentCard.isVisible = true
                    count.text = element.events.size.toString()
                    if (collapsed) {
                        spacing.isVisible = false
                        colorBar.setColorTags(element.events.map { it.color })
                        indicator.rotation = 0f
                        colorBar.visibility = View.VISIBLE
                        collapseContainer.isVisible = false
                    } else {
                        spacing.isVisible = true
                        indicator.rotation = 180f
                        colorBar.visibility = View.INVISIBLE
                        populateEvents()

                        collapseContainer.isVisible = true
                    }
                }

            }
        }
    }

    inner class OpenShiftElementViewHolder(parent: ViewGroup) :
        BindingViewHolder<ScheduleElementOpenShiftsCollapsedBinding>(
            ScheduleElementOpenShiftsCollapsedBinding.bind(parent.inflate(R.layout.schedule_element_open_shifts_collapsed))
        ) {
        fun bind(element: OpenShiftWithEvents, collapsed: Boolean, showDate: Boolean) {
            tryWith(viewBinding) {
                fun populateEvents() {
                    element.events.sortedBy { it.startTime }.forEach { openShift ->
                        val binding =
                            ScheduleElementOpenShiftBinding.inflate(LayoutInflater.from(
                                itemView.context), collapseContainer, true)
                        tryWith(binding) {
                            colorBar.visibility = View.VISIBLE
                            val radius = 10 //radius will be 5px

                            val gradientDrawable = GradientDrawable()
                            gradientDrawable.setColor(Color.parseColor("#f0efef"))
                            gradientDrawable.cornerRadius = radius.toFloat()
                            contentCard.setBackground(gradientDrawable)
                            colorBar.setBackgroundColor(openShift.color)
                            dayTag.setDate(openShift.date)
                            dayTag.visibility = if (showDate) View.VISIBLE else View.INVISIBLE

                            time.text = TimeFormatters.spannedShortRange(
                                openShift.startTime, false,
                                openShift.endTime, false, AppPrefs.militaryTime
                            )

                            shift.text = openShift.shiftTimeDescription

                            bidStatus.text = openShift.status
                            position.text = openShift.positionDescription
                            location.text = openShift.locationDescription
                            overtimeIcon.isVisible = true

                        }
                        binding.contentCard.setOnClickListener {
                            clickListener.onOpenShiftElementClickListener(openShift)
                        }
                    }
                }

                dayTag.setDate(element.date)
                dayTag.visibility = if (showDate) View.VISIBLE else View.INVISIBLE

                collapseContainer.removeAllViews()
                if (element.events.size <= 3) {
                    contentCard.isVisible = false
                    spacing.isVisible = false
                    populateEvents()

                    collapseContainer.isVisible = true
                } else {
                    contentCard.isVisible = true
                    count.text = element.events.size.toString()
                    if (collapsed) {
                        spacing.isVisible = false
                        colorBar.setColorTags(element.events.map { it.color })
                        indicator.rotation = 0f
                        colorBar.visibility = View.VISIBLE
                        collapseContainer.isVisible = false
                    } else {
                        spacing.isVisible = true
                        indicator.rotation = 180f
                        colorBar.visibility = View.INVISIBLE
                        populateEvents()

                        collapseContainer.isVisible = true
                    }
                }

            }
        }
    }
    interface ElementClickListener {
        fun onShiftElementClickLister(element: ShiftElement)
        fun onProjectedShiftElementClickLister(element: ProjectedShiftElement)
        fun onProjectedLeaveElementClickLister(element: ProjectedLeaveElement)
        fun onLeaveElementClickLister(element: LeaveElement)
        fun onPendingElementClickLister(element: PendingLeaveElement)
        fun onOpenShiftElementClickListener(element: OpenShiftEvent)
        fun onTradeElementClickListener(element: TradeEvent)

        fun onCreateLeaveClickLister(element: LocalDate)
        fun onCreateSignupClickLister(element: LocalDate)
        fun onCreateBidClickListener(element: LocalDate)
    }
}


class ShiftElementViewHolder(parent: ViewGroup) : BindingViewHolder<ScheduleElementShiftBinding>(
    ScheduleElementShiftBinding.bind(parent.inflate(R.layout.schedule_element_shift))
) {
    private fun bindBase(element: BaseShift, showDate: Boolean) {
        tryWith(viewBinding) {
            dayTag.setDate(element.date)
            dayTag.visibility = if (showDate) View.VISIBLE else View.INVISIBLE

            time.text = TimeFormatters.spannedShortRange(
                element.startTime, element.startDiff,
                element.endTime, element.endDiff, AppPrefs.militaryTime
            )

            shift.text = element.shiftTimeDescription

            position.text = element.positionDescription
            location.text = element.locationDescription
            overtimeIcon.isVisible = element.regularOvertimeHours > 0f
        }
    }

    private fun resetConstraints() {
        tryWith(viewBinding) {
            val constraints = ConstraintSet()
            constraints.clone(constraintLayout)
            constraints.applyTo(constraintLayout)
        }
    }

    fun bind(element: ShiftElement, showDate: Boolean) {
        tryWith(viewBinding) {
            bindBase(element, showDate)

            if (element.comments.isNotBlank()) {
                comment.text = element.comments
                comment.isVisible = true
                commentIcon.isVisible = true
            } else {
                comment.isVisible = false
                commentIcon.isVisible = false
            }


            contentCard.background = ShiftListElementBackground(element.color, false)

            resetConstraints()
        }
    }

    fun bindProjected(element: ProjectedShiftElement, showDate: Boolean) {
        tryWith(viewBinding) {
            bindBase(element, showDate)

            comment.isVisible = false
            commentIcon.isVisible = false

            contentCard.background = ShiftListElementBackground(element.color, true)

            resetConstraints()
        }
    }
}

class LeaveElementViewHolder(parent: ViewGroup) : BindingViewHolder<ScheduleElementLeaveBinding>(
    ScheduleElementLeaveBinding.bind(parent.inflate(R.layout.schedule_element_leave))
) {
    fun bind(element: LeaveElement, showDate: Boolean) {
        tryWith(viewBinding) {
            dayTag.setDate(element.date)
            dayTag.visibility = if (showDate) View.VISIBLE else View.INVISIBLE

            colorBar.setBackgroundColor(element.color)
            time.text =
                TimeFormatters.spannedRange(time.context, element.startTime, element.endTime, AppPrefs.militaryTime)
            type.text = element.leaveTypeDescription

            if (element.requestStatus != null) {
                status.text = itemView.context.statusString(element.requestStatus)
                status.isVisible = true
            } else {
                status.isVisible = false
            }

            commentIcon.isVisible = false
            comment.isVisible = false

            val constraints = ConstraintSet()
            constraints.clone(constraintLayout)
            constraints.applyTo(constraintLayout)
        }
    }

    fun bindProjected(element: ProjectedLeaveElement, showDate: Boolean) {
        tryWith(viewBinding) {
            dayTag.setDate(element.date)
            dayTag.visibility = if (showDate) View.VISIBLE else View.INVISIBLE

            colorBar.background = null
            contentCard.background =
                ShiftListElementBackground(element.color,
                    projected = true, leave = true)

            time.text =
                TimeFormatters.spannedRange(time.context, element.startTime, element.endTime, AppPrefs.militaryTime)
            type.text = element.leaveTypeDescription

            status.isVisible = false

            commentIcon.isVisible = false
            comment.isVisible = false

            val constraints = ConstraintSet()
            constraints.clone(constraintLayout)
            constraints.applyTo(constraintLayout)
        }
    }
}

private fun Context.statusString(status: String?): String {
    return when (status) {
        PendingLeaveStatus.PENDING -> getString(R.string.leave_request_status_pending)
        PendingLeaveStatus.ACCEPTED -> getString(R.string.leave_request_status_accepted)
        PendingLeaveStatus.PENDING_EDIT -> getString(R.string.leave_request_status_accepted_pending_edit)
        PendingLeaveStatus.ACCEPTED_CR -> getString(R.string.leave_request_status_accepted_cancellation_requested)
        PendingLeaveStatus.DECLINED -> getString(R.string.leave_request_status_declined)
        PendingLeaveStatus.REVOKED -> getString(R.string.leave_request_status_revoked)
        PendingLeaveStatus.CANCELLED -> getString(R.string.leave_request_status_cancelled)
        else -> ""
    }
}

class PendingElementViewHolder(parent: ViewGroup) :
    BindingViewHolder<ScheduleElementLeavePendingBinding>(
        ScheduleElementLeavePendingBinding.bind(parent.inflate(R.layout.schedule_element_leave_pending))
    ) {
    fun bind(element: PendingLeaveElement, showDate: Boolean) {
        tryWith(viewBinding) {
            dayTag.setDate(element.date)
            dayTag.visibility = if (showDate) View.VISIBLE else View.INVISIBLE

            status.text = itemView.context.statusString(element.status)

            colorBar.setBackgroundColor(element.color)

            time.text =
                TimeFormatters.spannedRange(time.context, element.startTime, element.endTime, AppPrefs.militaryTime)
            type.text = element.leaveTypeDescription

            val textColor = when {
                PendingLeaveStatus.isAccepted(element.status) -> {
                    contentCard.setCardBackgroundColor(ContextCompat.getColor(itemView.context,
                        R.color.approved_leave))
                    ContextCompat.getColor(itemView.context, R.color.white)
                }
                else -> {
                    contentCard.setCardBackgroundColor(ContextCompat.getColor(itemView.context,
                        R.color.very_light_gray))
                    ContextCompat.getColor(itemView.context, R.color.text_dark)
                }
            }

            status.setTextColor(textColor)
            time.setTextColor(textColor)
            type.setTextColor(textColor)
            calendarIcon.imageTintList = ColorStateList.valueOf(textColor)

            commentIcon.isVisible = false
            comment.isVisible = false
        }
    }
}

class HolidayElementViewHolder(parent: ViewGroup) :
    BindingViewHolder<ScheduleElementHolidayBinding>(
        ScheduleElementHolidayBinding.bind(parent.inflate(R.layout.schedule_element_holiday))
    ) {
    fun bind(element: HolidayItem, showDate: Boolean) {
        tryWith(viewBinding) {
            dayTag.setDate(element.date)
            dayTag.visibility = if (showDate) View.VISIBLE else View.INVISIBLE

            description.text = element.holiday
        }
    }
}

class TradeElementViewHolder(parent: ViewGroup) :
    BindingViewHolder<ScheduleElementTradeBinding>(
        ScheduleElementTradeBinding.bind(parent.inflate(R.layout.schedule_element_trade))
    ) {
    fun bind(element: TradeEvent, showDate: Boolean) {
        tryWith(viewBinding) {
            element.date?.let { dayTag.setDate(it) }
            dayTag.visibility = if (showDate) View.VISIBLE else View.INVISIBLE

            description.text = element.originatorName
        }
    }
}
class OffActionElementViewHolder(parent: ViewGroup) : BindingViewHolder<ScheduleElementOffBinding>(
    ScheduleElementOffBinding.bind(parent.inflate(R.layout.schedule_element_off))
) {
    fun bind(element: ActionsItem, showDate: Boolean) {
        tryWith(viewBinding) {
            dayTag.setDate(element.date)
            dayTag.visibility = if (showDate) View.VISIBLE else View.INVISIBLE

            leaveRequest.isVisible = ScheduleListActions.SUBMIT_LEAVE in element.actions
            signupRequest.isVisible = ScheduleListActions.SUBMIT_SIGN_UP in element.actions
            bidRequest.isVisible = ScheduleListActions.SUBMIT_BID in element.actions

            description.text = element.description
        }
    }
}
