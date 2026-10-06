package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DfaInterval
import com.example.model.SimulationState
import com.example.model.TransitionStep
import com.example.ui.theme.BorderNavy
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.ElevatedNavy
import com.example.ui.theme.HighlightViolet
import com.example.ui.theme.MainText
import com.example.ui.theme.PrimaryTeal
import com.example.ui.theme.SecondaryNavy
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SoftBlue
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TealGlow
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TransitionPathVisualizer(
  simulationState: SimulationState,
  selectedStep: Int?,
  onStepSelected: (Int) -> Unit,
  modifier: Modifier = Modifier,
) {
  val scrollState = rememberScrollState()

  // Auto-scroll to active step on step forward
  LaunchedEffect(simulationState.currentStepIndex) {
    if (simulationState.currentStepIndex > 0) {
      val targetOffset = (simulationState.currentStepIndex * 210)
      scrollState.animateScrollTo(targetOffset)
    }
  }

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
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Header
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
              imageVector = Icons.Default.AltRoute,
              contentDescription = "Traversal",
              tint = PrimaryTeal,
              modifier = Modifier.size(20.dp)
            )
          }

          Text(
            text = "State Traversal Path",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 17.sp
            ),
            color = MainText
          )
        }

        Text(
          text = "Scroll horizontally ➔",
          style = MaterialTheme.typography.labelSmall,
          color = SecondaryText
        )
      }

      // Legend of styles using FlowRow to prevent any screen overflow
      FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        LegendItem(label = "Initial State", color = HighlightViolet)
        LegendItem(label = "Current State", color = PrimaryTeal, glowing = true)
        LegendItem(label = "Visited State", color = SoftBlue)
        LegendItem(label = "Pending Step", color = Color(0xFF64748B))
      }

      // Horizontal Path of Steps
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(scrollState)
          .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Step 0: Initial State Card
        val isInitialCurrent = simulationState.currentStepIndex == 0
        InitialStateCard(
          initialH = simulationState.config.initialHiddenState,
          initialDfa = simulationState.initialDfaState,
          isCurrent = isInitialCurrent,
          isSelected = selectedStep == 0,
          onClick = { onStepSelected(0) }
        )

        // Steps 1..T
        simulationState.allSteps.forEachIndexed { idx, step ->
          val stepNumber = idx + 1
          val isStepExecuted = stepNumber <= simulationState.currentStepIndex
          val isCurrent = stepNumber == simulationState.currentStepIndex
          val isSelected = selectedStep == stepNumber

          // Connecting Transition Arrow with Input Symbol
          TransitionConnector(
            inputSymbol = step.inputSymbol,
            isActive = isStepExecuted,
            isCurrent = isCurrent
          )

          // Step Card
          StepTransitionCard(
            step = step,
            isExecuted = isStepExecuted,
            isCurrent = isCurrent,
            isSelected = isSelected,
            onClick = { onStepSelected(stepNumber) }
          )
        }
      }
    }
  }
}

@Composable
private fun LegendItem(label: String, color: Color, glowing: Boolean = false) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(6.dp)
  ) {
    Box(
      modifier = Modifier
        .size(10.dp)
        .clip(CircleShape)
        .background(color)
        .then(
          if (glowing) Modifier.border(1.5.dp, MainText, CircleShape) else Modifier
        )
    )
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall.copy(
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium
      ),
      color = MainText
    )
  }
}

@Composable
private fun InitialStateCard(
  initialH: Double,
  initialDfa: DfaInterval,
  isCurrent: Boolean,
  isSelected: Boolean,
  onClick: () -> Unit,
) {
  val borderColor by animateColorAsState(
    targetValue = when {
      isCurrent -> PrimaryTeal
      isSelected -> SoftBlue
      else -> HighlightViolet.copy(alpha = 0.7f)
    },
    label = "initial_border"
  )

  Surface(
    modifier = Modifier
      .width(160.dp)
      .clip(RoundedCornerShape(14.dp))
      .border(
        width = if (isCurrent) 2.dp else 1.2.dp,
        color = borderColor,
        shape = RoundedCornerShape(14.dp)
      )
      .clickable(onClick = onClick)
      .testTag("initial_state_card"),
    color = if (isCurrent) ElevatedNavy else DeepNavy,
    shape = RoundedCornerShape(14.dp)
  ) {
    Column(
      modifier = Modifier.padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(HighlightViolet.copy(alpha = 0.2f))
            .border(0.8.dp, HighlightViolet, RoundedCornerShape(6.dp))
            .padding(horizontal = 7.dp, vertical = 3.dp)
        ) {
          Text(
            text = "START t=0",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp
            ),
            color = HighlightViolet
          )
        }

        if (isCurrent) {
          CurrentPulseTag()
        }
      }

      Text(
        text = initialDfa.label,
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 18.sp
        ),
        color = if (isCurrent) PrimaryTeal else HighlightViolet
      )

      Column {
        Text(
          text = "Initial h(0)",
          style = MaterialTheme.typography.labelSmall,
          color = SecondaryText
        )
        Text(
          text = String.format(Locale.US, "%+.4f", initialH),
          style = MaterialTheme.typography.bodyMedium.copy(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
          ),
          color = MainText
        )
      }

      Text(
        text = initialDfa.formatRange(),
        style = MaterialTheme.typography.labelSmall.copy(
          fontFamily = FontFamily.Monospace,
          fontSize = 11.sp
        ),
        color = SecondaryText
      )
    }
  }
}

