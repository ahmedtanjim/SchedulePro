package com.shiftboard.schedulepro.content.overtime

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.shiftboard.schedulepro.R
import com.shiftboard.schedulepro.core.network.model.overtime.SignupField
import com.shiftboard.schedulepro.core.network.model.overtime.SignupFieldOptions

class ChipAdapter(private val closeAction: (String, SignupFieldOptions)->Unit): RecyclerView.Adapter<ChipAdapter.ShiftHeaderViewHolder>() {
    private var required = false
    private var key = ""
    private val options = mutableListOf<SignupFieldOptions>()
    private var editEnabled: Boolean = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShiftHeaderViewHolder {
        return ShiftHeaderViewHolder(parent).apply {
            closeAction.setOnClickListener {
                val option = options[bindingAdapterPosition] ?: return@setOnClickListener
                closeAction(key, option)
            }
        }
    }

    override fun onBindViewHolder(holder: ShiftHeaderViewHolder, position: Int) {
        holder.bind(options[position])
    }

    override fun getItemCount(): Int = options.size

    fun setData(data: SignupField, editEnabled: Boolean) {

        val selectedChips = data.options.filter { it.id in data.selected }
        key = data.type
        this.required = data.required
        options.clear()
        this.editEnabled = editEnabled
        options.addAll(selectedChips)
        notifyDataSetChanged()
    }

    inner class ShiftHeaderViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(
        R.layout.signup_element_multiselect_chip, parent, false)) {

        val chipTitle: TextView by lazy { itemView.findViewById(R.id.chip_title) }
        val closeAction: ImageView by lazy { itemView.findViewById(R.id.close_action) }

        fun bind(option: SignupFieldOptions) {
            chipTitle.text = option.name
            closeAction.isVisible = (!required || options.size > 1) && editEnabled
        }
    }
}