package com.psyone.sox

import android.media.AudioFormat
import android.util.Log
import com.google.android.exoplayer2.audio.AudioProcessor
import com.google.android.exoplayer2.audio.BaseAudioProcessor
import com.google.android.exoplayer2.audio.TeeAudioProcessor
import com.psyone.sox.SoxProgramHandler.exampleConvertByPcmData
import com.psyone.sox.SoxProgramHandler.exampleConvertByPcmData2
import com.psyone.sox.WavHelp.writeWaveFileHeader
import java.nio.ByteBuffer

class NewHandlerSoxAudioProcessor : BaseAudioProcessor() {


    companion object {
        const val TAG = "NewHandlerSoxAudio"
    }

    private var inputAudioFormat: AudioFormat? = null

    var musicEffecyBean: EffectsBean? = null

    override fun onConfigure(inputAudioFormat: AudioProcessor.AudioFormat): AudioProcessor.AudioFormat {
        Log.i(TAG, "onConfigure: inputAudioFormat , $inputAudioFormat")
        return inputAudioFormat.also {
            this@NewHandlerSoxAudioProcessor.inputAudioFormat = it
        }
    }

    override fun queueInput(inputBuffer: ByteBuffer) {

        if (!inputBuffer.hasRemaining() || inputAudioFormat == null) {
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
        readByte = exampleConvertByPcmData(readByte,if(musicEffecyBean != null) 20.toString() else null)
//        readByte = exampleConvertByPcmData2(readByte, musicEffecyBean,format.sampleRate,format.channelCount,16)
        Log.i(TAG, "queueInput: 处理后的 readByte = " + readByte.size)

        val newSize = readByte.size;
        replaceOutputBuffer(newSize - 44).apply {
            for (v in 44 until newSize) {
                this.put(readByte[v])
            }
            this.flip()
        }
    }


}