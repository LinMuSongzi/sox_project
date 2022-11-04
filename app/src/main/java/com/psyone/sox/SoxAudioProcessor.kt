package com.psyone.sox

import android.util.Log
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.audio.AudioProcessor
import com.google.android.exoplayer2.audio.AudioProcessor.EMPTY_BUFFER
import com.google.android.exoplayer2.audio.AudioProcessor.UnhandledAudioFormatException
import com.psyone.sox.SoxProgramHandler.TAG
import com.psyone.sox.SoxProgramHandler.exampleConvertByPcmData
import com.psyone.sox.WavHelp.writeWaveFileHeader
import java.io.FileInputStream
import java.nio.ByteBuffer

class SoxAudioProcessor : AudioProcessor {

    private var enabled: Boolean = true
    private var inputEnded = false;
    private lateinit var format: AudioProcessor.AudioFormat
    private var offset = 44
    private var operateBytes: ByteArray? = null
    private var outputByteBuffer: ByteBuffer = EMPTY_BUFFER
    private var lastLimite = 0;

    override fun configure(inputAudioFormat: AudioProcessor.AudioFormat): AudioProcessor.AudioFormat {
        if (inputAudioFormat.encoding != C.ENCODING_PCM_16BIT) {
            throw UnhandledAudioFormatException(inputAudioFormat)
        }
        printlnAudioFormat(inputAudioFormat)
        format = inputAudioFormat
//      val size = 16L / 8 * inputAudioFormat.sampleRate * inputAudioFormat.channelCount / 10
        return if (enabled) inputAudioFormat else AudioProcessor.AudioFormat.NOT_SET
    }

    private fun printlnAudioFormat(inputAudioFormat: AudioProcessor.AudioFormat) {
        Log.i(TAG, "printlnAudioFormat: encoding = ${inputAudioFormat.encoding} , channelCount = ${inputAudioFormat.channelCount} ," +
                " bytesPerFrame = ${inputAudioFormat.bytesPerFrame} , sampleRate = ${inputAudioFormat.sampleRate} , ${16 / 8 * inputAudioFormat.sampleRate * inputAudioFormat.channelCount}"
        )
    }

    override fun isActive(): Boolean {
        Log.i(TAG, "isActive: $enabled")
        return enabled
    }


    override fun queueInput(inputBuffer: ByteBuffer) {
        if (inputBuffer.hasRemaining()) {
            val limite = inputBuffer.limit()
            var operateBytes = this.operateBytes
            if (operateBytes == null) {
                operateBytes = ByteArray(offset + limite)
                val byteRate = format.sampleRate * 16 * format.channelCount / 8
                Log.i(TAG, "queueInput: operateBytes = ${operateBytes.size} , byteRate = $byteRate , limite = $limite")
                writeWaveFileHeader(
                    operateBytes,
                    limite.toLong(),
                    (limite + offset).toLong(), format.sampleRate, format.channelCount, byteRate
                )
                this.operateBytes = operateBytes
            } else if (limite != lastLimite) {
                Log.i(TAG, "queueInput: $limite != $lastLimite")
                this.operateBytes = null
                queueInput(inputBuffer)
                return
            }

            inputBuffer.get(operateBytes, offset, limite)
            operateBytes = exampleConvertByPcmData(
                operateBytes,
                5.toString(),
            )
            val outputByteBuffer = ByteBuffer.allocate(limite)
            outputByteBuffer.put(operateBytes, offset, limite)
            outputByteBuffer.flip()
            this.outputByteBuffer = outputByteBuffer
            this.lastLimite = limite
        }
    }

    /**
     * 播放完毕
     */
    override fun queueEndOfStream() {
        Log.i(TAG, "queueEndOfStream: ")
        inputEnded = true
    }

    override fun getOutput(): ByteBuffer {
        val buffer = this.outputByteBuffer
        this.outputByteBuffer = EMPTY_BUFFER
        return buffer
    }

    override fun isEnded(): Boolean {
        Log.i(TAG, "isEnded: ")
        return inputEnded
    }

    /**
     *
     */
    override fun flush() {
        Log.i(TAG, "flush: ")
        inputEnded = false
    }

    override fun reset() {
        Log.i(TAG, "reset: ")
        inputEnded = true
        outputByteBuffer = EMPTY_BUFFER
    }


}