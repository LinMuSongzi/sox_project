package com.psyone.sox

import android.animation.ValueAnimator
import android.media.AudioFormat
import android.util.Log
import com.example.cpp.data.EuqInfo
import com.google.android.exoplayer2.audio.AudioProcessor
import com.google.android.exoplayer2.audio.BaseAudioProcessor
import com.psyone.sox.SoxProgramHandler.ConvertByPcmData
import com.psyone.sox.WavHelp.writeWaveFileHeader
import java.nio.ByteBuffer

class NewHandlerSoxAudioProcessor() : BaseAudioProcessor() {


    companion object {
        const val TAG = "NewHandlerSoxAudio"
    }

    var musicType: String? = null

    var euqInfo: EuqInfo? = null

    var isNativeMusic = true
    private var inputAudioFormat: AudioFormat? = null

    var musicEffecyBean: EffectsBean? = null

    val valueAnimate = ValueAnimator.ofInt(0, 10000, 0).apply {

        repeatMode = ValueAnimator.RESTART
        duration = 2000
    }

    var count = 0;
    var valueMaxCount = 0

    override fun onConfigure(inputAudioFormat: AudioProcessor.AudioFormat): AudioProcessor.AudioFormat {
        Log.i(TAG, "onConfigure: inputAudioFormat , $inputAudioFormat")
        return inputAudioFormat.also {
            this@NewHandlerSoxAudioProcessor.inputAudioFormat = it
        }
    }

    override fun queueInput(inputBuffer: ByteBuffer) {

        if (!inputBuffer.hasRemaining()) {
            return
        }

        val format = inputAudioFormat
        val length: Long = inputBuffer.remaining().toLong()
        val size = inputBuffer.remaining() + 44;
        var readByte = ByteArray(size).apply {
            for (v in 44 until size) {
                this[v] = inputBuffer.get()
            }
        }

        writeWaveFileHeader(readByte, length, length + 44, format.sampleRate, format.channelCount, format.sampleRate * 16 * format.channelCount / 8)

//        if (!valueAnimate.isStarted) {
//            valueAnimate.start()
//        }
        if (valueMaxCount != 0) {
            count++
            if (0 == count % valueMaxCount) {
                count = 0;
            }
        }
        Log.i(TAG, "queueInput: musicType = $musicType , animatedValue = $count")
        readByte = ConvertByPcmData(readByte, if (musicType.isNullOrEmpty()) -1 else musicType!!.toInt(), count)
        if (!valueAnimate.isStarted) {
            valueAnimate.start()
        } else {
            Log.i(TAG, "queueInput: musicType = $musicType , animatedValue = ${valueAnimate.animatedValue}")
        }

        val newSize = readByte.size;
        replaceOutputBuffer(newSize - 44).apply {
            for (v in 44 until newSize) {
                this.put(readByte[v])
            }
            this.flip()
        }
    }


}