package com.example.cpp.business

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.util.Log
import com.example.cpp.SoxUtil
import com.example.cpp.SoxUtil.TAG
import com.example.cpp.data.EffectsBean
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.audio.AudioProcessor
import com.google.android.exoplayer2.audio.AudioProcessor.EMPTY_BUFFER
import com.google.android.exoplayer2.audio.AudioProcessor.UnhandledAudioFormatException
import java.io.FileInputStream
import java.nio.ByteBuffer

class SoxAudioProcessors : AudioProcessor {


    private var enabled: Boolean = true
    private var inputEnded = false;
    private lateinit var format: AudioProcessor.AudioFormat


    val audioTrack = AudioTrack(
        AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build(),
        AudioFormat.Builder()
            .setSampleRate(SoudSoxBusiness.SAMPLE_RATE_INHZ)
            .setEncoding(SoudSoxBusiness.AUDIO_FORMAT)
            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO).build(),
        SoudSoxBusiness.minBufferSize,
        AudioTrack.MODE_STREAM,
        AudioManager.AUDIO_SESSION_ID_GENERATE
    )

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
            audioTrack.play()
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

    var effectsBean: EffectsBean? = EffectsBean("bass", "", "", "").apply {
        values = arrayOf("50")
    }
    var outputByteBuffer: ByteBuffer = EMPTY_BUFFER

    var offsetOther = 0
    override fun queueInput(inputBuffer: ByteBuffer) {


        if (inputBuffer.hasRemaining()) {
            val limite = inputBuffer.limit()
            Log.i(
                TAG,
                "queueInput: limit = $limite, offsetOther = $offsetOther "
            )
            inputBuffer.get(operateBytes, offset, inputBuffer.limit())
//            offsetOther += limite
//            if (offsetOther == operateBytes.size - offset - limite) {
                SoxUtil.buildMusicByEffectInfoFile(
                    effectsBean,
                    SoudSoxBusiness.PATH_OUTPUT.absolutePath,
                    operateBytes
                )
                openWriteStream()
                offsetOther = 0
//            }
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
        if (offsetOther == 0) {
            val buffer: ByteBuffer = this.outputByteBuffer
            this.outputByteBuffer = EMPTY_BUFFER
            return buffer
        } else {
            return EMPTY_BUFFER
        }
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