@Composable
private fun TransitionConnector(
  inputSymbol: Char,
  isActive: Boolean,
  isCurrent: Boolean,
) {
  // Clear, visible arrow & badge colors - never invisible dark navy
  val strokeColor = when {
    isCurrent -> PrimaryTeal
    isActive -> SoftBlue
    else -> Color(0xFF64748B) // Slate-500: clearly visible against deep navy
  }

  Column(
    modifier = Modifier
      .width(56.dp)
      .padding(horizontal = 2.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    // Input symbol badge
    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(6.dp))
        .background(ElevatedNavy)
        .border(1.2.dp, strokeColor, RoundedCornerShape(6.dp))
        .padding(horizontal = 7.dp, vertical = 3.dp)
    ) {
      Text(
        text = "x=$inputSymbol",
        style = MaterialTheme.typography.labelSmall.copy(
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp
        ),
        color = if (isActive) PrimaryTeal else MainText
      )
    }

    Spacer(modifier = Modifier.height(4.dp))

    // High-visibility Directional Arrow with circular accent background
    Box(
      modifier = Modifier
        .size(28.dp)
        .clip(CircleShape)
        .background(if (isActive) strokeColor.copy(alpha = 0.18f) else DeepNavy)
        .border(1.dp, strokeColor.copy(alpha = 0.6f), CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
        contentDescription = "Transitions via input $inputSymbol",
        tint = strokeColor,
        modifier = Modifier.size(18.dp)
      )
    }
  }
}

@Composable
private fun StepTransitionCard(
  step: TransitionStep,
  isExecuted: Boolean,
  isCurrent: Boolean,
  isSelected: Boolean,
  onClick: () -> Unit,
) {
  val borderColor by animateColorAsState(
    targetValue = when {
      isCurrent -> PrimaryTeal
      isSelected -> SoftBlue
      isExecuted -> BorderNavy
      else -> Color(0xFF334155).copy(alpha = 0.6f)
    },
    label = "step_border"
  )

  Surface(
    modifier = Modifier
      .width(172.dp)
      .clip(RoundedCornerShape(14.dp))
      .border(
        width = if (isCurrent) 2.dp else 1.2.dp,
        color = borderColor,
        shape = RoundedCornerShape(14.dp)
      )
      .clickable(onClick = onClick)
      .testTag("step_card_${step.stepNumber}"),
    color = when {
      isCurrent -> ElevatedNavy
      isExecuted -> DeepNavy
      else -> DeepNavy.copy(alpha = 0.6f)
    },
    shape = RoundedCornerShape(14.dp)
  ) {
    Column(
      modifier = Modifier.padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      // Top row: Step number & Current tag
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Icon(
            imageVector = when {
              isCurrent -> Icons.Default.PlayCircleFilled
              isExecuted -> Icons.Default.CheckCircle
              else -> Icons.Default.RadioButtonUnchecked
            },
            contentDescription = null,
            tint = when {
              isCurrent -> PrimaryTeal
              isExecuted -> SuccessGreen
              else -> Color(0xFF64748B)
            },
            modifier = Modifier.size(14.dp)
          )

          Text(
            text = "Step t = ${step.stepNumber}",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            ),
            color = if (isExecuted) SoftBlue else SecondaryText
          )
        }

        if (isCurrent) {
          CurrentPulseTag()
        }
      }

      // Mapped DFA State
      Text(
        text = step.dfaState.label,
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 18.sp
        ),
        color = when {
          isCurrent -> PrimaryTeal
          isExecuted -> MainText
          else -> SecondaryText
        }
      )

      // Value comparisons: h(t-1) -> h(t)
      Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "h(t-1):",
            style = MaterialTheme.typography.labelSmall,
            color = SecondaryText
          )
          Text(
            text = String.format(Locale.US, "%+.4f", step.prevHiddenState),
            style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
            color = if (isExecuted) MainText else SecondaryText
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "h(t):",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = if (isCurrent) PrimaryTeal else SecondaryText
          )
          Text(
            text = String.format(Locale.US, "%+.4f", step.currentHiddenState),
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold
            ),
            color = if (isCurrent) PrimaryTeal else if (isExecuted) MainText else SecondaryText
          )
        }
      }

      // Interval range
      Text(
        text = step.dfaState.formatRange(),
        style = MaterialTheme.typography.labelSmall.copy(
          fontFamily = FontFamily.Monospace,
          fontSize = 11.sp
        ),
        color = SecondaryText
      )
    }
  }
}

@Composable
private fun CurrentPulseTag() {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(4.dp))
      .background(TealGlow)
      .border(1.dp, PrimaryTeal, RoundedCornerShape(4.dp))
      .padding(horizontal = 6.dp, vertical = 2.dp)
  ) {
    Text(
      text = "ACTIVE",
      style = MaterialTheme.typography.labelSmall.copy(
        fontWeight = FontWeight.ExtraBold,
        fontSize = 9.sp,
        letterSpacing = 0.5.sp
      ),
      color = PrimaryTeal
    )
  }
}
