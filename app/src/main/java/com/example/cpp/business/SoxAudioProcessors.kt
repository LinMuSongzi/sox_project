package com.example.cpp.business

import android.util.Log
import com.example.cpp.SoxUtil.TAG
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.audio.AudioProcessor
import com.google.android.exoplayer2.audio.AudioProcessor.EMPTY_BUFFER
import com.google.android.exoplayer2.audio.AudioProcessor.UnhandledAudioFormatException
import java.nio.ByteBuffer

class SoxAudioProcessors : AudioProcessor {


    private var enabled: Boolean = true
    private var inputEnded = false;
    private lateinit var format: AudioProcessor.AudioFormat
    private var buffer: ByteBuffer = EMPTY_BUFFER

    override fun configure(inputAudioFormat: AudioProcessor.AudioFormat): AudioProcessor.AudioFormat {
        if (inputAudioFormat.encoding != C.ENCODING_PCM_16BIT) {
            throw UnhandledAudioFormatException(inputAudioFormat)
        }
        printlnAudioFormat(inputAudioFormat)
        format = inputAudioFormat
        return if (enabled) inputAudioFormat else AudioProcessor.AudioFormat.NOT_SET
    }

    private fun printlnAudioFormat(inputAudioFormat: AudioProcessor.AudioFormat) {
        Log.i(
            TAG,
            "printlnAudioFormat: encoding = ${inputAudioFormat.encoding} , channelCount = ${inputAudioFormat.channelCount} ," +
                    " bytesPerFrame = ${inputAudioFormat.bytesPerFrame} , sampleRate = ${inputAudioFormat.sampleRate} , ${16 / 8 * inputAudioFormat.sampleRate * inputAudioFormat.channelCount}"
        )


    }

    override fun isActive(): Boolean {
        Log.i(TAG, "isActive: $enabled")
        return enabled
    }

    override fun queueInput(inputBuffer: ByteBuffer) {
        Log.i(TAG, "queueInput: limit = ${inputBuffer.limit()}")
        buffer = inputBuffer

    }

    /**
     * 播放完毕
     */
    override fun queueEndOfStream() {
        Log.i(TAG, "queueEndOfStream: ")
        inputEnded = true
    }

    override fun getOutput(): ByteBuffer {
        Log.i(TAG, "getOutput: ")
        return buffer
    }

    override fun isEnded(): Boolean {
        Log.i(TAG, "isEnded: ")
        return inputEnded
    }

    /**
     * exoplayer stop/release
     */
    override fun flush() {
        Log.i(TAG, "flush: ")
        inputEnded = false
    }

    override fun reset() {
        Log.i(TAG, "reset: ")
        buffer = EMPTY_BUFFER
    }


}