package com.shiftboard.schedulepro.content.overtime

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.shiftboard.schedulepro.R
import com.shiftboard.schedulepro.core.network.model.overtime.SignupField
import com.shiftboard.schedulepro.core.network.model.overtime.SignupFieldOptions
import com.shiftboard.schedulepro.ui.overtime.CheckStateView
import com.shiftboard.schedulepro.ui.overtime.CheckableToggleView

class MultiselectAdapter(private val multiselectInterface: MultiselectInterface) :
    RecyclerView.Adapter<MultiselectAdapter.MultiselectViewHolder>() {
    private val activeElements = hashSetOf<String>()
    private var field: SignupField? = null

    val key: String get() = this.field?.type ?: ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultiselectViewHolder {
        return when (viewType) {
            R.layout.signup_element_multiselect_header -> HeaderViewHolder(parent).apply {
                selectAll.onToggleListener = CheckableToggleView.ToggleListener { active, _ ->
                    if (active) {
                        field?.options?.forEach {
                            activeElements.add(it.id)
                        }
                    } else {
                        activeElements.clear()
                    }
                    multiselectInterface.onSelectUpdated(field ?: return@ToggleListener,
                        activeElements.toList())
                    notifyDataSetChanged()
                }
            }
            R.layout.signup_element_multiselect -> ElementViewHolder(parent).apply {
                itemView.setOnClickListener {
                    val option = getItem(bindingAdapterPosition) ?: return@setOnClickListener
                    if (option.id in activeElements) {
                        activeElements.remove(option.id)
                    } else {
                        activeElements.add(option.id)
                    }

                    notifyItemChanged(bindingAdapterPosition)
                    notifyItemChanged(0) // also update header
                    multiselectInterface.onSelectUpdated(field ?: return@setOnClickListener,
                        activeElements.toList())
                }
            }
            else -> throw IllegalStateException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: MultiselectViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> {
                holder.bind()
            }
            is ElementViewHolder -> {
                holder.bind(getItem(position) ?: return)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> R.layout.signup_element_multiselect_header
            else -> R.layout.signup_element_multiselect
        }
    }

    private fun getItem(position: Int): SignupFieldOptions? = field?.options?.get(position - 1)

    override fun getItemCount(): Int = (field?.options?.size ?: -1) + 1

    fun getSelections(): List<String> = activeElements.toList()

    fun setData(signupField: SignupField) {
        activeElements.clear()
        activeElements.addAll(signupField.selected)

        field = signupField

        notifyDataSetChanged()
    }

    abstract class MultiselectViewHolder(parent: ViewGroup, @LayoutRes res: Int) :
        RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(res, parent, false))

    inner class HeaderViewHolder(parent: ViewGroup) :
        MultiselectViewHolder(parent, R.layout.signup_element_multiselect_header) {
        private val title: TextView by lazy { itemView.findViewById(R.id.title) }
        val selectAll: CheckableToggleView by lazy { itemView.findViewById(R.id.select_all) }

        fun bind() {
            title.text = field?.name ?: ""
            if (itemCount - 1 == activeElements.size) {
                selectAll.state = CheckStateView.State.MULTISELECT_ACTIVE
            } else {
                selectAll.state = CheckStateView.State.MULTISELECT_INACTIVE
            }
        }
    }

    inner class ElementViewHolder(parent: ViewGroup) :
        MultiselectViewHolder(parent, R.layout.signup_element_multiselect) {
        private val title: TextView by lazy { itemView.findViewById(R.id.title) }
        private val toggleView: CheckableToggleView by lazy { itemView.findViewById(R.id.toggle_view) }

        init {
            toggleView.setOnClickListener { itemView.callOnClick() }
        }

        fun bind(data: SignupFieldOptions) {
            title.text = data.name
            if (data.id in activeElements) {
                toggleView.state = CheckStateView.State.ACTIVE
            } else {
                toggleView.state = CheckStateView.State.INACTIVE
            }
        }
    }

    interface MultiselectInterface {
        fun onSelectUpdated(field: SignupField, activeElements: List<String>)
    }
}