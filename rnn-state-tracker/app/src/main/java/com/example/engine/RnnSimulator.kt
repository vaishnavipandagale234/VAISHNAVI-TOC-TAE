package com.example.engine

import com.example.model.DfaInterval
import com.example.model.RnnConfig
import com.example.model.SimulationState
import com.example.model.TransitionStep
import kotlin.math.floor
import kotlin.math.tanh

object RnnSimulator {

  /**
   * Generates N equal-sized DFA intervals covering [-1.0, 1.0].
   */
  fun calculateIntervals(numStates: Int): List<DfaInterval> {
    val count = numStates.coerceIn(2, 10)
    val totalRange = 2.0
    val stepSize = totalRange / count

    return (0 until count).map { i ->
      val lower = -1.0 + i * stepSize
      val upper = if (i == count - 1) 1.0 else -1.0 + (i + 1) * stepSize
      val labelChar = ('A'.code + i).toChar()
      DfaInterval(
        index = i,
        label = "State $labelChar",
        shortLabel = labelChar.toString(),
        lower = lower,
        upper = upper,
        isLast = (i == count - 1),
      )
    }
  }

  /**
   * Maps a continuous hidden-state value in [-1.0, 1.0] to its corresponding discrete DFA interval.
   */
  fun mapToDfaState(hiddenValue: Double, intervals: List<DfaInterval>): DfaInterval {
    if (intervals.isEmpty()) {
      return DfaInterval(0, "State A", "A", -1.0, 1.0, true)
    }
    val count = intervals.size
    val stepSize = 2.0 / count

    // Handle exact or out-of-bound edge cases consistently
    if (hiddenValue <= -1.0) return intervals.first()
    if (hiddenValue >= 1.0) return intervals.last()

    val rawIndex = floor((hiddenValue - (-1.0)) / stepSize).toInt()
    val clampedIndex = rawIndex.coerceIn(0, count - 1)
    return intervals[clampedIndex]
  }

  /**
   * Computes a single RNN transition step:
   * h(t) = tanh(Wx * x(t) + Wh * h(t-1) + b)
   */
  fun computeStep(
    stepNumber: Int,
    symbol: Char,
    prevHiddenState: Double,
    config: RnnConfig,
    intervals: List<DfaInterval>,
  ): TransitionStep {
    val inputVal = if (symbol == '1') 1.0 else 0.0
    val netInput = config.inputWeight * inputVal + config.recurrentWeight * prevHiddenState + config.bias
    val currentHiddenState = tanh(netInput)
    val dfaState = mapToDfaState(currentHiddenState, intervals)

    return TransitionStep(
      stepNumber = stepNumber,
      inputSymbol = symbol,
      inputVal = inputVal,
      prevHiddenState = prevHiddenState,
      netInput = netInput,
      currentHiddenState = currentHiddenState,
      dfaState = dfaState,
    )
  }

  /**
   * Simulates the full input sequence from the given initial configuration.
   */
  fun simulate(inputSequence: String, config: RnnConfig): SimulationState {
    val intervals = calculateIntervals(config.dfaStateCount)
    val initialDfaState = mapToDfaState(config.initialHiddenState, intervals)

    val validSequence = inputSequence.filter { it == '0' || it == '1' }
    val steps = mutableListOf<TransitionStep>()
    var currentH = config.initialHiddenState

    for ((index, symbol) in validSequence.withIndex()) {
      val step = computeStep(
        stepNumber = index + 1,
        symbol = symbol,
        prevHiddenState = currentH,
        config = config,
        intervals = intervals,
      )
      steps.add(step)
      currentH = step.currentHiddenState
    }

    return SimulationState(
      config = config,
      inputSequence = validSequence,
      intervals = intervals,
      initialDfaState = initialDfaState,
      allSteps = steps,
      currentStepIndex = 0,
    )
  }

  /**
   * Validates the binary sequence entered by the user.
   */
  fun validateInputSequence(sequence: String): String? {
    if (sequence.isBlank()) {
      return "Please enter a binary sequence (e.g., 010110)."
    }
    val invalidChars = sequence.filter { it != '0' && it != '1' }.toSet()
    if (invalidChars.isNotEmpty()) {
      val chars = invalidChars.joinToString(", ") { "'$it'" }
      return "Invalid character(s) detected: $chars. Only 0 and 1 are allowed."
    }
    return null
  }
}
