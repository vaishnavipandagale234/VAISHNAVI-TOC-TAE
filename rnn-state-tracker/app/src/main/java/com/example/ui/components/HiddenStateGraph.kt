package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
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
import com.example.ui.theme.TealGlow
import java.util.Locale
import kotlin.math.abs

@Composable
fun HiddenStateGraph(
  simulationState: SimulationState,
  selectedStep: Int?,
  onStepSelected: (Int) -> Unit,
  modifier: Modifier = Modifier,
) {
  // Build data points: step 0 (initial) up to executed steps
  val stepsData = remember(simulationState.currentStepIndex, simulationState.config, simulationState.allSteps) {
    val list = mutableListOf<Pair<Int, Double>>()
    list.add(0 to simulationState.config.initialHiddenState)
    simulationState.executedSteps.forEach { step ->
      list.add(step.stepNumber to step.currentHiddenState)
    }
    list
  }

  val totalSteps = simulationState.totalSteps.coerceAtLeast(1)

  // Current active step or explicitly user-selected step for display
  val inspectedStep = selectedStep ?: simulationState.currentStepIndex
  val inspectedPoint = if (inspectedStep == 0) {
    0 to simulationState.config.initialHiddenState
  } else if (inspectedStep <= simulationState.allSteps.size && inspectedStep > 0) {
    val step = simulationState.allSteps[inspectedStep - 1]
    step.stepNumber to step.currentHiddenState
  } else {
    null
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
              imageVector = Icons.Default.ShowChart,
              contentDescription = "Hidden State Graph",
              tint = PrimaryTeal,
              modifier = Modifier.size(20.dp)
            )
          }

          Text(
            text = "Continuous Hidden State Graph",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 17.sp
            ),
            color = MainText
          )
        }

        Text(
          text = "Tap point to inspect",
          style = MaterialTheme.typography.labelSmall,
          color = SecondaryText
        )
      }

      // Graph Canvas
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(240.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(DeepNavy)
          .border(1.2.dp, BorderNavy, RoundedCornerShape(12.dp))
          .testTag("hidden_state_graph_canvas")
      ) {
        Canvas(
          modifier = Modifier
            .matchParentSize()
            .pointerInput(stepsData, totalSteps) {
              detectTapGestures { offset ->
                val padLeft = 48.dp.toPx()
                val padRight = 48.dp.toPx()
                val plotWidth = size.width - padLeft - padRight
                if (plotWidth > 0 && stepsData.isNotEmpty()) {
                  val relativeX = (offset.x - padLeft).coerceIn(0f, plotWidth)
                  val stepRatio = relativeX / plotWidth
                  val stepEstimate = (stepRatio * totalSteps).toInt()
                  val closestStep = stepsData.minByOrNull { abs(it.first - stepEstimate) }?.first
                  if (closestStep != null) {
                    onStepSelected(closestStep)
                  }
                }
              }
            }
        ) {
          val padLeft = 48.dp.toPx()
          val padRight = 48.dp.toPx()
          val padTop = 24.dp.toPx()
          val padBottom = 28.dp.toPx()

          val plotWidth = size.width - padLeft - padRight
          val plotHeight = size.height - padTop - padBottom

          fun mapX(step: Int): Float {
            return padLeft + (step.toFloat() / totalSteps.toFloat()) * plotWidth
          }

          fun mapY(h: Double): Float {
            // h in [-1.0, 1.0] -> y from (padTop + plotHeight) to padTop
            val norm = ((h - (-1.0)) / 2.0).coerceIn(0.0, 1.0)
            return (padTop + plotHeight * (1.0 - norm)).toFloat()
          }

          val tickTextSize = 11.sp.toPx()
          val labelTextSize = 12.sp.toPx()

          // 1. Draw DFA Interval Boundary horizontal dashed lines & background bands
          val dashedEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)

          simulationState.intervals.forEach { interval ->
            val yBound = mapY(interval.upper)
            if (interval.index < simulationState.intervals.size - 1) {
              drawLine(
                color = Color(0xFF475569),
                start = Offset(padLeft, yBound),
                end = Offset(padLeft + plotWidth, yBound),
                strokeWidth = 1.2f,
                pathEffect = dashedEffect
              )
            }

            // Draw DFA state label on right axis with clear text size
            val midY = mapY((interval.lower + interval.upper) / 2.0)
            drawContext.canvas.nativeCanvas.apply {
              val paint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#94A3B8")
                textSize = labelTextSize
                textAlign = android.graphics.Paint.Align.LEFT
                isAntiAlias = true
                typeface = android.graphics.Typeface.DEFAULT_BOLD
              }
              drawText(interval.shortLabel, padLeft + plotWidth + 8.dp.toPx(), midY + 4.dp.toPx(), paint)
            }
          }

          // 2. Zero axis baseline (h = 0)
          val zeroY = mapY(0.0)
          drawLine(
            color = Color(0xFF475569),
            start = Offset(padLeft, zeroY),
            end = Offset(padLeft + plotWidth, zeroY),
            strokeWidth = 1.5f
          )

          // 3. Y-axis tick marks & labels
          val yTicks = listOf(1.0, 0.0, -1.0)
          yTicks.forEach { yVal ->
            val py = mapY(yVal)
            drawContext.canvas.nativeCanvas.apply {
              val paint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#94A3B8")
                textSize = tickTextSize
                textAlign = android.graphics.Paint.Align.RIGHT
                isAntiAlias = true
                typeface = android.graphics.Typeface.MONOSPACE
              }
              val text = String.format(Locale.US, "%+.1f", yVal)
              drawText(text, padLeft - 6.dp.toPx(), py + 4.dp.toPx(), paint)
            }
          }

          // 4. X-axis tick marks & step labels (t = 0, 1, 2...)
          for (t in 0..totalSteps) {
            val px = mapX(t)
            drawLine(
              color = Color(0xFF334155),
              start = Offset(px, padTop),
              end = Offset(px, padTop + plotHeight),
              strokeWidth = 0.8f,
              pathEffect = dashedEffect
            )

            drawContext.canvas.nativeCanvas.apply {
              val paint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#94A3B8")
                textSize = tickTextSize
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true
                typeface = android.graphics.Typeface.MONOSPACE
              }
              drawText("$t", px, padTop + plotHeight + 18.dp.toPx(), paint)
            }
          }

          // 5. Vertical guideline for currently inspected step
          if (inspectedPoint != null) {
            val guideX = mapX(inspectedPoint.first)
            drawLine(
              color = SoftBlue.copy(alpha = 0.8f),
              start = Offset(guideX, padTop),
              end = Offset(guideX, padTop + plotHeight),
              strokeWidth = 2f
            )
          }

          // 6. Plot continuous trajectory line connecting processed steps
          if (stepsData.size > 1) {
            val linePath = Path()
            stepsData.forEachIndexed { idx, pair ->
              val px = mapX(pair.first)
              val py = mapY(pair.second)
              if (idx == 0) linePath.moveTo(px, py) else linePath.lineTo(px, py)
            }

            // Glow stroke
            drawPath(
              path = linePath,
              color = PrimaryTeal.copy(alpha = 0.3f),
              style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // Sharp main stroke
            drawPath(
              path = linePath,
              color = PrimaryTeal,
              style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
          }

          // 7. Draw point markers
          stepsData.forEach { (step, hVal) ->
            val px = mapX(step)
            val py = mapY(hVal)

            val isSelected = inspectedStep == step
            val isCurrent = simulationState.currentStepIndex == step
            val isInitial = step == 0

            if (isSelected || isCurrent) {
              // Pulse outer glow
              drawCircle(
                color = PrimaryTeal.copy(alpha = 0.4f),
                radius = 14.dp.toPx(),
                center = Offset(px, py)
              )
            }

            // Outer ring
            drawCircle(
              color = if (isInitial) HighlightViolet else PrimaryTeal,
              radius = if (isSelected) 7.dp.toPx() else 5.dp.toPx(),
              center = Offset(px, py)
            )

            // Inner center dot
            drawCircle(
              color = if (isInitial) DeepNavy else MainText,
              radius = if (isSelected) 4.dp.toPx() else 2.8.dp.toPx(),
              center = Offset(px, py)
            )
          }
        }
      }

      // Selected Point Details Card
      if (inspectedPoint != null) {
        val stepNum = inspectedPoint.first
        val hVal = inspectedPoint.second
        val matchingDfa = simulationState.intervals.firstOrNull {
          if (it.isLast) hVal in it.lower..it.upper else hVal >= it.lower && hVal < it.upper
        } ?: simulationState.initialDfaState

        val associatedStep = if (stepNum > 0 && stepNum <= simulationState.allSteps.size) {
          simulationState.allSteps[stepNum - 1]
        } else null

        Surface(
          color = ElevatedNavy,
          shape = RoundedCornerShape(12.dp),
          border = androidx.compose.foundation.BorderStroke(1.2.dp, PrimaryTeal.copy(alpha = 0.6f))
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = if (stepNum == 0) "Step t = 0 (Initial Hidden State)" else "Step t = $stepNum (Input x = ${associatedStep?.inputSymbol ?: '-'})",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = SoftBlue
              )
              Text(
                text = "h($stepNum) = " + String.format(Locale.US, "%+.4f", hVal),
                style = MaterialTheme.typography.titleMedium.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.ExtraBold
                ),
                color = PrimaryTeal
              )
            }

            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = "DFA Projection",
                style = MaterialTheme.typography.labelSmall,
                color = SecondaryText
              )
              Text(
                text = "${matchingDfa.label} ${matchingDfa.formatRange()}",
                style = MaterialTheme.typography.titleSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold
                ),
                color = HighlightViolet
              )
            }
          }
        }
      }
    }
  }
}
