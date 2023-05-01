package com.numq.common.encoder

import com.squareup.gifencoder.GifEncoder
import com.squareup.gifencoder.ImageOptions
import org.bytedeco.javacv.AndroidFrameConverter
import org.bytedeco.javacv.Frame
import java.io.OutputStream
import java.util.concurrent.TimeUnit

actual class Encoder actual constructor(
    private val width: Int,
    private val height: Int,
    private val quality: Float,
    private val delay: Long,
    private val loopCount: Int,
) {
    private var encoder: GifEncoder? = null

    actual fun start(os: OutputStream) {
        os.use { encoder = GifEncoder(it, width, height, loopCount) }
    }

    actual fun finish() {
        encoder?.finishEncoding()
    }

    actual fun addFrame(frame: Frame) {
        val options = ImageOptions().apply {
            setDelay(delay, TimeUnit.MILLISECONDS)
            setColorQuantizer { originalColors, maxColorCount ->
                val oldMin = 64
                val oldMax = 256
                val newMin = 0
                val newRange = maxColorCount - newMin
                val oldRange = oldMax - oldMin
                val scaleFactor = (newRange / oldRange) * (quality * 100.0)
                originalColors.map { it.scaled(scaleFactor / 100.0) }.toSet()
            }
        }
        // TODO: 4/25/2023 quality changing support
        // TODO: 4/26/2023 ensure that quality changing works
        val pixels = AndroidFrameConverter().use { converter ->
            IntArray(width * height).apply {
                converter.convert(frame).getPixels(this, 0, width, 0, 0, width, height)
            }
        }
        encoder?.addImage(pixels, width, options) ?: throw EncoderException.Default
    }
}