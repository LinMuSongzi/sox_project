package com.psyone.sox

import android.util.Log
import com.example.cpp.business.SoudSoxBusiness
import com.example.cpp.business.WavAnalysisBusiness
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.audio.AudioProcessor
import com.google.android.exoplayer2.audio.AudioProcessor.EMPTY_BUFFER
import com.google.android.exoplayer2.audio.AudioProcessor.UnhandledAudioFormatException
import com.psyone.sox.SoxProgramHandler.TAG
import com.psyone.sox.SoxProgramHandler.exampleConvertByPcmData
import java.io.FileInputStream
import java.nio.ByteBuffer

class SoxAudioProcessor : AudioProcessor {


    private var enabled: Boolean = true
    private var inputEnded = false;
    private lateinit var format: AudioProcessor.AudioFormat


    private var offset = 44

    private lateinit var operateBytes: ByteArray

    override fun configure(inputAudioFormat: AudioProcessor.AudioFormat): AudioProcessor.AudioFormat {
        if (inputAudioFormat.encoding != C.ENCODING_PCM_16BIT) {
            SoudSoxBusiness.PATH_OUTPUT.delete()
            throw UnhandledAudioFormatException(inputAudioFormat)
        }
        printlnAudioFormat(inputAudioFormat)
        format = inputAudioFormat

        val size = 16L / 8 * inputAudioFormat.sampleRate * inputAudioFormat.channelCount / 10
        operateBytes = ByteArray(offset + size.toInt())
        return if (enabled) inputAudioFormat.apply {
            WavAnalysisBusiness.writeWaveFileHeader(
                operateBytes,
                size, size + offset, inputAudioFormat.sampleRate, channelCount, size.toInt() * 10
            )
//            audioTrack.play()
//            readByte.cop
        } else AudioProcessor.AudioFormat.NOT_SET
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

//    var effectsBean: EffectsBean? = EffectsBean("bass", "", "", "").apply {
//        values = arrayOf("50")
//    }
    var outputByteBuffer: ByteBuffer = EMPTY_BUFFER

    var offsetOther = 0
    override fun queueInput(inputBuffer: ByteBuffer) {
        if (inputBuffer.hasRemaining()) {
            val limite = inputBuffer.limit()
            inputBuffer.get(operateBytes, offset, limite)
//            audioTrack.write(operateBytes,offset,limite)
            operateBytes = exampleConvertByPcmData(
                operateBytes,
                5.toString(),
            )
            outputByteBuffer = ByteBuffer.allocate(limite)
            outputByteBuffer.put(operateBytes, offset, limite)
            outputByteBuffer.flip()
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

    private fun openWriteStream() {
        if (!SoudSoxBusiness.PATH_OUTPUT.exists()) {
            return;
        }
//        operateBytes = operateBytes.copyOfRange(offset)
        val fileInputStream = FileInputStream(SoudSoxBusiness.PATH_OUTPUT)
        fileInputStream.read(operateBytes)
        fileInputStream.close()
        if (SoudSoxBusiness.PATH_OUTPUT.exists()) {
            SoudSoxBusiness.PATH_OUTPUT.delete()
        }
        val a = operateBytes.size - offset
        Log.i(TAG, "openWriteStream: $a")
        outputByteBuffer = ByteBuffer.allocate(a)
        outputByteBuffer.put(operateBytes, offset, a)
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