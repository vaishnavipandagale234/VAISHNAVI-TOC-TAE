package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AppHeader
import com.example.ui.components.DfaPartitionOverview
import com.example.ui.components.HiddenStateGraph
import com.example.ui.components.InputSection
import com.example.ui.components.ResultsDisplay
import com.example.ui.components.TransitionPathVisualizer
import com.example.ui.components.TransitionTable
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.RnnViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        Scaffold(
          modifier = Modifier.fillMaxSize(),
          containerColor = DeepNavy,
        ) { innerPadding ->
          RnnTraversalScreen(
            modifier = Modifier.padding(innerPadding)
          )
        }
      }
    }
  }
}

@Composable
fun RnnTraversalScreen(
  modifier: Modifier = Modifier,
  viewModel: RnnViewModel = viewModel(),
) {
  val inputSequence by viewModel.inputSequence.collectAsStateWithLifecycle()
  val sequenceError by viewModel.sequenceError.collectAsStateWithLifecycle()
  val initialHiddenState by viewModel.initialHiddenState.collectAsStateWithLifecycle()
  val numDfaStates by viewModel.numDfaStates.collectAsStateWithLifecycle()
  val inputWeight by viewModel.inputWeight.collectAsStateWithLifecycle()
  val recurrentWeight by viewModel.recurrentWeight.collectAsStateWithLifecycle()
  val bias by viewModel.bias.collectAsStateWithLifecycle()
  val simulationState by viewModel.simulationState.collectAsStateWithLifecycle()
  val selectedStep by viewModel.selectedStep.collectAsStateWithLifecycle()

  val listState = rememberLazyListState()

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(DeepNavy),
    contentAlignment = Alignment.TopCenter
  ) {
    LazyColumn(
      state = listState,
      modifier = Modifier
        .fillMaxWidth()
        .widthIn(max = 760.dp),
      contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 48.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // 1. Application Header
      item {
        AppHeader()
      }

      // 2. Input Section & Simulation Controls
      item {
        InputSection(
          inputSequence = inputSequence,
          sequenceError = sequenceError,
          initialHiddenState = initialHiddenState,
          numDfaStates = numDfaStates,
          inputWeight = inputWeight,
          recurrentWeight = recurrentWeight,
          bias = bias,
          simulationState = simulationState,
          onSequenceChange = viewModel::onInputSequenceChange,
          onQuickSequence = viewModel::setQuickSequence,
          onInitialHiddenStateChange = viewModel::onInitialHiddenStateChange,
          onNumDfaStatesChange = viewModel::onNumDfaStatesChange,
          onInputWeightChange = viewModel::onInputWeightChange,
          onRecurrentWeightChange = viewModel::onRecurrentWeightChange,
          onBiasChange = viewModel::onBiasChange,
          onRunSimulation = viewModel::runSimulation,
          onStepForward = viewModel::stepForward,
          onReset = viewModel::resetToDefaults,
        )
      }

      // 3. Continuous-to-Discrete State Mapping Overview
      item {
        DfaPartitionOverview(
          simulationState = simulationState,
        )
      }

      // 4. State Traversal Transition Path Visualizer
      item {
        TransitionPathVisualizer(
          simulationState = simulationState,
          selectedStep = selectedStep,
          onStepSelected = viewModel::selectStep,
        )
      }

      // 5. Hidden State Continuous Trajectory Graph
      item {
        HiddenStateGraph(
          simulationState = simulationState,
          selectedStep = selectedStep,
          onStepSelected = viewModel::selectStep,
        )
      }

      // 6. DFA State Transition Table
      item {
        TransitionTable(
          simulationState = simulationState,
          selectedStep = selectedStep,
          onStepSelected = viewModel::selectStep,
        )
      }

      // 7. Results Display Summary
      item {
        ResultsDisplay(
          simulationState = simulationState,
        )
      }

      item {
        Spacer(modifier = Modifier.height(16.dp))
      }
    }
  }
}
