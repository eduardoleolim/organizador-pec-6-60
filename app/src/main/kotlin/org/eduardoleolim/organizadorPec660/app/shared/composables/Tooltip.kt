package org.eduardoleolim.organizadorPec660.app.shared.composables

import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PlainTooltipState
import androidx.compose.material3.RichTooltipState
import androidx.compose.material3.TooltipBoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay

context(TooltipBoxScope)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Modifier.tooltipOnHover(state: PlainTooltipState, timeMillis: Long = 500L): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    LaunchedEffect(isHovered) {
        if (isHovered) {
            delay(timeMillis)
            state.show()
        } else {
            state.dismiss()
        }
    }

    return hoverable(interactionSource)
}

context(TooltipBoxScope)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Modifier.tooltipOnHover(state: RichTooltipState, timeMillis: Long = 500L): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    LaunchedEffect(isHovered) {
        if (isHovered) {
            delay(timeMillis)
            state.show()
        } else {
            state.dismiss()
        }
    }

    return hoverable(interactionSource)
}
