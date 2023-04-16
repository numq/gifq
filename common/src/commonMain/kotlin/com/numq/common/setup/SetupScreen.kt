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
import com.numq.common.settings.SettingsRow
import com.numq.common.upload.UploadDialog
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
        }, cancelUploading = {
            feature.dispatch(SetupIntent.CancelUploading)
        })
        is SetupState.Uploaded -> SetupUploaded(
            state.settings,
            state.size,
            calculateSize = {
                feature.dispatch(SetupIntent.UpdateSettings(it))
            },
            convert = {
                feature.dispatch(SetupIntent.StartProcessing(it))
            },
            cancel = {
                feature.dispatch(SetupIntent.Reset)
            }
        )
        is SetupState.Error -> SetupError(state.exception) {
            feature.dispatch(SetupIntent.Error(state.exception))
        }
    }
}

@Composable
private fun SetupEmpty(upload: (UploadedFile) -> Unit, cancelUploading: () -> Unit) {

    val uploadDialog = remember { UploadDialog() }

    val (status, setStatus) = remember { mutableStateOf<UploadStatus>(UploadStatus.Closed) }

    when (status) {
        is UploadStatus.Opened -> {
            CircularProgressIndicator()
            uploadDialog.show(setStatus)
        }
        is UploadStatus.Uploaded -> {
            val cancel = {
                cancelUploading()
                setStatus(UploadStatus.Closed)
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
            ) {
                CircularProgressIndicator()
                if (status.file.size / (1024 * 1024) > 100) Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("File is larger than 100MB", color = Color.Red)
                        IconButton(onClick = {
                            upload(status.file)
                        }) {
                            Icon(Icons.Rounded.Done, "ok")
                        }
                    }
                } else LaunchedEffect(Unit) { upload(status.file) }
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

    val (quality, setQuality) = remember { mutableStateOf(initialSettings.quality) }

    val settings by remember(name, width, height, frameRate, repeat, quality) {
        derivedStateOf {
            initialSettings.copy(
                fileName = name.takeIf { it.isNotBlank() } ?: initialSettings.fileName,
                width = width.toIntOrNull()
                    ?.coerceIn(Settings.minWidth, Settings.maxWidth) ?: initialSettings.width,
                height = height.toIntOrNull()
                    ?.coerceIn(Settings.minHeight, Settings.maxHeight) ?: initialSettings.height,
                fps = frameRate.toDoubleOrNull()?.coerceIn(Settings.minFPS, Settings.maxFPS) ?: initialSettings.fps,
                repeat = repeat,
                quality = quality.coerceIn(Settings.minQuality, Settings.maxQuality))
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
                    onValueChange = setFrameRate,
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
            Text(text = when (quality) {
                in (Settings.minQuality..Settings.midQuality - 1) -> "Low quality, small size"
                in (Settings.midQuality + 1..Settings.maxQuality) -> "High quality, large size"
                else -> "Medium quality, normal size"
            })
            Slider(
                value = quality,
                onValueChange = { setQuality(it) },
                valueRange = Settings.minQuality..Settings.maxQuality,
                steps = 1,
                modifier = Modifier.fillMaxWidth()
            )
        }
        OutlinedTextField(
            modifier = Modifier.onFocusChanged { setNameFocused(it.isFocused) },
            value = name,
            onValueChange = setName,
            label = { Text("Output name") },
            trailingIcon = { if (nameFocused) Text(".gif") },
            placeholder = { Text(settings.fileName) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )
        if (size != null) Text("Estimated gif size: ${SizeFormatter.format(size)}", Modifier.animateContentSize())
        else {
            val calculationTransition = rememberInfiniteTransition()
            val calculationAnimation by calculationTransition.animateFloat(
                initialValue = 1f,
                targetValue = 4f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000),
                    repeatMode = RepeatMode.Restart
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
    Column(
        Modifier.fillMaxWidth(.5f),
        horizontalAlignment = Alignment.CenterHorizontally,
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
        Button(onClick = { close() }) {
            Text("Close")
        }
    }
}