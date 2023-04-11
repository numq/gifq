package com.numq.common.settings

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ColumnScope.SettingsColumn(content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier.weight(1f, false).padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        content()
    }
}

@Composable
fun RowScope.SettingsColumn(content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier.weight(1f, false).padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        content()
    }
}