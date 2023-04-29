package com.numq.common.encoder

import org.bytedeco.javacv.Frame
import java.io.OutputStream

expect class Encoder constructor(width: Int, height: Int, quality: Float, delay: Long, loopCount: Int) {
    fun start(os: OutputStream)
    fun finish()
    fun addFrame(frame: Frame)
}