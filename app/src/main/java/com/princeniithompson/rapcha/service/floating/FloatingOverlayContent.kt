package com.princeniithompson.rapcha.service.floating

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.princeniithompson.rapcha.service.A11yRepairKind
import com.princeniithompson.rapcha.service.FloatingBubbleManager
import com.princeniithompson.rapcha.ui.components.overlay.CompactRecordingBar
import com.princeniithompson.rapcha.ui.components.overlay.FloatingCollapsedBubble
import com.princeniithompson.rapcha.ui.theme.MyApplicationTheme

/** Label shown under the timer in the compact pill. */
internal fun compactPillLabel(finishing: Boolean, networkProblem: Boolean): String = when {
    finishing -> "Finishing"
    networkProblem -> "Offline"
    else -> "Listening"
}

/**
 * v25 lean overlay tree: the static compact recording bar while a session is open,
 * otherwise the collapsed lifebuoy bubble. The window has a fixed size in dp, so the
 * system font scale is pinned to 1 here; the pill keeps its shape at any font size.
 */
@Composable
fun FloatingOverlayContent(
    recording: Boolean,
    pendingFinalizing: Boolean,
    isExpanded: Boolean,
    isShrunk: Boolean,
    isSnappedToRight: Boolean,
    elapsedSeconds: Int = 0,
    repairKind: A11yRepairKind = A11yRepairKind.NONE,
    onRepairClick: () -> Unit = {},
    onRingClick: () -> Unit,
    onCancelClick: () -> Unit,
    onCompleteClick: () -> Unit,
    onLongPressBubble: () -> Unit,
    onDragStart: () -> Unit,
    onDrag: (dx: Float, dy: Float) -> Unit,
    onDragEnd: () -> Unit
) {
    val density = LocalDensity.current
    CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale = 1f)) {
        MyApplicationTheme(darkTheme = true, dynamicColor = true) {
            Box(modifier = Modifier.wrapContentSize()) {
                if (isExpanded) {
                    val problem by FloatingBubbleManager.isNetworkProblem.collectAsState()
                    CompactRecordingBar(
                        elapsedSeconds = elapsedSeconds,
                        label = compactPillLabel(pendingFinalizing, problem),
                        processing = pendingFinalizing,
                        onCancel = onCancelClick,
                        onComplete = onCompleteClick,
                        onDragStart = onDragStart,
                        onDrag = onDrag,
                        onDragEnd = onDragEnd
                    )
                } else {
                    FloatingCollapsedBubble(
                        isRecording = recording,
                        isShrunk = isShrunk,
                        isSnappedToRight = isSnappedToRight,
                        repairKind = repairKind,
                        onRepairClick = onRepairClick,
                        onClick = onRingClick,
                        onLongPress = onLongPressBubble,
                        onDragStart = onDragStart,
                        onDrag = onDrag,
                        onDragEnd = onDragEnd
                    )
                }
            }
        }
    }
}
