package com.shiftboard.schedulepro.content.leaves.request

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.datetime.datePicker
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.afollestad.materialdialogs.list.SingleChoiceListener
import com.afollestad.materialdialogs.list.listItems
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.shiftboard.schedulepro.R
import com.shiftboard.schedulepro.core.common.PageState
import com.shiftboard.schedulepro.core.common.analytics.AbstractAnalyticsProvider
import com.shiftboard.schedulepro.core.common.analytics.LeaveRequestAnalyticEvents
import com.shiftboard.schedulepro.core.common.setState
import com.shiftboard.schedulepro.core.common.utils.*
import com.shiftboard.schedulepro.core.network.model.LeaveElementModel
import com.shiftboard.schedulepro.core.persistence.CacheResponse
import com.shiftboard.schedulepro.databinding.LeaveRequestDialogBinding
import com.shiftboard.schedulepro.ui.form.FormPicker
import com.shiftboard.schedulepro.ui.form.PickerDelegate
import com.shiftboard.schedulepro.ui.lazyViewBinding
import com.shiftboard.schedulepro.ui.tryWith
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.threeten.bp.LocalDate


class LeaveRequestDialog(clickDate: LocalDate, private val postSubmit: OnLeaveRequestSuccess) : BottomSheetDialogFragment() {
    private val boundView by lazyViewBinding<LeaveRequestDialogBinding>()
    private val viewModel by viewModel<LeaveRequestViewModel>()
    private val analytics by inject<AbstractAnalyticsProvider>()

    private var hourSummary: TextView? = null
    private var submit: Button? = null
    private var loadingDialog: MaterialDialog? = null

