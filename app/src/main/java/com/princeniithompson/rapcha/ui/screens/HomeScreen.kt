package com.princeniithompson.rapcha.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * v25 lean Home: a scrollable status page. Shows whether the bubble can work right now
 * (bubble switch, overlay, accessibility, API key, Keep Awake) and how to use it.
 * No fixed heights, so text wraps at any font size.
 */
@Composable
fun HomeScreen(
    isBubbleEnabled: Boolean,
    hasOverlayPermission: Boolean,
    isAccessibilityConnected: Boolean,
    apiKeyConfigured: Boolean,
    keepAwakeNeedsFix: Boolean,
    onOpenMenu: () -> Unit,
    onBubbleToggle: (Boolean) -> Unit,
    onGrantOverlay: () -> Unit,
    onTurnOnAccessibility: () -> Unit,
    onOpenKeepAwake: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ready = isBubbleEnabled && hasOverlayPermission && isAccessibilityConnected && apiKeyConfigured
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("home_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onOpenMenu,
                modifier = Modifier.testTag("home_menu_button")
            ) {
                Icon(imageVector = Icons.Filled.Menu, contentDescription = "Open settings")
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Rapcha",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (ready) Color(0xFF16A34A).copy(alpha = 0.10f)
                else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = if (ready) "Ready to dictate" else "Finish setup",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag("home_ready_title")
                )
                Text(
                    text = if (ready) {
                        "Tap the floating bubble in any app, speak, then tap the lifebuoy to type."
                    } else {
                        "Rapcha needs the items below before the bubble can type for you."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Floating Voice Ring",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (isBubbleEnabled) "On" else "Off",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Switch(
                        checked = isBubbleEnabled,
                        onCheckedChange = onBubbleToggle,
                        modifier = Modifier.testTag("home_bubble_switch")
                    )
                }
                StatusLine(
                    title = "Draw over other apps",
                    ok = hasOverlayPermission,
                    actionLabel = "Grant",
                    onAction = onGrantOverlay
                )
                StatusLine(
                    title = "Keyboard & field detector",
                    ok = isAccessibilityConnected,
                    actionLabel = "Turn on",
                    onAction = onTurnOnAccessibility
                )
                StatusLine(
                    title = "Gemini API key",
                    ok = apiKeyConfigured,
                    actionLabel = "Add key",
                    onAction = onOpenMenu
                )
                StatusLine(
                    title = "Keep Rapcha awake",
                    ok = !keepAwakeNeedsFix,
                    actionLabel = "Fix",
                    onAction = onOpenKeepAwake
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "How to use",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                HowToStep(1, "Tap a text box in any app.")
                HowToStep(2, "Tap the floating bubble and speak normally. Quiet speech is fine.")
                HowToStep(3, "Tap the lifebuoy to type your words. Tap X to cancel.")
                HowToStep(4, "No text box focused? Your words are copied to the clipboard.")
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun StatusLine(
    title: String,
    ok: Boolean,
    actionLabel: String,
    onAction: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (ok) Icons.Filled.CheckCircle else Icons.Filled.ErrorOutline,
            contentDescription = if (ok) "Done" else "Needed",
            tint = if (ok) Color(0xFF16A34A) else MaterialTheme.colorScheme.error,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        if (!ok) {
            Spacer(modifier = Modifier.width(8.dp))
            FilledTonalButton(onClick = onAction) {
                Text(actionLabel, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun HowToStep(number: Int, text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 8.dp, vertical = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$number",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
    }
}
