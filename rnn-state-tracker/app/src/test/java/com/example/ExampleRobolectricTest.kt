package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.engine.RnnSimulator
import com.example.model.RnnConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.math.abs
import kotlin.math.tanh

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("RNN State Tracker", appName)
  }

  @Test
  fun `dfa interval partitioning creates equal intervals covering full range`() {
    val intervals3 = RnnSimulator.calculateIntervals(3)
    assertEquals(3, intervals3.size)
    assertEquals("State A", intervals3[0].label)
    assertEquals("State B", intervals3[1].label)
    assertEquals("State C", intervals3[2].label)

    assertEquals(-1.0, intervals3[0].lower, 1e-6)
    assertEquals(-1.0 + 2.0 / 3.0, intervals3[0].upper, 1e-6)
    assertEquals(-1.0 + 2.0 / 3.0, intervals3[1].lower, 1e-6)
    assertEquals(-1.0 + 4.0 / 3.0, intervals3[1].upper, 1e-6)
    assertEquals(1.0, intervals3[2].upper, 1e-6)
    assertTrue(intervals3[2].isLast)
  }

  @Test
  fun `dfa state mapping handles boundaries consistently`() {
    val intervals = RnnSimulator.calculateIntervals(3)

    // Lower bound & below
    assertEquals(0, RnnSimulator.mapToDfaState(-1.0, intervals).index)
    assertEquals(0, RnnSimulator.mapToDfaState(-1.5, intervals).index)

    // Center state (h = 0.0) in 3 intervals should be State B
    assertEquals(1, RnnSimulator.mapToDfaState(0.0, intervals).index)

    // Upper bound & above
    assertEquals(2, RnnSimulator.mapToDfaState(1.0, intervals).index)
    assertEquals(2, RnnSimulator.mapToDfaState(1.2, intervals).index)
  }

  @Test
  fun `rnn equation calculates exact mathematical hidden states`() {
    val config = RnnConfig(
      inputWeight = 0.8,
      recurrentWeight = 0.6,
      bias = -0.1,
      initialHiddenState = 0.0,
      dfaStateCount = 3
    )
    val intervals = RnnSimulator.calculateIntervals(3)

    // Step 1: symbol = '1', prevH = 0.0
    // net = 0.8 * 1.0 + 0.6 * 0.0 - 0.1 = 0.700
    // expected h1 = tanh(0.700)
    val step1 = RnnSimulator.computeStep(
      stepNumber = 1,
      symbol = '1',
      prevHiddenState = 0.0,
      config = config,
      intervals = intervals
    )
    val expectedH1 = tanh(0.7)
    assertEquals(expectedH1, step1.currentHiddenState, 1e-6)
    assertEquals(0.7, step1.netInput, 1e-6)

    // Step 2: symbol = '0', prevH = expectedH1
    // net = 0.8 * 0.0 + 0.6 * expectedH1 - 0.1
    val expectedNet2 = 0.6 * expectedH1 - 0.1
    val expectedH2 = tanh(expectedNet2)
    val step2 = RnnSimulator.computeStep(
      stepNumber = 2,
      symbol = '0',
      prevHiddenState = expectedH1,
      config = config,
      intervals = intervals
    )
    assertEquals(expectedH2, step2.currentHiddenState, 1e-6)
  }

  @Test
  fun `input sequence validation rejects invalid characters`() {
    assertNull(RnnSimulator.validateInputSequence("010110"))
    assertNull(RnnSimulator.validateInputSequence("1"))
    assertNull(RnnSimulator.validateInputSequence("000"))

    assertNotNull(RnnSimulator.validateInputSequence(""))
    assertNotNull(RnnSimulator.validateInputSequence("   "))
    assertNotNull(RnnSimulator.validateInputSequence("010210"))
    assertNotNull(RnnSimulator.validateInputSequence("abc"))
  }

  @Test
  fun `full simulation produces correct steps sequence`() {
    val config = RnnConfig(initialHiddenState = 0.0, dfaStateCount = 3)
    val state = RnnSimulator.simulate("0101", config)

    assertEquals(4, state.allSteps.size)
    assertEquals(4, state.totalSteps)
    assertEquals(0, state.currentStepIndex)
    assertEquals("State B", state.initialDfaState.label)
  }
}
