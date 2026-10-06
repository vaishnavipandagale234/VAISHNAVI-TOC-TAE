package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AppHeader(modifier: Modifier = Modifier) {
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
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        // High-Visibility Dedicated Neural Network Icon Box
        Box(
          modifier = Modifier
            .size(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
              Brush.radialGradient(
                colors = listOf(TealGlow, DeepNavy)
              )
            )
            .border(1.5.dp, PrimaryTeal, RoundedCornerShape(14.dp))
            .semantics { contentDescription = "RNN Neural Network Icon" },
          contentAlignment = Alignment.Center
        ) {
          Icon(
            painter = painterResource(id = R.drawable.ic_neural_network_logo),
            contentDescription = null,
            tint = androidx.compose.ui.graphics.Color.Unspecified, // Keep multi-color vector
            modifier = Modifier.size(34.dp)
          )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "RNN State Traversal Tracker",
            style = MaterialTheme.typography.titleLarge.copy(
              fontSize = 19.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.2.sp
            ),
            color = MainText
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "Visualizing Continuous RNN States as Discrete DFA States",
            style = MaterialTheme.typography.bodySmall.copy(
              fontSize = 12.sp,
              lineHeight = 16.sp
            ),
            color = SecondaryText
          )
        }
      }

      // Diagnostic badges using FlowRow to prevent any horizontal overflow on small screens
      FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        BadgeTag(text = "Theory of Computation", color = SoftBlue)
        BadgeTag(text = "h(t) = tanh(Wx·x + Wh·h + b)", color = PrimaryTeal)
        BadgeTag(text = "DFA Projection", color = HighlightViolet)
      }
    }
  }
}

@Composable
private fun BadgeTag(text: String, color: androidx.compose.ui.graphics.Color) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(6.dp))
      .background(color.copy(alpha = 0.14f))
      .border(0.8.dp, color.copy(alpha = 0.45f), RoundedCornerShape(6.dp))
      .padding(horizontal = 9.dp, vertical = 4.dp)
  ) {
    Text(
      text = text,
      color = color,
      style = MaterialTheme.typography.labelSmall.copy(
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold
      )
    )
  }
}
