package com.shiftboard.schedulepro.content.overtime

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.shiftboard.schedulepro.R
import com.shiftboard.schedulepro.core.common.analytics.AbstractAnalyticsProvider
import com.shiftboard.schedulepro.core.common.analytics.SignupAnalyticEvents
import com.shiftboard.schedulepro.core.common.utils.displayDate
import com.shiftboard.schedulepro.core.common.utils.serverFormat
import com.shiftboard.schedulepro.core.network.model.overtime.OTSignup
import com.shiftboard.schedulepro.core.network.model.overtime.SignupField
import com.shiftboard.schedulepro.databinding.SignupDialogActionsBinding
import com.shiftboard.schedulepro.databinding.SignupDialogBinding
import com.shiftboard.schedulepro.resources.SignupSelectionType
import com.shiftboard.schedulepro.ui.utils.disableAnimations
import kotlinx.coroutines.flow.collectLatest
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.threeten.bp.LocalDate


class SignupDialogFragment(
    private val clickDate: LocalDate,
    private val postSubmit: OnSignupRequestSuccess,
) : BottomSheetDialogFragment() {
    private val analytics by inject<AbstractAnalyticsProvider>()
    private val signupViewModel by viewModel<SignupViewModel>()

    private var _contentBinding: SignupDialogBinding? = null
    private val contentBinding: SignupDialogBinding
        get() =
            _contentBinding
                ?: throw IllegalStateException("content binding can only be accessed while the fragment is created")

    private var _bottomBinding: SignupDialogActionsBinding? = null
    private val bottomBinding: SignupDialogActionsBinding
        get() =
            _bottomBinding
                ?: throw IllegalStateException("bottom binding can only be accessed while the dialog is created")

    private val contentAdapter by lazy {
        ContentAdapter(object : ContentAdapter.ContentInterface {
            override fun startFieldInteraction(field: SignupField) {
                signupViewModel.startFieldInteraction(field.type)
            }

            override fun removeOption(key: String, selection: String) {
                signupViewModel.removeOption(key, selection)
            }

            override fun shiftsUpdated(list: List<String>) {
                signupViewModel.updateSelectedShifts(list)
                if (signupViewModel.isFirstSave()) {
                    bottomBinding.save.text = getString(R.string.signup_count, list.size.toString())
                } else {
                    bottomBinding.save.text = getString(R.string.save)
                }
                bottomBinding.save.isEnabled = signupViewModel.canSave()
            }
        })
    }


    private val singleAdapter by lazy {
        SingleSelectAdapter { key, selection ->
            signupViewModel.updateFieldSelections(key, selection)
        }
    }

    private val multiAdapter by lazy {
        MultiselectAdapter(object : MultiselectAdapter.MultiselectInterface {
            override fun onSelectUpdated(field: SignupField, activeElements: List<String>) {
                bottomBinding.save.isEnabled = !field.required || activeElements.isNotEmpty()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _contentBinding = SignupDialogBinding.inflate(inflater, container, false)
        return contentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenResumed {
            signupViewModel.viewState.collectLatest {
                when (it) {
                    is SignupViewState.MainState -> {
                        contentBinding.stateFlipper.displayedChild = 2
                        loadMainView(it.signup,
                            it.activeField,
                            signupViewModel.getSelectedShifts(),
                            it.currentSelections)
                    }
                    is SignupViewState.ErrorState -> {
                        analytics.logEvent(SignupAnalyticEvents.OnError(it.throwable?.message ?: ""))
                        contentBinding.stateFlipper.displayedChild = 1
                    }
                    else -> {
                        contentBinding.stateFlipper.displayedChild = 0
                    }
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            signupViewModel.savingState.collectLatest {
                when (it) {
                    is SavingState.SuccessState -> {
                        if (signupViewModel.isFirstSave()) {
                            analytics.logEvent(SignupAnalyticEvents.OnCreate)
                        } else {
                            analytics.logEvent(SignupAnalyticEvents.OnUpdate)
                        }
                        postSubmit.onSuccess(it.date,
                            getString(R.string.signup_saved, it.date.displayDate()))
                        dismiss()
                    }
                    is SavingState.ErrorState -> {
                        analytics.logEvent(SignupAnalyticEvents.OnError(it.throwable?.message ?: ""))
                        contentBinding.stateFlipper.displayedChild = 2
                    }
                    else -> {
                        contentBinding.stateFlipper.displayedChild = 0
                    }
                }
            }
        }

        contentBinding.fieldRecycler.layoutManager = LinearLayoutManager(context)
        contentBinding.fieldRecycler.disableAnimations()

        signupViewModel.loadDate(clickDate)
    }

    private fun loadMultiSelectField(activeField: SignupField) {

        bottomBinding.save.isVisible = true

        addOrSwapAdapter(multiAdapter)
        multiAdapter.setData(activeField)

        bottomBinding.save.setText(R.string.next)
        bottomBinding.save.isEnabled = !activeField.required || activeField.selected.isNotEmpty()

        bottomBinding.save.setOnClickListener {
            signupViewModel.updateFieldSelections(multiAdapter.key, multiAdapter.getSelections())
        }

    }

    private fun loadSingleSelectField(activeField: SignupField) {

        bottomBinding.save.isVisible = true

        addOrSwapAdapter(singleAdapter)
        singleAdapter.setData(activeField)

        bottomBinding.save.setText(R.string.cancel)
        bottomBinding.save.isEnabled = !activeField.required || activeField.selected.isNotEmpty()
        bottomBinding.save.setOnClickListener {
            signupViewModel.cancelFieldInteraction()
        }
    }

    private fun loadMainView(
        signup: OTSignup,
        activeField: String?,
        selectedShifts: List<String>,
        fields: HashMap<String, SignupField>,
    ) {
        val field = fields[activeField]
        when (field?.selectionType) {
            SignupSelectionType.SINGLE -> loadSingleSelectField(field)
            SignupSelectionType.MULTI -> loadMultiSelectField(field)
            else -> {
                bottomBinding.save.isVisible = true

                addOrSwapAdapter(contentAdapter)
                contentAdapter.setData(signup, signupViewModel.getPreviousSelectedShifts(), selectedShifts, fields)

                if (signupViewModel.isFirstSave()) {
                    bottomBinding.save.text =
                        getString(R.string.signup_count, signupViewModel.getSelectedShifts()
                            .size.toString())
                } else {
                    bottomBinding.save.text = getString(R.string.save)
                }
                bottomBinding.save.isEnabled = signupViewModel.canSave()
                bottomBinding.save.setOnClickListener {
                    signupViewModel.postSignup(contentAdapter.selectedShifts())
                }
                if (signupViewModel.isReadOnly()) {
                    bottomBinding.save.visibility = View.INVISIBLE
                    bottomBinding.save.isEnabled = false
                }
            }
        }
    }

    private fun <T : RecyclerView.Adapter<*>> addOrSwapAdapter(newAdapter: T) {
        with(contentBinding.fieldRecycler) {
            if (adapter != null) {
                swapAdapter(newAdapter, true)
            } else {
                adapter = newAdapter
            }
        }
    }

    private fun onCancel() {
        analytics.logEvent(SignupAnalyticEvents.OnCancel)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: BottomSheetDialog =
            super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val coordinator =
                (it as BottomSheetDialog).findViewById<CoordinatorLayout>(com.google.android.material.R.id.coordinator)
            val containerLayout =
                it.findViewById<FrameLayout>(com.google.android.material.R.id.container)

            val buttons = dialog.layoutInflater.inflate(R.layout.signup_dialog_actions, null)

            val bottomSheet =
                it.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)

            val behavior = bottomSheet?.let { layout -> BottomSheetBehavior.from(layout) }
            behavior?.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        onCancel()
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

            _bottomBinding = SignupDialogActionsBinding.bind(buttons)
        }
        return dialog
    }

    fun interface OnSignupRequestSuccess {
        fun onSuccess(date: LocalDate, message: String)
    }
}