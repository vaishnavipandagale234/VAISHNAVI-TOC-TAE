package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val RnnDarkColorScheme = darkColorScheme(
  primary = PrimaryTeal,
  onPrimary = DeepNavy,
  primaryContainer = SecondaryNavy,
  onPrimaryContainer = PrimaryTeal,
  secondary = SoftBlue,
  onSecondary = DeepNavy,
  secondaryContainer = ElevatedNavy,
  onSecondaryContainer = SoftBlue,
  tertiary = HighlightViolet,
  onTertiary = DeepNavy,
  background = DeepNavy,
  onBackground = MainText,
  surface = SecondaryNavy,
  onSurface = MainText,
  surfaceVariant = ElevatedNavy,
  onSurfaceVariant = SecondaryText,
  outline = BorderNavy,
  error = ErrorRed,
  onError = DeepNavy,
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force professional dark theme as requested
  dynamicColor: Boolean = false, // Keep specific designated AI diagnostic colors
  content: @Composable () -> Unit,
) {
  val colorScheme = RnnDarkColorScheme

  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as? Activity)?.window
      if (window != null) {
        window.statusBarColor = DeepNavy.toArgb()
        window.navigationBarColor = DeepNavy.toArgb()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
      }
    }
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
