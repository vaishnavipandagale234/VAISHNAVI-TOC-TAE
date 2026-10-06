package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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
import com.example.ui.theme.SuccessGreen
import java.util.Locale

@Composable
fun ResultsDisplay(
  simulationState: SimulationState,
  modifier: Modifier = Modifier,
) {
  val finalStep = simulationState.executedSteps.lastOrNull()
  val finalH = finalStep?.currentHiddenState ?: simulationState.config.initialHiddenState
  val finalDfa = finalStep?.dfaState ?: simulationState.initialDfaState
  val pathScrollState = rememberScrollState()

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
              imageVector = Icons.Default.Assessment,
              contentDescription = "Results",
              tint = PrimaryTeal,
              modifier = Modifier.size(20.dp)
            )
          }

          Text(
            text = "Diagnostic Results",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 17.sp
            ),
            color = MainText
          )
        }

        if (simulationState.isCompleted) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(SuccessGreen.copy(alpha = 0.18f))
              .border(1.dp, SuccessGreen, RoundedCornerShape(6.dp))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(
                imageVector = Icons.Default.DoneAll,
                contentDescription = null,
                tint = SuccessGreen,
                modifier = Modifier.size(15.dp)
              )
              Text(
                text = "Complete",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = SuccessGreen
              )
            }
          }
        }
      }

      // Summary Card
      Surface(
        color = DeepNavy,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderNavy),
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          // Row 1: Original input sequence & Processed symbols
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1.2f)) {
              Text(
                text = "Original Input Sequence",
                style = MaterialTheme.typography.labelSmall,
                color = SecondaryText
              )
              Text(
                text = simulationState.inputSequence,
                style = MaterialTheme.typography.bodyLarge.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 2.sp
                ),
                color = MainText
              )
            }

            Column(modifier = Modifier.weight(0.8f), horizontalAlignment = Alignment.End) {
              Text(
                text = "Symbols Processed",
                style = MaterialTheme.typography.labelSmall,
                color = SecondaryText
              )
              Text(
                text = "${simulationState.currentStepIndex} / ${simulationState.totalSteps}",
                style = MaterialTheme.typography.bodyLarge.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold
                ),
                color = if (simulationState.isCompleted) SuccessGreen else SoftBlue
              )
            }
          }

          Divider(color = BorderNavy, thickness = 1.dp)

          // Row 2: Final Continuous Hidden State & Final Mapped DFA State
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Final State h(T)",
                style = MaterialTheme.typography.labelSmall,
                color = SecondaryText
              )
              Text(
                text = String.format(Locale.US, "%+.4f", finalH),
                style = MaterialTheme.typography.titleMedium.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.ExtraBold
                ),
                color = PrimaryTeal
              )
            }

            Column(modifier = Modifier.weight(1.2f), horizontalAlignment = Alignment.End) {
              Text(
                text = "Final Mapped DFA State",
                style = MaterialTheme.typography.labelSmall,
                color = SecondaryText
              )
              Text(
                text = "${finalDfa.label} ${finalDfa.formatRange()}",
                style = MaterialTheme.typography.titleSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold
                ),
                color = HighlightViolet
              )
            }
          }

          Divider(color = BorderNavy, thickness = 1.dp)

          // Row 3: Complete Sequence of Mapped DFA States (with horizontal scroll for long paths)
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "Complete DFA Traversal Path:",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = SecondaryText
              )
              Text(
                text = "Swipe path ➔",
                style = MaterialTheme.typography.labelSmall,
                color = SecondaryText
              )
            }

            Surface(
              color = ElevatedNavy,
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(pathScrollState)
            ) {
              Text(
                text = simulationState.dfaSequenceString,
                style = MaterialTheme.typography.bodyMedium.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 1.5.sp
                ),
                color = PrimaryTeal,
                modifier = Modifier
                  .padding(10.dp)
                  .testTag("dfa_sequence_result")
              )
            }
          }
        }
      }

      // Theory of Computation Note
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(ElevatedNavy)
          .padding(12.dp)
      ) {
        Text(
          text = "Theory of Computation Note: Equivalence partitioning maps infinite continuous RNN hidden-state trajectories h(t) into finite discrete DFA states Q = {${simulationState.intervals.joinToString(", ") { it.shortLabel }}}. This demonstrates how continuous dynamical systems model discrete language acceptors.",
          style = MaterialTheme.typography.bodySmall.copy(
            fontSize = 11.5.sp,
            lineHeight = 16.sp
          ),
          color = SecondaryText
        )
      }
    }
  }
}
