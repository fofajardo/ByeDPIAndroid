package io.github.dovecoteescapee.byedpi.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ListItemShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.alorma.compose.settings.ui.expressive.SettingsMenuLink
import com.alorma.compose.settings.ui.expressive.SettingsSwitch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun defaultSegmentedColors(): ListItemColors {
    return ListItemDefaults.segmentedColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsGroup(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp),
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap),
            content = content,
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PreferenceItem(
    title: String,
    summary: String? = null,
    enabled: Boolean = true,
    index: Int = 0,
    count: Int = 1,
    shapes: ListItemShapes = ListItemDefaults.segmentedShapes(index, count),
    colors: ListItemColors = defaultSegmentedColors(),
    onClick: (() -> Unit)? = null,
) {
    SettingsMenuLink(
        title = { Text(text = title) },
        subtitle = if (!summary.isNullOrEmpty()) {
            { Text(text = summary) }
        } else {
            null
        },
        enabled = enabled,
        colors = colors,
        shapes = shapes,
        onClick = {
            if (onClick != null) {
                onClick()
            }
        },
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SwitchPreferenceItem(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    summary: String? = null,
    enabled: Boolean = true,
    index: Int = 0,
    count: Int = 1,
    shapes: ListItemShapes = ListItemDefaults.segmentedShapes(index, count),
    colors: ListItemColors = defaultSegmentedColors(),
) {
    SettingsSwitch(
        state = checked,
        title = { Text(text = title) },
        subtitle = if (!summary.isNullOrEmpty()) {
            { Text(text = summary) }
        } else {
            null
        },
        enabled = enabled,
        colors = colors,
        shapes = shapes,
        onCheckedChange = onCheckedChange,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ListPreferenceItem(
    title: String,
    selectedValue: String,
    entries: List<String>,
    entryValues: List<String>,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    index: Int = 0,
    count: Int = 1,
    shapes: ListItemShapes = ListItemDefaults.segmentedShapes(index, count),
    colors: ListItemColors = defaultSegmentedColors(),
) {
    var showDialog by remember { mutableStateOf(false) }
    val selectedIndex = entryValues.indexOf(selectedValue).coerceAtLeast(0)
    val displaySummary = entries.getOrNull(selectedIndex) ?: selectedValue

    SettingsMenuLink(
        title = { Text(text = title) },
        subtitle = { Text(text = displaySummary) },
        enabled = enabled,
        colors = colors,
        shapes = shapes,
        onClick = { showDialog = true },
    )

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = title) },
            text = {
                Column {
                    entries.forEachIndexed { idx, entryText ->
                        val value = entryValues[idx]
                        val isSelected = value == selectedValue
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = isSelected,
                                    onClick = {
                                        onValueChange(value)
                                        showDialog = false
                                    },
                                    role = Role.RadioButton,
                                )
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = null,
                            )
                            Text(
                                text = entryText,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 16.dp),
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EditTextPreferenceItem(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    summary: String? = null,
    enabled: Boolean = true,
    index: Int = 0,
    count: Int = 1,
    shapes: ListItemShapes = ListItemDefaults.segmentedShapes(index, count),
    colors: ListItemColors = defaultSegmentedColors(),
    validate: ((String) -> Boolean)? = null,
) {
    var showDialog by remember { mutableStateOf(false) }
    var textInput by remember(showDialog) { mutableStateOf(value) }
    var isError by remember { mutableStateOf(false) }

    SettingsMenuLink(
        title = { Text(text = title) },
        subtitle = { Text(text = summary ?: value) },
        enabled = enabled,
        colors = colors,
        shapes = shapes,
        onClick = { showDialog = true },
    )

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = title) },
            text = {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = {
                        textInput = it
                        isError = if (validate != null) {
                            !validate(it)
                        } else {
                            false
                        }
                    },
                    isError = isError,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val valid = if (validate != null) {
                            validate(textInput)
                        } else {
                            true
                        }
                        if (valid) {
                            onValueChange(textInput)
                            showDialog = false
                        } else {
                            isError = true
                        }
                    },
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            },
        )
    }
}
