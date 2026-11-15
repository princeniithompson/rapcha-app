package com.princeniithompson.rapcha.service.floating

/**
 * v25 lean: the bubble is always the light, static compact pill.
 * Compact sizes are taken from the phone screenshot at about 2.6 px/dp.
 * Cancel 48dp, pill 90x48dp, Complete 48dp, gaps 5dp.
 */
object BubbleChromeGeometry {
    const val EDGE_MARGIN_DP = 20
    const val WINDOW_PADDING_DP = 12
    const val CIRCLE_SIZE_DP = 48
    const val CANCEL_DP = 48
    const val PILL_WIDTH_DP = 90
    const val PILL_HEIGHT_DP = 48
    const val COMPLETE_DP = 48
    const val GAP_DP = 5
    const val TOTAL_COMPACT_WIDTH_DP = 48 + 5 + 90 + 5 + 48 // 196
    const val IDLE_WINDOW_SIZE_DP = 48 + 2 * WINDOW_PADDING_DP // 72
    const val RECORDING_WINDOW_WIDTH_DP = 196 + 2 * WINDOW_PADDING_DP // 220
    const val RECORDING_WINDOW_HEIGHT_DP = 48 + 2 * WINDOW_PADDING_DP // 72
    const val SESSION_CAP_SECONDS = 600
}

/**
 * Expanded full-width chrome stays pinned to x = 0.
 * A compact window, even while the session is expanded, can move on both axes.
 */
object OverlayDragMath {
    fun nextX(
        currentX: Int,
        dx: Int,
        screenWidth: Int,
        windowWidth: Int,
        lockHorizontal: Boolean
    ): Int {
        if (lockHorizontal) return 0
        val maxX = (screenWidth - windowWidth).coerceAtLeast(0)
        return (currentX + dx).coerceIn(0, maxX)
    }

    fun locksHorizontal(expanded: Boolean, matchParentWidth: Boolean): Boolean {
        return expanded && matchParentWidth
    }
}

object SessionClockFormat {
    fun format(elapsedSeconds: Int): String {
        val capped = elapsedSeconds.coerceIn(0, BubbleChromeGeometry.SESSION_CAP_SECONDS)
        val minutes = capped / 60
        val seconds = capped % 60
        return "%02d:%02d".format(minutes, seconds)
    }
}

