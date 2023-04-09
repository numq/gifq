package com.numq.common.processing

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.numq.common.converter.ConversionStatus
import com.numq.common.indicator.ProgressIndicator
import com.numq.common.settings.Settings

@Composable
fun ProcessingScreen(
    feature: ProcessingFeature,
    settings: Settings,
    close: () -> Unit,
) {
    when (feature.effect.collectAsState(null).value) {
        is ProcessingEffect.Close -> SideEffect { close() }
        else -> Unit
    }
    when (val state = feature.state.collectAsState().value) {
        is ProcessingState.Loading -> feature.dispatch(ProcessingIntent.Start(settings))
        is ProcessingState.Active -> ProcessingActive(
            Modifier.fillMaxWidth(.5f).height(24.dp),
            state.status.collectAsState(ConversionStatus.Progress()).value,
            cancel = { feature.dispatch(ProcessingIntent.Close) },
            complete = { feature.dispatch(ProcessingIntent.Complete(it)) }
        )
        is ProcessingState.Result -> ProcessingCompleted(state.path, close)
        is ProcessingState.Error -> ProcessingError(state.exception) {
            feature.dispatch(ProcessingIntent.Close)
        }
    }
}

@Composable
private fun ProcessingActive(
    modifier: Modifier,
    status: ConversionStatus,
    cancel: () -> Unit,
    complete: (String) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp, alignment = Alignment.CenterVertically)
    ) {
        when (status) {
            is ConversionStatus.Progress -> {
                ProgressIndicator(modifier, status.progress)
                Button(onClick = cancel) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.Cancel, "cancel")
                        Text("Cancel")
                    }
                }
            }
            is ConversionStatus.Result -> SideEffect { complete(status.path) }
            is ConversionStatus.Error -> {
                Icon(Icons.Rounded.Error, "error")
                Text(status.exception.localizedMessage)
                Button(onClick = cancel) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.ArrowBack, "exit")
                        Text("Load next")
                    }
                }
            }
        }
    }
}

@Composable
private fun ProcessingCompleted(path: String, close: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp, alignment = Alignment.CenterVertically)
    ) {
        Text(path)
        Button(onClick = close) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Text("Upload next")
                Icon(Icons.Rounded.ArrowForward, "exit")
            }
        }
    }
}

@Composable
private fun ProcessingError(exception: Exception, close: () -> Unit) {
    Column(
        Modifier.fillMaxWidth(.5f), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp, alignment = Alignment.CenterVertically)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("ERROR", color = Color.Red)
            Icon(Icons.Rounded.ErrorOutline, "processing error", tint = Color.Red)
        }
        Text(exception.localizedMessage)
        Button(onClick = close) {
            Button(onClick = { close() }) {
                Text("Close")
            }
        }
    }
}