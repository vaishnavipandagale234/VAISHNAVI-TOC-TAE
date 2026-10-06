package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SimulationState
import com.example.ui.theme.BorderNavy
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.ElevatedNavy
import com.example.ui.theme.HighlightViolet
import com.example.ui.theme.MainText
import com.example.ui.theme.PrimaryTeal
import com.example.ui.theme.SecondaryNavy
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SoftBlue
import com.example.ui.theme.TealGlow
import java.util.Locale

@Composable
fun TransitionTable(
  simulationState: SimulationState,
  selectedStep: Int?,
  onStepSelected: (Int) -> Unit,
  modifier: Modifier = Modifier,
) {
  val horizontalScroll = rememberScrollState()

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
              .background(SoftBlue.copy(alpha = 0.15f))
              .border(1.dp, SoftBlue.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.TableChart,
              contentDescription = "Transition Table",
              tint = SoftBlue,
              modifier = Modifier.size(20.dp)
            )
          }

          Text(
            text = "DFA Transition Table",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 17.sp
            ),
            color = MainText
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(ElevatedNavy)
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = "Swipe table ⇄",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = SecondaryText
          )
        }
      }

      // Horizontally scrollable table container
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(DeepNavy)
          .border(1.2.dp, BorderNavy, RoundedCornerShape(12.dp))
          .horizontalScroll(horizontalScroll)
          .testTag("transition_table")
      ) {
        Column(modifier = Modifier.width(560.dp)) {
          // Table Column Headers
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(ElevatedNavy)
              .padding(vertical = 12.dp, horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            TableCell(text = "Step (t)", weight = 1f, isHeader = true, align = TextAlign.Center)
            TableCell(text = "Input x(t)", weight = 1.1f, isHeader = true, align = TextAlign.Center)
            TableCell(text = "Prev h(t-1)", weight = 1.5f, isHeader = true, align = TextAlign.End)
            TableCell(text = "Current h(t)", weight = 1.5f, isHeader = true, align = TextAlign.End)
            TableCell(text = "Mapped DFA State", weight = 2.1f, isHeader = true, align = TextAlign.Start)
          }

          Divider(color = BorderNavy, thickness = 1.2.dp)

          // Step 0: Initial State Row
          val isInitialCurrent = simulationState.currentStepIndex == 0
          val isInitialSelected = selectedStep == 0
          TableRow(
            stepLabel = "0 (init)",
            inputSymbol = "—",
            prevH = "—",
            currH = String.format(Locale.US, "%+.4f", simulationState.config.initialHiddenState),
            dfaStateLabel = "${simulationState.initialDfaState.label} ${simulationState.initialDfaState.formatRange()}",
            isCurrent = isInitialCurrent,
            isSelected = isInitialSelected,
            isExecuted = true,
            onClick = { onStepSelected(0) }
          )

          // Step 1..T Rows
          simulationState.allSteps.forEachIndexed { index, step ->
            val stepNumber = index + 1
            val isExecuted = stepNumber <= simulationState.currentStepIndex
            val isCurrent = stepNumber == simulationState.currentStepIndex
            val isSelected = selectedStep == stepNumber

            TableRow(
              stepLabel = "$stepNumber",
              inputSymbol = "${step.inputSymbol}",
              prevH = String.format(Locale.US, "%+.4f", step.prevHiddenState),
              currH = String.format(Locale.US, "%+.4f", step.currentHiddenState),
              dfaStateLabel = "${step.dfaState.label} ${step.dfaState.formatRange()}",
              isCurrent = isCurrent,
              isSelected = isSelected,
              isExecuted = isExecuted,
              onClick = { onStepSelected(stepNumber) }
            )
          }
        }
      }
    }
  }
}

@Composable
private fun TableRow(
  stepLabel: String,
  inputSymbol: String,
  prevH: String,
  currH: String,
  dfaStateLabel: String,
  isCurrent: Boolean,
  isSelected: Boolean,
  isExecuted: Boolean,
  onClick: () -> Unit,
) {
  val rowBg = when {
    isCurrent -> TealGlow
    isSelected -> SoftBlue.copy(alpha = 0.18f)
    !isExecuted -> DeepNavy.copy(alpha = 0.5f)
    else -> DeepNavy
  }

  val textColor = when {
    isCurrent -> PrimaryTeal
    !isExecuted -> SecondaryText.copy(alpha = 0.5f)
    else -> MainText
  }

  Column {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(rowBg)
        .clickable(onClick = onClick)
        .padding(vertical = 11.dp, horizontal = 14.dp)
        .then(
          if (isCurrent) Modifier.border(1.2.dp, PrimaryTeal) else Modifier
        ),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Step column with indicator
      Row(
        modifier = Modifier.weight(1f),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (isCurrent) {
          Box(
            modifier = Modifier
              .size(7.dp)
              .clip(CircleShape)
              .background(PrimaryTeal)
          )
          Spacer(modifier = Modifier.width(4.dp))
        }
        Text(
          text = stepLabel,
          style = MaterialTheme.typography.bodySmall.copy(
            fontFamily = FontFamily.Monospace,
            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium
          ),
          color = textColor,
          textAlign = TextAlign.Center
        )
      }

      // Input symbol
      TableCell(
        text = inputSymbol,
        weight = 1.1f,
        color = if (isCurrent) PrimaryTeal else if (isExecuted) HighlightViolet else Color(0xFF64748B),
        align = TextAlign.Center,
        isMono = true
      )

      // Prev h(t-1)
      TableCell(
        text = prevH,
        weight = 1.5f,
        color = if (isExecuted) SecondaryText else Color(0xFF475569),
        align = TextAlign.End,
        isMono = true
      )

      // Current h(t)
      TableCell(
        text = currH,
        weight = 1.5f,
        color = if (isCurrent) PrimaryTeal else textColor,
        align = TextAlign.End,
        isMono = true
      )

      // Mapped DFA State
      TableCell(
        text = dfaStateLabel,
        weight = 2.1f,
        color = if (isCurrent) PrimaryTeal else if (isExecuted) SoftBlue else Color(0xFF64748B),
        align = TextAlign.Start,
        isMono = true
      )
    }

    Divider(color = BorderNavy.copy(alpha = 0.6f), thickness = 0.8.dp)
  }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.TableCell(
  text: String,
  weight: Float,
  isHeader: Boolean = false,
  color: androidx.compose.ui.graphics.Color = MainText,
  align: TextAlign = TextAlign.Start,
  isMono: Boolean = false,
) {
  Text(
    text = text,
    modifier = Modifier
      .weight(weight)
      .padding(horizontal = 4.dp),
    style = if (isHeader) {
      MaterialTheme.typography.labelSmall.copy(
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp
      )
    } else {
      MaterialTheme.typography.bodySmall.copy(
        fontFamily = if (isMono) FontFamily.Monospace else FontFamily.Default,
        fontSize = 12.sp
      )
    },
    color = if (isHeader) SecondaryText else color,
    textAlign = align
  )
}
