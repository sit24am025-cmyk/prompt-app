package com.idchan.prompt.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.idchan.prompt.domain.model.PromptMode
import com.idchan.prompt.ui.theme.CyanPrimary

@Composable
fun ModeChipRow(
    selectedMode: PromptMode,
    onModeSelected: (PromptMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val modes = PromptMode.entries

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        modes.forEach { mode ->
            val isSelected = mode == selectedMode
            FilterChip(
                selected = isSelected,
                onClick = { onModeSelected(mode) },
                label = { Text(mode.displayName) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CyanPrimary.copy(alpha = 0.2f),
                    selectedLabelColor = CyanPrimary,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = MaterialTheme.colorScheme.outline,
                    selectedBorderColor = CyanPrimary
                )
            )
        }
    }
}
