package com.numq.common.setup

import com.numq.common.converter.CalculateSize
import com.numq.common.mvi.Feature
import com.numq.common.settings.GetSettings

class SetupFeature(
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
            clearUploading()
            updateState(SetupState.Error(intent.exception))
        }

        is SetupIntent.UploadFile -> fileUploading = getSettings.apply {
            invoke(coroutineScope, intent.file, error = {
                updateState(SetupState.Error(it))
            }, success = {
                updateState(
                    SetupState.Uploaded(
                        it.copy(
                            fileUrl = intent.file.url,
                            fileInitialName = intent.file.name,
                            filePath = intent.file.path
                        )
                    )
                )
            })
        }

        is SetupIntent.CancelUploading -> {
            clearUploading()
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
        }

        is SetupIntent.StartProcessing -> {
            updateState(SetupState.Empty)
            emitEffect(SetupEffect.StartProcessing(intent.settings))
        }

        is SetupIntent.Error -> updateState(SetupState.Error(intent.exception))
        is SetupIntent.Reset -> {
            clearCalculation()
            updateState(SetupState.Empty)
        }
    }

    private fun clearUploading() {
        fileUploading?.cancel()
        fileUploading = null
    }

    private fun clearCalculation() {
        sizeCalculation?.cancel()
        sizeCalculation = null
    }
}