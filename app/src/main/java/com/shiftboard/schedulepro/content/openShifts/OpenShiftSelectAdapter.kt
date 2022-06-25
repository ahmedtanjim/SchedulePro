package com.shiftboard.schedulepro.content.openShifts

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.shiftboard.schedulepro.R
import com.shiftboard.schedulepro.common.prefs.AppPrefs
import com.shiftboard.schedulepro.core.network.model.openshift.OpenShiftDetails
import com.shiftboard.schedulepro.ui.formatting.TimeFormatters
import com.shiftboard.schedulepro.ui.schedule.DayTagView

class OpenShiftSelectAdapter(val selectionInterface: SelectionInterface) : RecyclerView.Adapter<OpenShiftSelectAdapter.OpenShiftSelectViewHolder>() {

    private var shifts: List<OpenShiftDetails>? = null
    private var selectedIndex: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OpenShiftSelectViewHolder {
        return when (viewType) {
            R.layout.schedule_element_open_shift -> ElementViewHolder(parent).apply {
                itemView.setOnClickListener {
                    val shiftElement = shifts?.get(bindingAdapterPosition)
                    if (bindingAdapterPosition == selectedIndex || shiftElement?.actions?.size ?: 0 == 0) return@setOnClickListener

                    if (selectedIndex != -1) shifts?.get(selectedIndex)?.selected = false

                    selectedIndex = bindingAdapterPosition
                    if (shifts!![selectedIndex].actions.contains("Cancel")) {
                        shifts!![selectedIndex].shiftRequestId?.let { it1 ->
                            selectionInterface.onSelection(it1, true )
                        }
                    } else {
                        selectionInterface.onSelection(shifts!![selectedIndex].id, false )
                    }
                    shifts!![selectedIndex].selected = true
                    notifyDataSetChanged()
                }
            }
            else -> throw IllegalStateException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: OpenShiftSelectViewHolder, position: Int) {
        when (holder) {
            is ElementViewHolder -> {
                shifts?.let { holder.bind(it.get(position), position ) }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.schedule_element_open_shift
    }
    // number of options plus the header
    override fun getItemCount(): Int = shifts?.count() ?: 0

    fun setData(shifts: List<OpenShiftDetails>) {
        this.shifts = shifts
        notifyDataSetChanged()
    }

    abstract class OpenShiftSelectViewHolder(parent: ViewGroup, @LayoutRes res: Int):
        RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(res, parent, false))

    inner class ElementViewHolder(parent: ViewGroup): OpenShiftSelectViewHolder(parent, R.layout.open_shift_element_content_shift) {
        private val dayTag: DayTagView by lazy { itemView.findViewById(R.id.day_tag) }
        private val constraintLayout: ConstraintLayout by lazy { itemView.findViewById(R.id.constraint_layout) }
        private val colorBar: View by lazy { itemView.findViewById(R.id.color_bar) }
        private val shiftCode: TextView by lazy { itemView.findViewById(R.id.shift_time_code) }
        private val shiftTime: TextView by lazy { itemView.findViewById(R.id.shift_time) }
        private val shiftLocation: TextView by lazy {itemView.findViewById(R.id.shift_location)}
        private val shiftRole: TextView by lazy {itemView.findViewById(R.id.shift_role)}
        private val shiftBidders: TextView by lazy {itemView.findViewById(R.id.shift_bidders)}
        private val shiftStatus: TextView by lazy {itemView.findViewById(R.id.shift_status)}
        private val shiftHours: TextView by lazy {itemView.findViewById(R.id.shift_hours)}
        private val actionButton: TextView by lazy { itemView.findViewById(R.id.toggle_view) }
        fun bind(data: OpenShiftDetails, position: Int) {
            with(data.shift) {
                dayTag.setDate(date)
                colorBar.setBackgroundColor(color)
                shiftCode.text = shiftTimeDescription

                shiftTime.text = TimeFormatters.spannedShortRange(
                    startTime,
                    false,
                    endTime,
                    false, AppPrefs.militaryTime)

                val constraints = ConstraintSet()
                constraints.clone(constraintLayout)
                constraints.applyTo(constraintLayout)
                shiftLocation.text = locationCode
                shiftRole.text = positionCode
                shiftStatus.text = when(data.shiftRequestStatus) {
                    "Submitted" -> itemView.context.getString(R.string.pending)
                    "Rejected" -> itemView.context.getString(R.string.rejected)
                    else -> ""
                }

                shiftBidders.visibility = if (data.totalRequests > 0) View.VISIBLE else View.INVISIBLE
                shiftHours.visibility = if (hoursPaid > 0) View.VISIBLE else View.INVISIBLE
                actionButton.visibility = if (data.actions.count() > 0) View.VISIBLE else View.INVISIBLE
                shiftStatus.visibility = if (data.shiftRequestStatus != "Open") View.VISIBLE else View.INVISIBLE

                actionButton.text = if (data.actions.contains("Cancel")) itemView.context.getString(R.string.cancel) else {
                    if (data.openShiftType == "AutomatedAssignment") itemView.context.getString(R.string.take) else itemView.context.getString(R.string.bid)
                }
                if (data.selected) {
                    actionButton.background =
                        AppCompatResources.getDrawable(itemView.context, R.drawable.open_shift_action_button_active)
                    actionButton.setTextColor(Color.WHITE)
                } else {
                    actionButton.background =
                        AppCompatResources.getDrawable(itemView.context, R.drawable.open_shift_action_button)

                    actionButton.setTextColor(Color.parseColor(itemView.context.getString(R.string.selectable_bid_text_color)))
                }

                itemView.context.getString(R.string.bidders, data.totalRequests)
                    .also { shiftBidders.text = it }
                shiftHours.text = String.format("%.2f Hrs", hoursPaid)
            }
            dayTag.visibility = if (position == 0) View.VISIBLE else View.INVISIBLE
        }
    }

    fun interface SelectionInterface {
        fun onSelection(key: String, isCancel: Boolean)
    }
}