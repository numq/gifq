package com.numq.common.setup

import com.numq.common.converter.CalculateSize
import com.numq.common.mvi.Feature
import com.numq.common.settings.GetSettings

class SetupFeature constructor(
    private val getSettings: GetSettings,
    private val calculateSize: CalculateSize,
) : Feature<SetupState, SetupIntent, SetupEffect>(SetupState.Empty) {

    private var fileUploading: GetSettings? = null
    private var sizeCalculation: CalculateSize? = null

    override fun reduce(
        state: SetupState,
        intent: SetupIntent,
        updateState: (SetupState) -> Unit,
        emitEffect: (SetupEffect) -> Unit,
    ) = when (intent) {
        is SetupIntent.UploadError -> {
            fileUploading?.cancel()
            fileUploading = null
            updateState(SetupState.Error(intent.exception))
        }
        is SetupIntent.UploadFile -> fileUploading = getSettings.apply {
            invoke(coroutineScope, intent.file, error = {
                updateState(SetupState.Error(it))
            }, success = {
                updateState(SetupState.Uploaded(it.copy(
                    fileUrl = intent.file.url,
                    fileName = intent.file.name,
                    filePath = intent.file.path
                )))
            })
        }
        is SetupIntent.CancelUploading -> {
            fileUploading?.cancel()
            fileUploading = null
            updateState(SetupState.Empty)
        }
        is SetupIntent.UpdateSettings -> {
            sizeCalculation?.cancel()
            sizeCalculation = calculateSize.apply {
                invoke(coroutineScope, intent.settings, error = {
                    updateState(SetupState.Error(it))
                }, success = {
                    updateState(SetupState.Uploaded(intent.settings, it))
                })
            }
            sizeCalculation = null
        }
        is SetupIntent.StartProcessing -> {
            updateState(SetupState.Empty)
            emitEffect(SetupEffect.StartProcessing(intent.settings))
        }
        is SetupIntent.Error -> updateState(SetupState.Error(intent.exception))
        is SetupIntent.Reset -> {
            sizeCalculation?.cancel()
            sizeCalculation = null
            updateState(SetupState.Empty)
        }
    }
}