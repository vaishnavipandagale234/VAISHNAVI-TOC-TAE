package com.example.model

import java.util.Locale

/**
 * Represents a discrete DFA state partition over the [-1.0, 1.0] continuous hidden-state range.
 */
data class DfaInterval(
  val index: Int,
  val label: String,       // e.g. "State A"
  val shortLabel: String,  // e.g. "A"
  val lower: Double,
  val upper: Double,
  val isLast: Boolean = false,
) {
  fun formatRange(): String {
    val l = String.format(Locale.US, "%.3f", lower)
    val u = String.format(Locale.US, "%.3f", upper)
    return if (isLast) "[$l, $u]" else "[$l, $u)"
  }
}

/**
 * Hyperparameters and configuration for the simple Recurrent Neural Network.
 */
data class RnnConfig(
  val inputWeight: Double = 0.8,
  val recurrentWeight: Double = 0.6,
  val bias: Double = -0.1,
  val initialHiddenState: Double = 0.0,
  val dfaStateCount: Int = 3,
)

/**
 * Represents a single transition step in the RNN execution.
 */
data class TransitionStep(
  val stepNumber: Int,
  val inputSymbol: Char,
  val inputVal: Double,
  val prevHiddenState: Double,
  val netInput: Double,
  val currentHiddenState: Double,
  val dfaState: DfaInterval,
)

/**
 * Complete snapshot of the simulation state.
 */
data class SimulationState(
  val config: RnnConfig = RnnConfig(),
  val inputSequence: String = "010110",
  val intervals: List<DfaInterval> = emptyList(),
  val initialDfaState: DfaInterval = DfaInterval(0, "State A", "A", -1.0, 1.0, true),
  val allSteps: List<TransitionStep> = emptyList(),
  val currentStepIndex: Int = 0, // 0 = initial state (step 0), 1 = step 1 executed, etc.
) {
  val totalSteps: Int get() = allSteps.size
  val hasStarted: Boolean get() = currentStepIndex > 0
  val isCompleted: Boolean get() = allSteps.isNotEmpty() && currentStepIndex >= allSteps.size

  val currentStep: TransitionStep?
    get() = if (currentStepIndex in 1..allSteps.size) allSteps[currentStepIndex - 1] else null

  val currentHiddenState: Double
    get() = currentStep?.currentHiddenState ?: config.initialHiddenState

  val currentDfaState: DfaInterval
    get() = currentStep?.dfaState ?: initialDfaState

  val executedSteps: List<TransitionStep>
    get() = allSteps.take(currentStepIndex)

  val visitedDfaStateIndices: Set<Int>
    get() = setOf(initialDfaState.index) + executedSteps.map { it.dfaState.index }

  val dfaSequenceString: String
    get() {
      if (currentStepIndex == 0) return initialDfaState.shortLabel
      val path = mutableListOf(initialDfaState.shortLabel)
      executedSteps.forEach { path.add(it.dfaState.shortLabel) }
      return path.joinToString(" → ")
    }
}
