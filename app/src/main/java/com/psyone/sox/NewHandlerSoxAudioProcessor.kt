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
        Log.i(TAG, "queueInput: musicType = $musicType , animatedValue = ${valueAnimate.animatedValue}")
        if(!valueAnimate.isStarted){
            valueAnimate.start()
        }
        readByte = ConvertByPcmData(readByte, if(musicType.isNullOrEmpty()) -1 else musicType!!.toInt(), valueAnimate.animatedValue as Int)
//            if (isNativeMusic) {
////            Log.i(TAG, "queueInput: 原声 = " + readByte.size)
//
//            if(!valueAnimate.isStarted){
//                valueAnimate.start()
//            }else{
//                Log.i(TAG, "queueInput: musicType = $musicType , animatedValue = ${valueAnimate.animatedValue}")
//            }
//            exampleConvertByPcmData3(readByte, musicType,valueAnimate.animatedValue as Int)
//        } else {
//            Log.i(TAG, "queueInput: 不是原声 = $euqInfo")
////            exampleConvertByPcmData(readByte, null)
//            exampleConvertByPcmData2(readByte, info = euqInfo, simpleRate = format.sampleRate, channel = format.channelCount, bit = 16)
//        }
//        readByte = exampleConvertByPcmData2(readByte, musicEffecyBean,format.sampleRate,format.channelCount,16)


        val newSize = readByte.size;
        replaceOutputBuffer(newSize - 44).apply {
            for (v in 44 until newSize) {
                this.put(readByte[v])
            }
            this.flip()
        }
    }


}