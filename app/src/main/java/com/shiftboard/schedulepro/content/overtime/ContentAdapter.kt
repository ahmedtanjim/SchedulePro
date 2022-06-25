package com.shiftboard.schedulepro.content.overtime

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.shiftboard.schedulepro.R
import com.shiftboard.schedulepro.common.prefs.AppPrefs
import com.shiftboard.schedulepro.core.common.utils.shortDateTimeDisplay
import com.shiftboard.schedulepro.core.network.model.overtime.OTSignup
import com.shiftboard.schedulepro.core.network.model.overtime.SignupField
import com.shiftboard.schedulepro.core.network.model.overtime.SignupFieldOptions
import com.shiftboard.schedulepro.core.network.model.overtime.SignupShiftTime
import com.shiftboard.schedulepro.resources.SignupSelectionType
import com.shiftboard.schedulepro.ui.formatting.TimeFormatters
import com.shiftboard.schedulepro.ui.overtime.CheckStateView
import com.shiftboard.schedulepro.ui.overtime.CheckableToggleView
import com.shiftboard.schedulepro.ui.schedule.DayTagView
import org.threeten.bp.LocalDate
import java.util.*


class ContentAdapter(private val contentInterface: ContentInterface) :
    RecyclerView.Adapter<ContentAdapter.ContentViewHolder>() {
    private val elements = mutableListOf<ContentHolder>()
    private var signup: OTSignup? = null
    private var enableMultiSelect: Boolean = true
    private val selected = hashSetOf<String>()
    private val prevSelection = hashSetOf<String>()
    private var headerPos: Int = RecyclerView.NO_POSITION

    fun selectedShifts(): List<String> = selected.toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        return when (viewType) {
            R.layout.signup_element_content_single -> SingleSelectViewHolder(parent).apply {
                textEdit.setOnClickListener {
                    val element = elements[bindingAdapterPosition] as? SignupFieldElement
                        ?: return@setOnClickListener
                    if (signup?.allowEdits == true) {
                        contentInterface.startFieldInteraction(element.signupField)
                    }
                }
            }
            R.layout.signup_element_content_multi -> MultiSelectViewHolder(parent).apply {
                selectAction.setOnClickListener {
                    val element = elements[bindingAdapterPosition] as? SignupFieldElement
                        ?: return@setOnClickListener
                    if (signup?.allowEdits == true) {
                        contentInterface.startFieldInteraction(element.signupField)
                    }
                }

                chipCloseListener = { key, option -> contentInterface.removeOption(key, option.id) }
            }
            R.layout.signup_element_content_header -> ShiftHeaderViewHolder(parent).apply {
                shiftToggleView.onToggleListener = CheckableToggleView.ToggleListener { active, _ ->
                    if (active) {
                        signup?.shiftTimes?.forEach {
                            selected.add(it.id)
                        } ?: return@ToggleListener
                    } else {
                        selected.clear()
                    }
                    notifyItemRangeChanged(headerPos, elements.size - 1)
                    contentInterface.shiftsUpdated(selectedShifts())
                }
            }
            R.layout.signup_element_content_shift -> ShiftViewHolder(parent).apply {
                toggleView.setOnClickListener {
                    val element = elements[bindingAdapterPosition] as? SignupShiftElement
                        ?: return@setOnClickListener
                    val id = element.signupTime.id

                    if (id in selected) {
                        selected.remove(id)
                    } else {
                        selected.add(id)
                    }

                    notifyItemChanged(bindingAdapterPosition)
                    notifyItemChanged(headerPos)
                    contentInterface.shiftsUpdated(selectedShifts())
                }
            }
            R.layout.schedule_element_holiday -> HolidayElementViewHolder(parent)
            else -> throw Exception("Invalid View Type")
        }
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        when (holder) {
            is SingleSelectViewHolder -> {
                holder.bind(elements[position] as SignupFieldElement)
            }
            is MultiSelectViewHolder -> {
                holder.bind(elements[position] as SignupFieldElement)
            }
            is ShiftHeaderViewHolder -> {
                holder.bind()
            }
            is ShiftViewHolder -> {
                holder.bind(elements[position] as SignupShiftElement)
            }
            is HolidayElementViewHolder -> {
                holder.bind(elements[position] as SignupHolidayElement)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (val element = elements[position]) {
            is SignupShiftHeaderElement -> R.layout.signup_element_content_header
            is SignupShiftElement -> R.layout.signup_element_content_shift
            is SignupFieldElement -> {
                if (element.signupField.selectionType == SignupSelectionType.SINGLE) {
                    R.layout.signup_element_content_single
                } else {
                    R.layout.signup_element_content_multi
                }
            }
            is SignupHolidayElement -> R.layout.schedule_element_holiday
            else -> throw Exception("Invalid Item")
        }
    }

    override fun getItemCount(): Int = elements.size

    fun setData(
        signups: OTSignup,
        prevSelectedShifts: List<String>,
        selectedShifts: List<String>,
        fields: HashMap<String, SignupField>,
    ) {
        elements.clear()
        selected.clear()
        prevSelection.clear()

        prevSelection.addAll(prevSelectedShifts)
        signup = signups

        fields.values.toList().forEach {
            elements.add(SignupFieldElement(it))
        }
        elements.add(SignupShiftHeaderElement())
        // Store header pos to be able to easily reference it later.
        // It won't change until we rebind data so this should be safe.
        headerPos = elements.size - 1

        enableMultiSelect = signups.shiftTimes.all { it.selectable }

        if (!signups.holidayName.isNullOrBlank()) {
            elements.add(SignupHolidayElement(signups.date, signups.holidayName ?: ""))
        }
        signups.shiftTimes.filter {
            it.selectable || it.id in selectedShifts
        }.forEachIndexed { index, signupShiftTime ->
            elements.add(SignupShiftElement(signupShiftTime,
                index == 0 && signups.holidayName.isNullOrBlank()))
        }
        selected.addAll(selectedShifts)

        notifyDataSetChanged()
    }

    abstract class ContentViewHolder(parent: ViewGroup, @LayoutRes res: Int) :
        RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(res, parent, false))

    private class SingleSelectViewHolder(parent: ViewGroup) :
        ContentViewHolder(parent, R.layout.signup_element_content_single) {
        val textContent: TextInputLayout by lazy { itemView.findViewById(R.id.text_content) }
        val textEdit: TextInputEditText by lazy { itemView.findViewById(R.id.text_edit) }

        fun bind(data: SignupFieldElement) {
            textContent.hint = data.signupField.name
            val selected = data.signupField.selected.firstOrNull()?.let { id ->
                data.signupField.options.find { it.id == id }
            }
            if (selected != null) {
                textEdit.setText(selected.name)
            } else {
                textEdit.setText(R.string.no_selection)
            }
        }
    }

    private inner class MultiSelectViewHolder(parent: ViewGroup) :
        ContentViewHolder(parent, R.layout.signup_element_content_multi) {

        private val title: TextView by lazy { itemView.findViewById(R.id.title) }
        val selectAction: TextView by lazy { itemView.findViewById(R.id.select_action) }
        private val chipGroup: RecyclerView by lazy { itemView.findViewById(R.id.chip_group) }

        var chipCloseListener: (String, SignupFieldOptions) -> Unit = { _, _ -> }

        private val adapter: ChipAdapter by lazy {
            ChipAdapter { key, option ->
                // proxy this call so we can easily override it without making things complicated
                chipCloseListener(key, option)
            }
        }

        init {
            chipGroup.layoutManager = FlexboxLayoutManager(itemView.context)
            chipGroup.adapter = adapter
        }

        fun bind(data: SignupFieldElement) {
            title.text = data.signupField.name
            selectAction.isVisible =
                data.signupField.selected.size != data.signupField.options.size && signup?.allowEdits == true
            adapter.setData(data.signupField, signup?.allowEdits == true)
        }
    }

    private inner class ShiftHeaderViewHolder(parent: ViewGroup) :
        ContentViewHolder(parent, R.layout.signup_element_content_header) {
        val shiftToggleView: CheckableToggleView by lazy { itemView.findViewById(R.id.shift_toggle_view) }

        fun bind() {
            // If we have selected all the available shifts force the toggle to active otherwise force it inactive.
            if (enableMultiSelect) {
                shiftToggleView.isVisible = true
                if (selected.size == (signup?.shiftTimes?.size ?: -1)) {
                    shiftToggleView.state = CheckStateView.State.MULTISELECT_ACTIVE
                } else {
                    shiftToggleView.state = CheckStateView.State.MULTISELECT_INACTIVE
                }
            } else {
                shiftToggleView.isVisible = false
            }
        }
    }

    private inner class HolidayElementViewHolder(parent: ViewGroup) :
        ContentViewHolder(parent, R.layout.schedule_element_holiday) {
        val dayTag: DayTagView by lazy { itemView.findViewById(R.id.day_tag) }
        val description: TextView by lazy { itemView.findViewById(R.id.description) }

        fun bind(element: SignupHolidayElement) {
            dayTag.setDate(element.date)
            dayTag.visibility = View.VISIBLE

            description.text = element.holiday
        }
    }

    private inner class ShiftViewHolder(parent: ViewGroup) :
        ContentViewHolder(parent, R.layout.signup_element_content_shift) {

        private val dayTag: DayTagView by lazy { itemView.findViewById(R.id.day_tag) }
        private val constraintLayout: ConstraintLayout by lazy { itemView.findViewById(R.id.constraint_layout) }
        private val colorBar: View by lazy { itemView.findViewById(R.id.color_bar) }
        private val shiftCode: TextView by lazy { itemView.findViewById(R.id.shift_time_code) }
        private val shiftTime: TextView by lazy { itemView.findViewById(R.id.shift_time) }
        private val dueTimestamp: TextView by lazy { itemView.findViewById(R.id.due_timestamp) }
        val toggleView: CheckableToggleView by lazy { itemView.findViewById(R.id.toggle_view) }

        fun bind(data: SignupShiftElement) {
            with(data.signupTime) {
                dayTag.visibility = if (data.first) View.VISIBLE else View.INVISIBLE
                dayTag.setDate(date)

                colorBar.setBackgroundColor(color)
                shiftCode.text = shiftTimeDescription

                shiftTime.text = TimeFormatters.spannedShortRange(
                    startTime,
                    false /*startTime.isSameDay(date)*/,
                    endTime,
                    false /*endTime.isSameDay(date)*/, AppPrefs.militaryTime)

                signup?.lastSubmitted?.takeIf { id in prevSelection }?.let {
                    dueTimestamp.text =
                        itemView.context.getString(R.string.submitted_timestamp,
                            it.shortDateTimeDisplay(
                                AppPrefs.militaryTime))
                } ?: run {
                    dueTimestamp.text =
                        itemView.context.getString(R.string.due_timestamp,
                            cutoffTime.shortDateTimeDisplay(
                                AppPrefs.militaryTime))
                }

                if (data.signupTime.selectable) {
                    toggleView.isVisible = true
                    toggleView.state = if (id in this@ContentAdapter.selected)
                        CheckStateView.State.ACTIVE else CheckStateView.State.INACTIVE
                } else {
                    toggleView.isVisible = false
                }

                val constraints = ConstraintSet()
                constraints.clone(constraintLayout)
                constraints.applyTo(constraintLayout)
            }
        }
    }

    interface ContentInterface {
        fun startFieldInteraction(field: SignupField)
        fun removeOption(key: String, selection: String)
        fun shiftsUpdated(list: List<String>)
    }
}

private interface ContentHolder

private inline class SignupFieldElement(val signupField: SignupField) : ContentHolder
private class SignupShiftElement(val signupTime: SignupShiftTime, val first: Boolean) :
    ContentHolder

private class SignupHolidayElement(val date: LocalDate, val holiday: String) : ContentHolder
private class SignupShiftHeaderElement : ContentHolder