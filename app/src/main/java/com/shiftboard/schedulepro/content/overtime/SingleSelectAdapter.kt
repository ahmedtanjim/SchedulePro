package com.shiftboard.schedulepro.content.overtime

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.shiftboard.schedulepro.R
import com.shiftboard.schedulepro.core.network.model.overtime.SignupField
import com.shiftboard.schedulepro.core.network.model.overtime.SignupFieldOptions
import com.shiftboard.schedulepro.ui.overtime.CheckStateView
import com.shiftboard.schedulepro.ui.overtime.CheckableToggleView

class SingleSelectAdapter(val selectionInterface: SelectionInterface) : RecyclerView.Adapter<SingleSelectAdapter.SingleSelectViewHolder>() {

    private var field: SignupField? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleSelectViewHolder {
        return when (viewType) {
            R.layout.signup_element_single_header -> HeaderViewHolder(parent)
            R.layout.signup_element_single -> ElementViewHolder(parent).apply {
                itemView.setOnClickListener {
                    val option = field?.options?.get(bindingAdapterPosition - 1) ?: return@setOnClickListener
                    selectionInterface.onSelection(field!!.type, listOf(option.id))
                }
            }
            else -> throw IllegalStateException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: SingleSelectViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> {
                holder.bind()
            }
            is ElementViewHolder -> {
                val option = field?.options?.get(position - 1) ?: return
                holder.bind(option)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> R.layout.signup_element_single_header
            else -> R.layout.signup_element_single
        }
    }

    // number of options plus the header
    override fun getItemCount(): Int = (field?.options?.size ?: -1) + 1

    fun setData(signupField: SignupField) {
        field = signupField
        notifyDataSetChanged()
    }

    abstract class SingleSelectViewHolder(parent: ViewGroup, @LayoutRes res: Int):
        RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(res, parent, false))

    inner class HeaderViewHolder(parent: ViewGroup): SingleSelectViewHolder(parent, R.layout.signup_element_single_header){
        private val title: TextView by lazy { itemView.findViewById(R.id.title) }

        fun bind() {
            title.text = field?.name ?: ""
        }
    }

    inner class ElementViewHolder(parent: ViewGroup): SingleSelectViewHolder(parent, R.layout.signup_element_single) {
        private val title: TextView by lazy { itemView.findViewById(R.id.title) }
        private val toggleView: CheckStateView by lazy { itemView.findViewById(R.id.toggle_view) }

        fun bind(data: SignupFieldOptions) {
            title.text = data.name

            toggleView.state = CheckStateView.State.ACTIVE
            toggleView.visibility = if (data.id == field?.selected?.firstOrNull()) View.VISIBLE else View.INVISIBLE
        }
    }

    fun interface SelectionInterface {
        fun onSelection(key: String, selection: List<String>)
    }
}