package com.example.viewmodel

import androidx.lifecycle.ViewModel
import com.example.engine.RnnSimulator
import com.example.model.RnnConfig
import com.example.model.SimulationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RnnViewModel : ViewModel() {

  // User input fields
  private val _inputSequence = MutableStateFlow("010110")
  val inputSequence: StateFlow<String> = _inputSequence.asStateFlow()

  private val _sequenceError = MutableStateFlow<String?>(null)
  val sequenceError: StateFlow<String?> = _sequenceError.asStateFlow()

  private val _initialHiddenState = MutableStateFlow("0.0")
  val initialHiddenState: StateFlow<String> = _initialHiddenState.asStateFlow()

  private val _numDfaStates = MutableStateFlow(3)
  val numDfaStates: StateFlow<Int> = _numDfaStates.asStateFlow()

  private val _inputWeight = MutableStateFlow("0.8")
  val inputWeight: StateFlow<String> = _inputWeight.asStateFlow()

  private val _recurrentWeight = MutableStateFlow("0.6")
  val recurrentWeight: StateFlow<String> = _recurrentWeight.asStateFlow()

  private val _bias = MutableStateFlow("-0.1")
  val bias: StateFlow<String> = _bias.asStateFlow()

  // Full simulation state
  private val _simulationState = MutableStateFlow(createInitialSimulationState())
  val simulationState: StateFlow<SimulationState> = _simulationState.asStateFlow()

  // Selected step index for graph / transition inspection (-1 = none/auto, 0 = initial, 1..N = step N)
  private val _selectedStep = MutableStateFlow<Int?>(null)
  val selectedStep: StateFlow<Int?> = _selectedStep.asStateFlow()

  init {
    recalculateSimulation(autoRunToCurrentIndex = false)
  }

  private fun parseConfig(): RnnConfig {
    val h0 = _initialHiddenState.value.toDoubleOrNull()?.coerceIn(-1.0, 1.0) ?: 0.0
    val wx = _inputWeight.value.toDoubleOrNull() ?: 0.8
    val wh = _recurrentWeight.value.toDoubleOrNull() ?: 0.6
    val b = _bias.value.toDoubleOrNull() ?: -0.1
    val nStates = _numDfaStates.value.coerceIn(2, 8)
    return RnnConfig(
      inputWeight = wx,
      recurrentWeight = wh,
      bias = b,
      initialHiddenState = h0,
      dfaStateCount = nStates,
    )
  }

  private fun createInitialSimulationState(): SimulationState {
    val config = parseConfig()
    return RnnSimulator.simulate(_inputSequence.value, config)
  }

  private fun recalculateSimulation(autoRunToCurrentIndex: Boolean = false) {
    val error = RnnSimulator.validateInputSequence(_inputSequence.value)
    _sequenceError.value = error
    if (error != null) return

    val config = parseConfig()
    val fullSim = RnnSimulator.simulate(_inputSequence.value, config)
    val oldIndex = _simulationState.value.currentStepIndex
    val newIndex = if (autoRunToCurrentIndex) {
      oldIndex.coerceIn(0, fullSim.totalSteps)
    } else {
      0
    }
    _simulationState.value = fullSim.copy(currentStepIndex = newIndex)
    _selectedStep.value = null
  }

  fun onInputSequenceChange(newSequence: String) {
    _inputSequence.value = newSequence
    val error = RnnSimulator.validateInputSequence(newSequence)
    _sequenceError.value = error
    if (error == null) {
      recalculateSimulation(autoRunToCurrentIndex = false)
    }
  }

  fun setQuickSequence(seq: String) {
    _inputSequence.value = seq
    _sequenceError.value = null
    recalculateSimulation(autoRunToCurrentIndex = false)
  }

  fun onInitialHiddenStateChange(value: String) {
    _initialHiddenState.value = value
    val d = value.toDoubleOrNull()
    if (d != null && d in -1.0..1.0) {
      recalculateSimulation(autoRunToCurrentIndex = true)
    }
  }

  fun onNumDfaStatesChange(count: Int) {
    _numDfaStates.value = count.coerceIn(2, 8)
    recalculateSimulation(autoRunToCurrentIndex = true)
  }

  fun onInputWeightChange(value: String) {
    _inputWeight.value = value
    if (value.toDoubleOrNull() != null) {
      recalculateSimulation(autoRunToCurrentIndex = true)
    }
  }

  fun onRecurrentWeightChange(value: String) {
    _recurrentWeight.value = value
    if (value.toDoubleOrNull() != null) {
      recalculateSimulation(autoRunToCurrentIndex = true)
    }
  }

  fun onBiasChange(value: String) {
    _bias.value = value
    if (value.toDoubleOrNull() != null) {
      recalculateSimulation(autoRunToCurrentIndex = true)
    }
  }

  fun runSimulation() {
    val error = RnnSimulator.validateInputSequence(_inputSequence.value)
    if (error != null) {
      _sequenceError.value = error
      return
    }
    val config = parseConfig()
    val fullSim = RnnSimulator.simulate(_inputSequence.value, config)
    _simulationState.value = fullSim.copy(currentStepIndex = fullSim.totalSteps)
    _selectedStep.value = fullSim.totalSteps
  }

  fun stepForward() {
    val state = _simulationState.value
    if (state.currentStepIndex < state.totalSteps) {
      val nextIndex = state.currentStepIndex + 1
      _simulationState.update { it.copy(currentStepIndex = nextIndex) }
      _selectedStep.value = nextIndex
    }
  }

  fun resetToDefaults() {
    _inputSequence.value = "010110"
    _sequenceError.value = null
    _initialHiddenState.value = "0.0"
    _numDfaStates.value = 3
    _inputWeight.value = "0.8"
    _recurrentWeight.value = "0.6"
    _bias.value = "-0.1"
    _selectedStep.value = null

    val config = RnnConfig(
      inputWeight = 0.8,
      recurrentWeight = 0.6,
      bias = -0.1,
      initialHiddenState = 0.0,
      dfaStateCount = 3,
    )
    _simulationState.value = RnnSimulator.simulate("010110", config)
  }

  fun selectStep(step: Int?) {
    _selectedStep.value = step
  }
}
