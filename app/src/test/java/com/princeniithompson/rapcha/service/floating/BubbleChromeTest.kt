package com.princeniithompson.rapcha.service.floating

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BubbleChromeTest {

    @Test
    fun `the clock caps at ten minutes and the compact pill keeps its geometry`() {
        assertEquals("00:00", SessionClockFormat.format(0))
        assertEquals("01:24", SessionClockFormat.format(84))
        assertEquals("10:00", SessionClockFormat.format(600))
        assertEquals("10:00", SessionClockFormat.format(601))
        assertEquals(48, BubbleChromeGeometry.CIRCLE_SIZE_DP)
        assertEquals(48, BubbleChromeGeometry.CANCEL_DP)
        assertEquals(90, BubbleChromeGeometry.PILL_WIDTH_DP)
        assertEquals(48, BubbleChromeGeometry.PILL_HEIGHT_DP)
        assertEquals(48, BubbleChromeGeometry.COMPLETE_DP)
        assertEquals(5, BubbleChromeGeometry.GAP_DP)
        assertEquals(20, BubbleChromeGeometry.EDGE_MARGIN_DP)
    }

    @Test
    fun `expanded full-width chrome locks x and a compact window can move sideways`() {
        assertTrue(OverlayDragMath.locksHorizontal(expanded = true, matchParentWidth = true))
        assertFalse(OverlayDragMath.locksHorizontal(expanded = true, matchParentWidth = false))
        assertEquals(0, OverlayDragMath.nextX(40, 30, 1080, 1080, lockHorizontal = true))
        assertEquals(70, OverlayDragMath.nextX(40, 30, 1080, 200, lockHorizontal = false))
        assertEquals(880, OverlayDragMath.nextX(860, 40, 1080, 200, lockHorizontal = false))
        assertEquals(0, OverlayDragMath.nextX(10, -40, 1080, 200, lockHorizontal = false))
    }

    @Test
    fun `compact pill label follows the session state`() {
        assertEquals("Listening", compactPillLabel(finishing = false, networkProblem = false))
        assertEquals("Offline", compactPillLabel(finishing = false, networkProblem = true))
        assertEquals("Finishing", compactPillLabel(finishing = true, networkProblem = true))
    }
}
