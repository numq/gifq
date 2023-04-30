package com.numq.common.encoder

import com.madgag.gif.fmsware.AnimatedGifEncoder
import org.bytedeco.javacv.Frame
import org.bytedeco.javacv.Java2DFrameConverter
import java.io.OutputStream

actual class Encoder actual constructor(
    private val width: Int,
    private val height: Int,
    private val quality: Float,
    private val delay: Long,
    private val loopCount: Int,
) {
    private var encoder: AnimatedGifEncoder? = null

    private fun scaledQuality(value: Float) = ((1 - value) * 20 + value * 10).toInt()

    actual fun start(os: OutputStream) {
        os.use {
            encoder = AnimatedGifEncoder().apply {
                setSize(width, height)
                setDelay(delay.toInt())
                setQuality(scaledQuality(quality))
                setRepeat(loopCount)
                start(it)
            }
        }
    }

    actual fun finish() {
        encoder?.finish()
    }

    actual fun addFrame(frame: Frame) {
        Java2DFrameConverter().use { converter ->
            encoder?.addFrame(converter.convert(frame)) ?: throw EncoderException.Default
        }
    }
}