    private var start: LocalDate? = clickDate
    private var end: LocalDate? = clickDate
    private var leaveType: LeaveElementModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.leave_request_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tryWith(boundView) {
            dialogContent.startDate.setPickerDelegate(object : PickerDelegate<LocalDate>() {
                override fun createPicker(view: FormPicker, pickerResults: (LocalDate?) -> Unit) {
                    MaterialDialog(requireContext()).show {
                        lifecycleOwner(this@LeaveRequestDialog)
                        datePicker(
                            currentDate = (start ?: LocalDate.now()).toCalendar(),
//                           TODO:: minDate = LocalDate.now().toCalendar()
                        ) { _, datetime ->
                            pickerResults(datetime.toLocalDate())
                        }
                    }
                }

                override fun onResults(view: FormPicker, result: LocalDate?) {
                    start = result
                    updateFields()
                }
            })
            dialogContent.startDate.setShowTextClearListener { false }

            dialogContent.endDate.setPickerDelegate(object : PickerDelegate<LocalDate>() {
                override fun createPicker(view: FormPicker, pickerResults: (LocalDate?) -> Unit) {
                    MaterialDialog(requireContext()).show {
                        lifecycleOwner(this@LeaveRequestDialog)
                        datePicker(
                            currentDate = (end ?: LocalDate.now()).toCalendar(),
//                            TODO:: minDate = LocalDate.now().toCalendar()
                        ) { _, datetime ->
                            pickerResults(datetime.toLocalDate())
                        }
                    }
                }

                override fun onResults(view: FormPicker, result: LocalDate?) {
                    end = result
                    updateFields()
                }
            })
            dialogContent.endDate.setShowTextClearListener { false }

            viewModel.leaveTypes.observe(viewLifecycleOwner) {
                when (it) {
                    is CacheResponse.Loading -> {
                        stateFlipper.setState(PageState.LOADING)
                    }
                    is CacheResponse.Success -> {
                        dialogContent.type.setPickerDelegate(object :
                            PickerDelegate<LeaveElementModel>() {
                            override fun createPicker(
                                view: FormPicker,
                                pickerResults: (LeaveElementModel?) -> Unit,
                            ) {
                                showMaterialDialog {
                                    lifecycleOwner(this@LeaveRequestDialog)
                                    title(R.string.select_leave_type)
                                    listItems(items = it.data.map { value -> value.description },
                                        selection = object : SingleChoiceListener {
                                            override fun invoke(
                                                dialog: MaterialDialog,
                                                index: Int,
                                                text: CharSequence,
                                            ) {
                                                pickerResults(it.data[index])
                                            }
                                        })
                                }
                            }

                            override fun onResults(view: FormPicker, result: LeaveElementModel?) {
                                leaveType = result
                                updateFields()
                            }
                        })
                        stateFlipper.setState(PageState.SUCCESS)
                    }
                    is CacheResponse.Failure -> {
                        stateFlipper.setState(PageState.ERROR)
                    }
                }
                dialogContent.type.setShowTextClearListener { false }
            }

            viewModel.submitState.observe(viewLifecycleOwner) { event ->
                event?.doUnlessHandledOrNull { state ->
                    when (state) {
                        is LeaveRequestState.ErrorState -> {
                            analytics.logEvent(LeaveRequestAnalyticEvents.OnError(state.exception?.message
                                ?: ""))
                            loadingDialog?.cancel()

                            showMaterialDialog {
                                message(text = state.exception?.message
                                    ?: getString(R.string.leave_request_error))
                                positiveButton(R.string.ok)
                            }
                        }
                        is LeaveRequestState.SuccessState -> {
                            analytics.logEvent(LeaveRequestAnalyticEvents.OnFinish)

                            loadingDialog?.cancel()
                            postSubmit.onSuccess(state.invalidateStart, state.invalidateEnd,
                                getString(R.string.leave_request_submitted))

                            dismiss()
                        }
                        is LeaveRequestState.LoadingState -> {
                            loadingDialog?.cancel()
                            loadingDialog = buildMaterialDialog {
                                customView(R.layout.loading_dialog)
                                lifecycleOwner(this@LeaveRequestDialog)
                                cancelOnTouchOutside(false)
                                cancelable(false)
                            }
                            loadingDialog?.show()
                        }
                    }
                }
            }

            updateFields()
        }
    }

    private fun updateFields() {
        tryWith(boundView) {
            dialogContent.startDate.setText(start?.displayDate() ?: "")
            dialogContent.endDate.setText(end?.displayDate() ?: "")
            dialogContent.type.setText(leaveType?.description ?: "")

            submit?.isEnabled = start != null && end != null && leaveType != null
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: BottomSheetDialog =
            super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val coordinator =
                (it as BottomSheetDialog).findViewById<CoordinatorLayout>(com.google.android.material.R.id.coordinator)
            val containerLayout =
                it.findViewById<FrameLayout>(com.google.android.material.R.id.container)
            val buttons =
                dialog.layoutInflater.inflate(R.layout.leave_request_dialog_actions, null)

            val bottomSheet =
                (it as BottomSheetDialog).findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)

            val behavior = bottomSheet?.let { layout -> BottomSheetBehavior.from(layout) }
            behavior?.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        analytics.logEvent(LeaveRequestAnalyticEvents.OnCancel)
                        activity?.hideSoftKeyboard()
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {

                }
            })
            buttons.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM)
            containerLayout?.addView(buttons)
            buttons.setOnClickListener { /* prevent click pass-through */ }

            hourSummary = buttons.findViewById(R.id.hour_summary)

            submit = buttons.findViewById(R.id.submit)
            submit?.setOnClickListener { submit() }

            buttons.post {
                (coordinator?.layoutParams as? ViewGroup.MarginLayoutParams).apply {
                    buttons.measure(
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                    )
                    this?.bottomMargin = buttons.measuredHeight
                    containerLayout?.requestLayout()
                }
            }
        }
        return dialog
    }

    private fun submit() {
        viewModel.submitLeaveRequest(PostLeaveRequest(
            start ?: return, end ?: return, leaveType?.id ?: return,
            boundView.dialogContent.comment.getText()
        ))
    }

    fun interface OnLeaveRequestSuccess {
        fun onSuccess(leaveStart: LocalDate, leaveEnd: LocalDate, message: String)
    }
}