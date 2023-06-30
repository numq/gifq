package com.numq.common.setup

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.numq.common.collector.Collector.collect
import com.numq.common.format.SizeFormatter
import com.numq.common.settings.Settings
import com.numq.common.settings.SettingsColumn
import com.numq.common.settings.SettingsException
import com.numq.common.settings.SettingsRow
import com.numq.common.upload.UploadDialog
import com.numq.common.upload.UploadException
import com.numq.common.upload.UploadStatus
import com.numq.common.upload.UploadedFile
import kotlinx.coroutines.delay

@Composable
fun SetupScreen(
    feature: SetupFeature,
    startProcessing: (Settings) -> Unit,
) {
    when (val effect = collect(feature.effect)) {
        is SetupEffect.StartProcessing -> startProcessing(effect.settings)
        else -> Unit
    }
    when (val state = collect(feature.state)) {
        is SetupState.Empty -> SetupEmpty(upload = {
            feature.dispatch(SetupIntent.UploadFile(it))
        }, error = {
            feature.dispatch(SetupIntent.UploadError(it))
        }, cancel = {
            feature.dispatch(SetupIntent.CancelUploading)
        })

        is SetupState.Uploaded -> SetupUploaded(state.settings, state.size, calculateSize = {
            feature.dispatch(SetupIntent.UpdateSettings(it))
        }, convert = {
            feature.dispatch(SetupIntent.StartProcessing(it))
        }, cancel = {
            feature.dispatch(SetupIntent.Reset)
        })

        is SetupState.Error -> SetupError(state.exception) {
            feature.dispatch(SetupIntent.Reset)
        }
    }
}

@Composable
private fun SetupEmpty(upload: (UploadedFile) -> Unit, error: (Exception) -> Unit, cancel: () -> Unit) {

    val uploadDialog = remember { UploadDialog() }

    val (status, setStatus) = remember { mutableStateOf<UploadStatus>(UploadStatus.Closed) }

    when (status) {
        is UploadStatus.Opened -> {
            CircularProgressIndicator()
            uploadDialog.show(setStatus)
        }

        is UploadStatus.Error -> error(status.exception)
        is UploadStatus.Uploaded -> {
            val cancelAndClose = {
                cancel()
                setStatus(UploadStatus.Closed)
            }
            LaunchedEffect(Unit) { upload(status.file) }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
            ) {
                CircularProgressIndicator()
                Button(onClick = cancelAndClose) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.Cancel, "cancel")
                        Text("Cancel")
                    }
                }
            }
        }

        is UploadStatus.Closed -> Button(onClick = { setStatus(UploadStatus.Opened) }) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Upload video")
                Icon(Icons.Rounded.FolderOpen, "choose")
            }
        }
    }
}

