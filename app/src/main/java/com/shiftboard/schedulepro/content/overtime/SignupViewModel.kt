package com.shiftboard.schedulepro.content.overtime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shiftboard.schedulepro.core.network.common.Maybe
import com.shiftboard.schedulepro.core.network.model.overtime.*
import com.shiftboard.schedulepro.resources.OvertimeActions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import java.lang.Exception


class SignupViewModel(private val signupRepo: SignupRepo) : ViewModel() {
    private val _viewState = MutableStateFlow<SignupViewState>(SignupViewState.NotInit)
    val viewState: StateFlow<SignupViewState> = _viewState.asStateFlow()

    private val _savingState = MutableStateFlow<SavingState>(SavingState.NotInit)
    val savingState: StateFlow<SavingState> = _savingState.asStateFlow()

    private var baseOtState: OTSignup? = null

    // I want this to be isolated from the rest of the state,
    // otherwise we will get a lot of view refreshes
    private var selectedShifts = listOf<String>()

    fun loadDate(date: LocalDate) {
        viewModelScope.launch {
            _viewState.value = SignupViewState.LoadingState

            when (val results = signupRepo.fetchSignup(date)) {
                is Maybe.Success -> {
                    baseOtState = results.data
                    setupMain(results.data)
                }
                is Maybe.Failure -> {
                    _viewState.value = SignupViewState.ErrorState(results.exception)
                }
            }
        }
    }

    fun updateSelectedShifts(shifts: List<String>) {
        selectedShifts = shifts
    }

    fun getSelectedShifts(): List<String> = selectedShifts
    fun getPreviousSelectedShifts(): List<String> = baseOtState?.shiftTimes?.filter { it.selected }?.map { it.id } ?: listOf()

    fun isFirstSave(): Boolean {
        return baseOtState?.lastSubmitted == null
    }

    private fun setupMain(data: OTSignup) {
        val fieldSelections = hashMapOf<String, SignupField>()

        data.fields.forEach {
            fieldSelections[it.type] = it
        }

        selectedShifts = data.shiftTimes.filter { it.selected }.map { it.id }

        val nextKey = fieldSelections.nextRequiredField()
        _viewState.value = SignupViewState.MainState(
            data, nextKey, fieldSelections
        )
    }

    fun isReadOnly(): Boolean {
        return when (val state = _viewState.value) {
            is SignupViewState.MainState -> !state.signup.allowEdits
            else -> false
        }
    }

    fun canSave(): Boolean {
        return when (val state = _viewState.value) {
            is SignupViewState.MainState -> {
                // can't save if we are editing a field
                if (state.activeField != null) return false
                val base = baseOtState ?: return false

                // we couldn't select anything
                if (!state.signup.allowEdits) return false

                val changed = base.fields.any {
                    it.selected != state.currentSelections[it.type]?.selected
                }
                val shiftChange = base.shiftTimes.filter { it.selected }.map { it.id } != selectedShifts

                return (changed || shiftChange) && (selectedShifts.isNotEmpty() || !isFirstSave())
            }
            else -> false
        }
    }

    private fun HashMap<String, SignupField>.nextRequiredField(): String? {
        entries.forEach {
            if (it.value.required && it.value.selected.isEmpty()) {
                return it.key
            }
        }
        return null
    }

    fun updateFieldSelections(key: String, selections: List<String>) {
        when (val state = _viewState.value) {
            is SignupViewState.MainState -> {
                val map = state.currentSelections
                map[key] = map[key]?.copy(selected = selections) ?: throw Exception("Unknown key $key")
                val nextKey = map.nextRequiredField()

                _viewState.value = SignupViewState.MainState(state.signup, nextKey, map)
            }
            else -> throw IllegalStateException("State must be Main to call this method")
        }
    }

    fun cancelFieldInteraction() {
        when (val state = _viewState.value) {
            is SignupViewState.MainState -> {
                _viewState.value = state.copy(activeField = null)
            }
            else -> throw IllegalStateException("State must be Main to call this method")
        }
    }

    fun startFieldInteraction(key: String) {
        when (val state = _viewState.value) {
            is SignupViewState.MainState -> {
                if (key !in state.currentSelections.keys) {
                    throw IllegalStateException("Key must be in fields")
                }
                _viewState.value = state.copy(activeField = key, atomic = !state.atomic)
            }
            else -> throw IllegalStateException("State must be Main to call this method")
        }
    }

    fun removeOption(key: String, option: String) {
        when (val state = _viewState.value) {
            is SignupViewState.MainState -> {
                val currentSelections = state.currentSelections

                val field = currentSelections[key] ?: return
                val newField = field.copy(selected = field.selected.filter { it != option })
                currentSelections[key] = newField

                // changing the hash map doesn't change the identity of the state so flip the atomic var to force it
                _viewState.value = state.copy(currentSelections = currentSelections, atomic = !state.atomic)
            }
            else -> throw IllegalStateException("State must be Main to call this method")
        }
    }

    fun postSignup(selectedSites: List<String>) {
        when (val state = _viewState.value) {
            is SignupViewState.MainState -> {
                val fieldStates = state.currentSelections.map {
                    FieldSelection(it.key, it.value.selected)
                }

                viewModelScope.launch {
                    _savingState.value = SavingState.LoadingState

                    val results = signupRepo.postSignup(
                        OTSignupPost(
                            state.signup.id,
                            state.signup.date,
                            fieldStates,
                            selectedSites
                        ))

                    when (results) {
                        is OTSignupResult.Success -> {
                            _savingState.value = SavingState.SuccessState(results.date)
                        }
                        is OTSignupResult.FailWithData -> {
                            _savingState.value = SavingState.ErrorState(results.throwable)
                            setupMain(results.signupData)
                        }
                        is OTSignupResult.Failure -> {
                            _savingState.value = SavingState.ErrorState(results.throwable)
                        }
                    }
                }
            }
            else -> throw IllegalStateException("State must be Main to call this method")
        }
    }
}

sealed class SignupViewState {
    object NotInit: SignupViewState()
    object LoadingState: SignupViewState()
    data class ErrorState(val throwable: Throwable?): SignupViewState()

    data class MainState(
        val signup: OTSignup,
        val activeField: String?,
        // Mutating copy of fields
        val currentSelections: HashMap<String, SignupField>,
        // Annoying thing about flow states is they sometimes don't update properly
        val atomic: Boolean = false,
    ): SignupViewState()
}

sealed class SavingState {
    object NotInit: SavingState()
    object LoadingState: SavingState()

    data class ErrorState(val throwable: Throwable?): SavingState()

    data class SuccessState(val date: LocalDate): SavingState()
}