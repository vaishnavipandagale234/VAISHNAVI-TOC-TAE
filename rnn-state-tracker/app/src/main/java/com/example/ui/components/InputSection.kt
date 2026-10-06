package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SimulationState
import com.example.ui.theme.BorderNavy
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.ElevatedNavy
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.MainText
import com.example.ui.theme.PrimaryTeal
import com.example.ui.theme.SecondaryNavy
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SoftBlue

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InputSection(
  inputSequence: String,
  sequenceError: String?,
  initialHiddenState: String,
  numDfaStates: Int,
  inputWeight: String,
  recurrentWeight: String,
  bias: String,
  simulationState: SimulationState,
  onSequenceChange: (String) -> Unit,
  onQuickSequence: (String) -> Unit,
  onInitialHiddenStateChange: (String) -> Unit,
  onNumDfaStatesChange: (Int) -> Unit,
  onInputWeightChange: (String) -> Unit,
  onRecurrentWeightChange: (String) -> Unit,
  onBiasChange: (String) -> Unit,
  onRunSimulation: () -> Unit,
  onStepForward: () -> Unit,
  onReset: () -> Unit,
  modifier: Modifier = Modifier,
) {
  var showAdvancedParams by remember { mutableStateOf(false) }

  Surface(
    modifier = modifier.fillMaxWidth(),
    color = SecondaryNavy,
    shape = RoundedCornerShape(16.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, BorderNavy),
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Header for inputs with prominent icon
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(PrimaryTeal.copy(alpha = 0.15f))
              .border(1.dp, PrimaryTeal.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Tune,
              contentDescription = "Configuration",
              tint = PrimaryTeal,
              modifier = Modifier.size(20.dp)
            )
          }

          Text(
            text = "Simulation Inputs",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 17.sp
            ),
            color = MainText
          )
        }

        // Toggle advanced weights
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(ElevatedNavy)
            .border(1.dp, BorderNavy, RoundedCornerShape(8.dp))
            .clickable { showAdvancedParams = !showAdvancedParams }
            .padding(horizontal = 10.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "RNN Parameters",
            tint = if (showAdvancedParams) PrimaryTeal else SecondaryText,
            modifier = Modifier.size(16.dp)
          )
          Text(
            text = if (showAdvancedParams) "Hide Weights" else "RNN Weights",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = if (showAdvancedParams) PrimaryTeal else SecondaryText
          )
        }
      }

      // 1. Input Sequence
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "1. Input Binary Sequence (0s and 1s)",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MainText
          )
          Text(
            text = "${inputSequence.length} symbols",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
            color = if (inputSequence.isNotEmpty()) PrimaryTeal else SecondaryText
          )
        }

        OutlinedTextField(
          value = inputSequence,
          onValueChange = onSequenceChange,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_sequence_field"),
          placeholder = { Text("e.g. 010110", color = SecondaryText.copy(alpha = 0.5f)) },
          isError = sequenceError != null,
          singleLine = true,
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          textStyle = MaterialTheme.typography.bodyLarge.copy(
            fontFamily = FontFamily.Monospace,
            letterSpacing = 2.5.sp,
            fontWeight = FontWeight.Bold,
            color = MainText
          ),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryTeal,
            unfocusedBorderColor = BorderNavy,
            errorBorderColor = ErrorRed,
            focusedContainerColor = ElevatedNavy,
            unfocusedContainerColor = ElevatedNavy,
          ),
          shape = RoundedCornerShape(10.dp)
        )

        // Error message if invalid
        AnimatedVisibility(
          visible = sequenceError != null,
          enter = fadeIn() + expandVertically(),
          exit = fadeOut() + shrinkVertically()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(ErrorRed.copy(alpha = 0.15f))
              .border(1.dp, ErrorRed.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
              .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(
              imageVector = Icons.Default.ErrorOutline,
              contentDescription = "Error",
              tint = ErrorRed,
              modifier = Modifier.size(18.dp)
            )
            Text(
              text = sequenceError.orEmpty(),
              color = ErrorRed,
              style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
              )
            )
          }
        }

        // Quick sequence preset chips
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = "Presets:",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = SecondaryText,
            modifier = Modifier.align(Alignment.CenterVertically)
          )
          listOf("010110", "11001", "101010", "001100", "111000").forEach { sample ->
            val isSelected = inputSequence == sample
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSelected) PrimaryTeal.copy(alpha = 0.22f) else ElevatedNavy)
                .border(
                  1.dp,
                  if (isSelected) PrimaryTeal else BorderNavy,
                  RoundedCornerShape(8.dp)
                )
                .clickable { onQuickSequence(sample) }
                .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Text(
                text = sample,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) PrimaryTeal else SecondaryText
              )
            }
          }
        }
      }

      // 2. Initial Hidden State & 3. Number of DFA States (side by side, properly spaced)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Initial Hidden State h(0)
        Column(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(
            text = "2. Initial h(0) [-1, 1]",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MainText
          )
          OutlinedTextField(
            value = initialHiddenState,
            onValueChange = onInitialHiddenStateChange,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("initial_hidden_state_field"),
            singleLine = true,
            placeholder = { Text("0.0", color = SecondaryText) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            textStyle = MaterialTheme.typography.bodyMedium.copy(
              fontFamily = FontFamily.Monospace,
              color = MainText
            ),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = PrimaryTeal,
              unfocusedBorderColor = BorderNavy,
              focusedContainerColor = ElevatedNavy,
              unfocusedContainerColor = ElevatedNavy,
            ),
            shape = RoundedCornerShape(10.dp)
          )
        }

        // Number of DFA States (N)
        Column(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(
            text = "3. DFA States (N)",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MainText
          )
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .height(56.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(ElevatedNavy)
              .border(1.dp, BorderNavy, RoundedCornerShape(10.dp))
              .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            listOf(2, 3, 4, 5).forEach { count ->
              val isSelected = numDfaStates == count
              Box(
                modifier = Modifier
                  .weight(1f)
                  .height(48.dp)
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (isSelected) PrimaryTeal else DeepNavy.copy(alpha = 0.6f))
                  .clickable { onNumDfaStatesChange(count) }
                  .testTag("dfa_state_button_$count"),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "$count",
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 15.sp,
                  color = if (isSelected) DeepNavy else MainText
                )
              }
            }
          }
        }
      }

      // 4. RNN Parameters (Wx, Wh, b) - Expandable
      AnimatedVisibility(
        visible = showAdvancedParams,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ElevatedNavy)
            .border(1.dp, BorderNavy, RoundedCornerShape(12.dp))
            .padding(14.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Text(
            text = "4. RNN Weights: h(t) = tanh(Wx·x + Wh·h + b)",
            style = MaterialTheme.typography.labelMedium.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            ),
            color = SoftBlue
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // Wx
            Column(modifier = Modifier.weight(1f)) {
              Text("Input Wx", style = MaterialTheme.typography.labelSmall, color = SecondaryText)
              Spacer(modifier = Modifier.height(4.dp))
              OutlinedTextField(
                value = inputWeight,
                onValueChange = onInputWeightChange,
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("input_weight_field"),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, color = MainText),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = PrimaryTeal,
                  unfocusedBorderColor = BorderNavy,
                  focusedContainerColor = DeepNavy,
                  unfocusedContainerColor = DeepNavy,
                ),
                shape = RoundedCornerShape(8.dp)
              )
            }

            // Wh
            Column(modifier = Modifier.weight(1f)) {
              Text("Recurrent Wh", style = MaterialTheme.typography.labelSmall, color = SecondaryText)
              Spacer(modifier = Modifier.height(4.dp))
              OutlinedTextField(
                value = recurrentWeight,
                onValueChange = onRecurrentWeightChange,
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("recurrent_weight_field"),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, color = MainText),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = PrimaryTeal,
                  unfocusedBorderColor = BorderNavy,
                  focusedContainerColor = DeepNavy,
                  unfocusedContainerColor = DeepNavy,
                ),
                shape = RoundedCornerShape(8.dp)
              )
            }

            // Bias b
            Column(modifier = Modifier.weight(1f)) {
              Text("Bias b", style = MaterialTheme.typography.labelSmall, color = SecondaryText)
              Spacer(modifier = Modifier.height(4.dp))
              OutlinedTextField(
                value = bias,
                onValueChange = onBiasChange,
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("bias_field"),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, color = MainText),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = PrimaryTeal,
                  unfocusedBorderColor = BorderNavy,
                  focusedContainerColor = DeepNavy,
                  unfocusedContainerColor = DeepNavy,
                ),
                shape = RoundedCornerShape(8.dp)
              )
            }
          }
        }
      }

      // Simulation Control Buttons: Spacious 2-row layout to prevent ANY overflow or squishing!
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Row 1: Prominent Full-Width "Run Simulation" Button
        Button(
          onClick = onRunSimulation,
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("run_simulation_button"),
          colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryTeal,
            contentColor = DeepNavy
          ),
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = null,
            tint = DeepNavy,
            modifier = Modifier.size(22.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Run Simulation",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            letterSpacing = 0.5.sp
          )
        }

        // Row 2: "Step Forward" & "Reset" with equal width so both icons and text are 100% visible
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // "Step Forward"
          Button(
            onClick = onStepForward,
            enabled = simulationState.currentStepIndex < simulationState.totalSteps,
            modifier = Modifier
              .weight(1.2f)
              .height(48.dp)
              .testTag("step_forward_button"),
            colors = ButtonDefaults.buttonColors(
              containerColor = SoftBlue,
              contentColor = DeepNavy,
              disabledContainerColor = ElevatedNavy,
              disabledContentColor = SecondaryText.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowForward,
              contentDescription = null,
              tint = if (simulationState.currentStepIndex < simulationState.totalSteps) DeepNavy else SecondaryText.copy(alpha = 0.5f),
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (simulationState.isCompleted) "Completed" else "Step Forward",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
          }

          // "Reset"
          OutlinedButton(
            onClick = onReset,
            modifier = Modifier
              .weight(0.9f)
              .height(48.dp)
              .testTag("reset_button"),
            colors = ButtonDefaults.outlinedButtonColors(
              contentColor = MainText
            ),
            border = androidx.compose.foundation.BorderStroke(1.2.dp, BorderNavy),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Refresh,
              contentDescription = "Reset",
              tint = MainText,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Reset",
              fontWeight = FontWeight.SemiBold,
              fontSize = 14.sp
            )
          }
        }

        // Execution status tracker
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(ElevatedNavy)
            .padding(horizontal = 12.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Progress: Step ${simulationState.currentStepIndex} of ${simulationState.totalSteps}",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = if (simulationState.isCompleted) PrimaryTeal else MainText
          )
          Text(
            text = if (simulationState.currentStepIndex == 0) "Initial State h(0)" else if (simulationState.isCompleted) "All Steps Processed" else "Stepping...",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = if (simulationState.isCompleted) PrimaryTeal else SoftBlue
          )
        }
      }
    }
  }
}
