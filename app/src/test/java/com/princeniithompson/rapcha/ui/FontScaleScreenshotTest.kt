package com.princeniithompson.rapcha.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.Density
import com.github.takahirom.roborazzi.captureRoboImage
import com.princeniithompson.rapcha.service.floating.FloatingOverlayContent
import com.princeniithompson.rapcha.ui.screens.HomeScreen
import com.princeniithompson.rapcha.ui.theme.MyApplicationTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * v25: Settings and Home at font scale 1.0, 1.6 and 2.0 (Android "huge" text). Every row must
 * still be reachable by scrolling, and the overlay pill must not grow with the font scale.
 * Images go to /workspace/rapcha-v25-shots when run with -Proborazzi.test.record=true.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33], qualifiers = "w360dp-h800dp-xhdpi")
class FontScaleScreenshotTest {
    @get:Rule
    val rule = createComposeRule()

    private val shots = System.getProperty("rapcha.shots.dir") ?: "/workspace/rapcha-v25-shots"

    private fun show(fontScale: Float, content: @Composable () -> Unit) {
        rule.setContent {
            val base = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(base.density, fontScale)) {
                MyApplicationTheme(darkTheme = false, dynamicColor = false) {
                    Surface(modifier = Modifier.fillMaxSize()) { content() }
                }
            }
        }
    }

    private fun settings(fontScale: Float) {
        show(fontScale) {
            SettingsContent(
                apiKey = "",
                apiKeyConfigured = true,
                signedInEmail = "",
                isProvisioning = false,
                vocabularyCount = 3,
                dictionarySelected = false,
                keepAwakeNeedsFix = true,
                isBubbleEnabled = true,
                hasOverlayPermission = true,
                isAccessibilityConnected = false,
                onSaveApiKey = {},
                onAutoGetWithGoogle = {},
                onOpenWelcomeTour = {},
                onOpenDictionary = {},
                onOpenKeepAwake = {},
                onBubbleToggle = {},
                onGrantOverlay = {},
                onTurnOnAccessibility = {}
            )
        }
    }

    private fun home(fontScale: Float) {
        show(fontScale) {
            HomeScreen(
                isBubbleEnabled = true,
                hasOverlayPermission = true,
                isAccessibilityConnected = false,
                apiKeyConfigured = true,
                keepAwakeNeedsFix = true,
                onOpenMenu = {},
                onBubbleToggle = {},
                onGrantOverlay = {},
                onTurnOnAccessibility = {},
                onOpenKeepAwake = {}
            )
        }
    }

    @Test
    fun settings_font_1_0() {
        settings(1.0f)
        rule.onRoot().captureRoboImage("$shots/settings_font_1.0.png")
        rule.onNodeWithTag("floating_bubble_switch").performScrollTo().assertIsDisplayed()
        rule.onRoot().captureRoboImage("$shots/settings_font_1.0_scrolled.png")
    }

    @Test
    fun settings_font_1_6() {
        settings(1.6f)
        rule.onRoot().captureRoboImage("$shots/settings_font_1.6.png")
        rule.onNodeWithTag("floating_bubble_switch").performScrollTo().assertIsDisplayed()
        rule.onNodeWithText("Keyboard & Field Detector", substring = true).performScrollTo().assertIsDisplayed()
        rule.onRoot().captureRoboImage("$shots/settings_font_1.6_scrolled.png")
    }

    @Test
    fun settings_font_2_0() {
        settings(2.0f)
        rule.onNodeWithText("Keyboard & Field Detector", substring = true).performScrollTo().assertIsDisplayed()
        rule.onRoot().captureRoboImage("$shots/settings_font_2.0_scrolled.png")
    }

    @Test
    fun home_font_1_0() {
        home(1.0f)
        rule.onNodeWithTag("home_ready_title").assertIsDisplayed()
        rule.onRoot().captureRoboImage("$shots/home_font_1.0.png")
    }

    @Test
    fun home_font_1_6() {
        home(1.6f)
        rule.onNodeWithTag("home_ready_title").assertIsDisplayed()
        rule.onRoot().captureRoboImage("$shots/home_font_1.6.png")
        rule.onNodeWithTag("home_bubble_switch").performScrollTo().assertIsDisplayed()
        rule.onRoot().captureRoboImage("$shots/home_font_1.6_scrolled.png")
    }

    @Test
    fun home_font_2_0() {
        home(2.0f)
        rule.onNodeWithTag("home_bubble_switch").performScrollTo().assertIsDisplayed()
        rule.onRoot().captureRoboImage("$shots/home_font_2.0_scrolled.png")
    }

    @Test
    fun overlay_pill_ignores_font_scale_2_0() {
        show(2.0f) {
            Box {
                FloatingOverlayContent(
                    recording = true,
                    pendingFinalizing = false,
                    isExpanded = true,
                    isShrunk = false,
                    isSnappedToRight = false,
                    elapsedSeconds = 84,
                    onRingClick = {},
                    onCancelClick = {},
                    onCompleteClick = {},
                    onLongPressBubble = {},
                    onDragStart = {},
                    onDrag = { _, _ -> },
                    onDragEnd = {}
                )
            }
        }
        rule.onNodeWithText("01:24").assertIsDisplayed()
        rule.onNodeWithTag("compact_bar_complete_button").assertIsDisplayed()
        rule.onRoot().captureRoboImage("$shots/overlay_pill_font_2.0.png")
    }
}
