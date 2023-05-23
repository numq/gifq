package com.numq.common.settings

import com.numq.common.interactor.Interactor
import com.numq.common.upload.UploadedFile

class GetSettings(
    private val service: SettingsService,
) : Interactor<UploadedFile, Settings>() {
    override suspend fun execute(arg: UploadedFile) = service.getInfo(arg).getOrThrow()
}