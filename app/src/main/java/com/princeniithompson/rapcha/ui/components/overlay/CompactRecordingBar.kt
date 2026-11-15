package com.princeniithompson.rapcha.ui.components.overlay

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.princeniithompson.rapcha.R
import com.princeniithompson.rapcha.service.floating.BubbleChromeGeometry
import com.princeniithompson.rapcha.service.floating.SessionClockFormat

/**
 * v25 lean compact recording bar. Static: no glow engine, no running animation,
 * no blur. Cancel X on the left, a dark pill with the timer and a short label in
 * the middle, the lifebuoy Complete on the right. Sizes come from
 * [BubbleChromeGeometry]; the overlay host pins fontScale to 1 so the pill keeps
 * its shape at any system font size.
 */
@Composable
fun CompactRecordingBar(
    elapsedSeconds: Int,
    label: String,
    processing: Boolean = false,
    onCancel: () -> Unit,
    onComplete: () -> Unit,
    onDragStart: () -> Unit = {},
    onDrag: (Float, Float) -> Unit = { _, _ -> },
    onDragEnd: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val cancelDiameter = BubbleChromeGeometry.CANCEL_DP.dp
    val pillWidth = BubbleChromeGeometry.PILL_WIDTH_DP.dp
    val pillHeight = BubbleChromeGeometry.PILL_HEIGHT_DP.dp
    val completeDiameter = BubbleChromeGeometry.COMPLETE_DP.dp
    val gap = BubbleChromeGeometry.GAP_DP.dp
    val pillShape = RoundedCornerShape(percent = 50)
    val noPadding = PlatformTextStyle(includeFontPadding = false)

    Row(
        modifier = modifier
            .wrapContentSize()
            .padding(BubbleChromeGeometry.WINDOW_PADDING_DP.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(cancelDiameter)
                .shadow(elevation = 3.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(Color.White)
                .border(width = 1.dp, color = Color.Black.copy(alpha = 0.16f), shape = CircleShape)
                .clickable(onClick = onCancel)
                .testTag("compact_bar_cancel_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Cancel",
                tint = Color(0xFF1B1B1F),
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(gap))

        Box(
            modifier = Modifier
                .size(width = pillWidth, height = pillHeight)
                .shadow(elevation = 3.dp, shape = pillShape)
                .clip(pillShape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF2A2633), Color(0xFF1E1B26))
                    )
                )
                .border(width = 1.dp, color = Color.White.copy(alpha = 0.18f), shape = pillShape)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { onDragStart() },
                        onDragEnd = { onDragEnd() },
                        onDragCancel = { onDragEnd() }
                    ) { change, drag ->
                        change.consume()
                        onDrag(drag.x, drag.y)
                    }
                }
                .testTag("compact_bar_pill"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (processing) "..." else SessionClockFormat.format(elapsedSeconds),
                    color = Color.White,
                    fontSize = 14.5.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    style = TextStyle(platformStyle = noPadding)
                )
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = label,
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 9.sp,
                    lineHeight = 10.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    style = TextStyle(platformStyle = noPadding)
                )
            }
        }

        Spacer(modifier = Modifier.width(gap))

        Box(
            modifier = Modifier
                .size(completeDiameter)
                .clip(CircleShape)
                .clickable(onClick = onComplete)
                .testTag("compact_bar_complete_button"),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_lifebuoy_ring),
                contentDescription = "Complete",
                modifier = Modifier.size(completeDiameter)
            )
        }
    }
}
