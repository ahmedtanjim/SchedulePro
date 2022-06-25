package com.shiftboard.schedulepro.content.openShifts

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
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.shiftboard.schedulepro.R
import com.shiftboard.schedulepro.core.common.PageState
import com.shiftboard.schedulepro.core.common.analytics.AbstractAnalyticsProvider
import com.shiftboard.schedulepro.core.common.analytics.OpenShiftRequestAnalyticEvents
import com.shiftboard.schedulepro.core.common.setState
import com.shiftboard.schedulepro.core.common.utils.*
import com.shiftboard.schedulepro.databinding.OpenShiftDialogBinding
import com.shiftboard.schedulepro.ui.lazyViewBinding
import com.shiftboard.schedulepro.ui.tryWith
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.threeten.bp.LocalDate
import timber.log.Timber

class OpenShiftRequestDialog(clickDate: LocalDate, private val postSubmit: OnOpenShiftRequestSuccess) : BottomSheetDialogFragment() {
    private val boundView by lazyViewBinding<OpenShiftDialogBinding>()
    private val viewModel by viewModel<OpenShiftDialogViewModel>()
    private val analytics by inject<AbstractAnalyticsProvider>()

    private var hourSummary: TextView? = null
    private var submit: Button? = null

    private var start: LocalDate? = clickDate
    private var end: LocalDate? = clickDate

    private val singleAdapter by lazy {
        OpenShiftSelectAdapter { key, isCancel ->
            viewModel.selectedShift = key
            viewModel.isCancel = isCancel
            submit!!.isEnabled = true
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.open_shift_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tryWith(boundView) {
            this.closeIcon.isVisible = true
            stateFlipper.setState(PageState.LOADING)

            viewModel.shifts.observe(viewLifecycleOwner) {
                when (it.openShifts.count()) {
                    0 -> {
                        Timber.d("no shifts")
                        stateFlipper.setState(PageState.ERROR)
                    }
                    else -> {
                        stateFlipper.setState(PageState.SUCCESS)
                        shiftRequestRecycler.adapter = singleAdapter
                        singleAdapter.setData(it.openShifts)
                    }
                }
            }
            viewModel.submitState.observe(viewLifecycleOwner) { event ->
                event?.doUnlessHandledOrNull { state ->
                    when (state) {
                        is OpenShiftRequestState.ErrorState -> {
                            analytics.logEvent(OpenShiftRequestAnalyticEvents.OnError(state.exception?.message
                                ?: ""))
                            stateFlipper.setState(PageState.ERROR)

                            showMaterialDialog {
                                message(text = state.exception?.message
                                    ?: getString(R.string.open_shift_request_failure))
                                positiveButton(R.string.ok)
                            }
                        }
                        is OpenShiftRequestState.SuccessState -> {
                            analytics.logEvent(OpenShiftRequestAnalyticEvents.OnFinish)

                            when (state.isCancel) {
                                false -> postSubmit.onSuccess(start!!, end!!,
                                    getString(R.string.open_shift_success_message))
                                true -> postSubmit.onSuccess(start!!, end!!,
                                    getString(R.string.open_shift_cancel_success))
                            }

                            dismiss()
                        }
                        is OpenShiftRequestState.LoadingState -> {
                           stateFlipper.setState(PageState.LOADING)
                        }
                    }
                }
            }

            updateFields()
            shiftRequestRecycler.layoutManager = LinearLayoutManager(context)

            start?.let { viewModel.loadDate(it) }
        }
    }

    private fun updateFields() {
        tryWith(boundView) {
            submit?.isEnabled = start != null && end != null
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
                it.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)

            val behavior = bottomSheet?.let { layout -> BottomSheetBehavior.from(layout) }
            behavior?.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        analytics.logEvent(OpenShiftRequestAnalyticEvents.OnCancel)
                        activity?.hideSoftKeyboard()
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
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
        when (viewModel.isCancel) {
            false -> start?.let { viewModel.postOpenShiftRequest(viewModel.selectedShift, it) }
            true -> start?.let { viewModel.deleteOpenShiftRequest(viewModel.selectedShift, it) }
        }
    }

    fun interface OnOpenShiftRequestSuccess {
        fun onSuccess(leaveStart: LocalDate, leaveEnd: LocalDate, message: String)
    }
}