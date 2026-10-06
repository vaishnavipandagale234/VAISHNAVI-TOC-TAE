package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DfaInterval
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
import com.example.ui.theme.TealGlow
import java.util.Locale

@Composable
fun DfaPartitionOverview(
  simulationState: SimulationState,
  modifier: Modifier = Modifier,
) {
  val currentH = simulationState.currentHiddenState
  val currentDfa = simulationState.currentDfaState
  val visitedIndices = simulationState.visitedDfaStateIndices
  val cardsScrollState = rememberScrollState()

  // Normalized position in [0, 1] for current h in [-1, 1]
  val normalizedPos = ((currentH - (-1.0)) / 2.0).coerceIn(0.0, 1.0).toFloat()
  val animatedNeedlePos by animateFloatAsState(
    targetValue = normalizedPos,
    animationSpec = tween(durationMillis = 350),
    label = "needle_position"
  )

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
              .background(HighlightViolet.copy(alpha = 0.15f))
              .border(1.dp, HighlightViolet.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Category,
              contentDescription = "DFA Mapping",
              tint = HighlightViolet,
              modifier = Modifier.size(20.dp)
            )
          }

          Text(
            text = "Continuous [-1, 1] to DFA Mapping",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 17.sp
            ),
            color = MainText
          )
        }

        // Active State Tag
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(TealGlow)
            .border(1.2.dp, PrimaryTeal, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
          Text(
            text = "Active: ${currentDfa.label}",
            color = PrimaryTeal,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 12.sp
          )
        }
      }

      // Mathematical explanation & current value banner
      Surface(
        color = ElevatedNavy,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderNavy),
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Current Continuous State h(t)",
              style = MaterialTheme.typography.labelSmall,
              color = SecondaryText
            )
            Text(
              text = String.format(Locale.US, "%+.4f", currentH),
              style = MaterialTheme.typography.titleLarge.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
              ),
              color = PrimaryTeal
            )
          }

          Text(
            text = "➔",
            fontSize = 22.sp,
            color = SecondaryText
          )

          Column(horizontalAlignment = Alignment.End) {
            Text(
              text = "Mapped DFA Interval",
              style = MaterialTheme.typography.labelSmall,
              color = SecondaryText
            )
            Text(
              text = "${currentDfa.label} ${currentDfa.formatRange()}",
              style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
              ),
              color = HighlightViolet
            )
          }
        }
      }

      // Visual Continuum Spectrum Bar with High-Visibility Pointer Needle
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
          text = "Continuous Range Partition Spectrum:",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
          color = MainText
        )

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
        ) {
          // Track with colored segment stripes for each interval
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .height(22.dp)
              .align(Alignment.Center)
              .clip(RoundedCornerShape(11.dp))
              .background(DeepNavy)
              .border(1.2.dp, BorderNavy, RoundedCornerShape(11.dp))
          ) {
            simulationState.intervals.forEach { interval ->
              val isCurrent = interval.index == currentDfa.index
              val bg = when {
                isCurrent -> PrimaryTeal.copy(alpha = 0.5f)
                visitedIndices.contains(interval.index) -> SoftBlue.copy(alpha = 0.28f)
                else -> ElevatedNavy
              }
              Box(
                modifier = Modifier
                  .weight(1f)
                  .height(22.dp)
                  .background(bg)
                  .border(0.6.dp, BorderNavy),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = interval.shortLabel,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isCurrent) PrimaryTeal else SecondaryText
                )
              }
            }
          }

          // Distinct Needle Pointer
          BoxWithConstraints(
            modifier = Modifier
              .fillMaxWidth()
              .align(Alignment.Center)
          ) {
            val totalWidth = maxWidth
            val needleOffset = (totalWidth - 18.dp) * animatedNeedlePos

            Box(
              modifier = Modifier
                .offset(x = needleOffset)
                .size(18.dp, 36.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(PrimaryTeal)
                .border(1.5.dp, MainText, RoundedCornerShape(4.dp))
                .shadow(6.dp, shape = RoundedCornerShape(4.dp)),
              contentAlignment = Alignment.Center
            ) {
              Box(
                modifier = Modifier
                  .size(6.dp)
                  .clip(CircleShape)
                  .background(DeepNavy)
              )
            }
          }
        }

        // Labels for [-1.0, 0.0, 1.0]
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(text = "-1.0 (Min)", style = MaterialTheme.typography.labelSmall, color = SecondaryText, fontFamily = FontFamily.Monospace)
          Text(text = "0.0", style = MaterialTheme.typography.labelSmall, color = SecondaryText, fontFamily = FontFamily.Monospace)
          Text(text = "+1.0 (Max)", style = MaterialTheme.typography.labelSmall, color = SecondaryText, fontFamily = FontFamily.Monospace)
        }
      }

      // Discrete DFA State Cards - Responsive handling to prevent any squishing
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Equivalence Class Intervals (${simulationState.intervals.size} States):",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MainText
          )
          if (simulationState.intervals.size > 3) {
            Text(
              text = "Swipe cards ➔",
              style = MaterialTheme.typography.labelSmall,
              color = SecondaryText
            )
          }
        }

        // Horizontally scrollable row if more than 3 states, or equal weights if 3 or less
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .then(
              if (simulationState.intervals.size > 3) Modifier.horizontalScroll(cardsScrollState)
              else Modifier
            ),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          simulationState.intervals.forEach { interval ->
            val isCurrent = interval.index == currentDfa.index
            val isVisited = visitedIndices.contains(interval.index)

            val animatedBorderColor by animateColorAsState(
              targetValue = when {
                isCurrent -> PrimaryTeal
                isVisited -> SoftBlue.copy(alpha = 0.7f)
                else -> BorderNavy
              },
              label = "card_border"
            )

            Surface(
              modifier = Modifier
                .then(
                  if (simulationState.intervals.size > 3) Modifier.width(135.dp)
                  else Modifier.weight(1f)
                )
                .border(
                  width = if (isCurrent) 2.dp else 1.dp,
                  color = animatedBorderColor,
                  shape = RoundedCornerShape(12.dp)
                ),
              color = if (isCurrent) ElevatedNavy else DeepNavy,
              shape = RoundedCornerShape(12.dp),
            ) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                // State Label & Tag
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Text(
                    text = interval.shortLabel,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = if (isCurrent) PrimaryTeal else if (isVisited) SoftBlue else MainText
                  )
                  if (isCurrent) {
                    Box(
                      modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(PrimaryTeal)
                    )
                  }
                }

                Text(
                  text = interval.formatRange(),
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                  ),
                  color = if (isCurrent) MainText else SecondaryText
                )

                // Status chip
                val statusText = when {
                  isCurrent -> "ACTIVE"
                  isVisited -> "VISITED"
                  else -> "IDLE"
                }
                val statusColor = when {
                  isCurrent -> PrimaryTeal
                  isVisited -> SuccessGreen
                  else -> SecondaryText.copy(alpha = 0.6f)
                }

                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(statusColor.copy(alpha = 0.15f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = statusText,
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.Bold,
                      fontSize = 10.sp
                    ),
                    color = statusColor
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}