@Composable
private fun SetupUploaded(
    initialSettings: Settings,
    size: Long?,
    calculateSize: (Settings) -> Unit,
    convert: (Settings) -> Unit,
    cancel: () -> Unit,
) {

    val (name, setName) = remember { mutableStateOf("") }
    val (nameFocused, setNameFocused) = remember { mutableStateOf(false) }

    val (width, setWidth) = remember { mutableStateOf("") }
    val (widthFocused, setWidthFocused) = remember { mutableStateOf(false) }

    val (height, setHeight) = remember { mutableStateOf("") }
    val (heightFocused, setHeightFocused) = remember { mutableStateOf(false) }

    val (frameRate, setFrameRate) = remember { mutableStateOf("") }

    val (repeat, setRepeat) = remember { mutableStateOf(initialSettings.repeat) }

    val (qualityLevel, setQualityLevel) = remember { mutableStateOf(initialSettings.qualityLevel) }

    val settings by remember(name, width, height, frameRate, repeat, qualityLevel) {
        derivedStateOf {
            initialSettings.copy(fileChangedName = name.takeIf { it.isNotBlank() },
                width = width.toIntOrNull()?.takeIf { it in Settings.minWidth..Settings.maxWidth }
                    ?: initialSettings.width,
                height = height.toIntOrNull()?.takeIf { it in Settings.minHeight..Settings.maxHeight }
                    ?: initialSettings.height,
                fps = frameRate.toDoubleOrNull()?.coerceIn(Settings.minFPS, Settings.maxFPS) ?: initialSettings.fps,
                repeat = repeat,
                qualityLevel = qualityLevel.coerceIn(Settings.minQuality, Settings.maxQuality))
        }
    }

    val (widthChanged, setWidthChanged) = remember { mutableStateOf(false) }

    val (heightChanged, setHeightChanged) = remember { mutableStateOf(false) }

    val (frameRateChanged, setFrameRateChanged) = remember { mutableStateOf(false) }

    LaunchedEffect(width) { if (!widthChanged) setWidthChanged(true) }

    LaunchedEffect(width) { if (!heightChanged) setHeightChanged(true) }

    LaunchedEffect(width) { if (!frameRateChanged) setFrameRateChanged(true) }

    val invalidWidth by remember(width) {
        derivedStateOf {
            widthChanged && width.toIntOrNull()?.let { it !in Settings.minWidth..Settings.maxWidth } ?: true
        }
    }

    val invalidHeight by remember(height) {
        derivedStateOf {
            heightChanged && height.toIntOrNull()?.let { it !in Settings.minHeight..Settings.maxHeight } ?: true
        }
    }

    val invalidFrameRate by remember(frameRate) {
        derivedStateOf {
            frameRateChanged && frameRate.toDoubleOrNull()?.let { it !in Settings.minFPS..Settings.maxFPS } ?: true
        }
    }

    LaunchedEffect(settings) {
        delay(500L)
        calculateSize(settings)
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(settings.fileUrl)
        Row(
            Modifier.fillMaxWidth().padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SettingsColumn {
                OutlinedTextField(
                    modifier = Modifier.onFocusChanged {
                        setWidthFocused(it.isFocused)
                    },
                    value = width,
                    onValueChange = setWidth,
                    isError = invalidWidth,
                    label = { Text("Width") },
                    trailingIcon = { if (widthFocused) Text("px") },
                    placeholder = { Text(settings.width.toString()) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
            Spacer(Modifier.width(4.dp))
            SettingsColumn {
                OutlinedTextField(
                    modifier = Modifier.onFocusChanged {
                        setHeightFocused(it.isFocused)
                    },
                    value = height,
                    onValueChange = setHeight,
                    isError = invalidHeight,
                    label = { Text("Height") },
                    trailingIcon = { if (heightFocused) Text("px") },
                    placeholder = { Text(settings.height.toString()) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
            Spacer(Modifier.width(4.dp))
            SettingsColumn {
                OutlinedTextField(
                    modifier = Modifier,
                    value = frameRate,
                    onValueChange = {
                        if (it.isBlank()) setFrameRate("${initialSettings.fps}")
                        setFrameRate(it)
                    },
                    isError = invalidFrameRate,
                    label = { Text("FPS") },
                    placeholder = { Text(settings.fps.toString()) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
            Spacer(Modifier.width(4.dp))
            SettingsColumn {
                Text(text = "Repeat")
                Checkbox(repeat, onCheckedChange = {
                    setRepeat(it)
                })
            }
        }
        SettingsColumn {
            Text(
                text = when (qualityLevel.toInt().toFloat()) {
                    Settings.minQuality -> "Low quality"
                    Settings.midQuality -> "Medium quality"
                    Settings.maxQuality -> "High quality"
                    else -> "${qualityLevel.toInt()}%"
                }
            )
            Slider(
                value = qualityLevel,
                onValueChange = setQualityLevel,
                valueRange = Settings.minQuality..Settings.maxQuality,
                steps = Settings.maxQuality.toInt(),
                modifier = Modifier.fillMaxWidth()
            )
        }
        OutlinedTextField(
            modifier = Modifier.onFocusChanged { setNameFocused(it.isFocused) },
            value = name,
            onValueChange = setName,
            label = { Text("Output name") },
            trailingIcon = { if (nameFocused) Text(".gif") },
            placeholder = { Text(settings.fileChangedName ?: settings.fileInitialName) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )
        if (size != null) Text("Estimated gif size: ${SizeFormatter.format(size)}", Modifier.animateContentSize())
        else {
            val calculationTransition = rememberInfiniteTransition()
            val calculationAnimation by calculationTransition.animateFloat(
                initialValue = 1f, targetValue = 4f, animationSpec = infiniteRepeatable(
                    animation = tween(1000), repeatMode = RepeatMode.Restart
                )
            )
            Text("Calculating size${".".repeat(calculationAnimation.toInt())}")
        }
        SettingsRow {
            Button(onClick = { cancel() }, Modifier.weight(1f)) {
                SettingsRow {
                    Icon(Icons.Rounded.ArrowBack, "cancel")
                    Text("Back")
                }
            }
            Button(onClick = { convert(settings) }, Modifier.weight(1f)) {
                SettingsRow {
                    Text("Convert")
                    Icon(Icons.Rounded.MovieCreation, "start converting")
                }
            }
        }
    }
}

@Composable
private fun SetupError(exception: Exception, close: () -> Unit) {
    val default = "Try to upload another file."
    Column(
        Modifier.fillMaxWidth(.5f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp, alignment = Alignment.CenterVertically)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Text("ERROR", color = Color.Red)
            Icon(Icons.Rounded.ErrorOutline, "processing error", tint = Color.Red)
        }
        Text(
            when (exception.cause ?: exception) {
                is UploadException.InvalidFileSize, is SettingsException.InvalidFormat -> exception.message ?: default
                else -> default
            }
        )
        Button(onClick = { close() }) {
            Text("Close")
        }
    }
}