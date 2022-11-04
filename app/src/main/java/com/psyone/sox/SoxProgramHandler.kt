package com.psyone.sox

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.audio.AudioCapabilities
import com.google.android.exoplayer2.audio.AudioSink
import com.google.android.exoplayer2.audio.DefaultAudioSink

object SoxProgramHandler {

    init {
        System.loadLibrary("ColSleep_sox_lib")
    }

    const val TAG = "SOX"

    @JvmStatic
    external fun exampleConvertByPcmData(byteArray: ByteArray, values: String?): ByteArray



    @RequiresApi(Build.VERSION_CODES.M)
    fun exoPlaySImple(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        //请填写本地/网络的 wav文件
        path: String?
    ): Player? {

//        var ouputPlayFile: File? = null
        if (path == null) {
            Toast.makeText(context, "不存在播放文件", Toast.LENGTH_SHORT).show()
            return null
        }

        //1. 创建播放器
        val player = ExoPlayer.Builder(context).setRenderersFactory(object :
            DefaultRenderersFactory(context) {
            override fun buildAudioSink(
                context: Context,
                enableFloatOutput: Boolean,
                enableAudioTrackPlaybackParams: Boolean,
                enableOffload: Boolean
            ): AudioSink? {
                return DefaultAudioSink.Builder()
                    .setAudioCapabilities(AudioCapabilities.getCapabilities(context))
                    .setEnableFloatOutput(enableFloatOutput)
                    .setEnableAudioTrackPlaybackParams(enableAudioTrackPlaybackParams)
                    /**
                     * 设置SoxAudioProcessor
                     * 处理音频数据
                     */
                    .setAudioProcessors(arrayOf(SoxAudioProcessor()))
                    .setOffloadMode(
                        if (enableOffload) DefaultAudioSink.OFFLOAD_MODE_ENABLED_GAPLESS_REQUIRED else DefaultAudioSink.OFFLOAD_MODE_DISABLED
                    )
                    .build()
            }
        }).build()

        Log.i(TAG, "exoPlaySImple: $path")
        player.setMediaItem(MediaItem.fromUri(path))

        //4.当Player处于STATE_READY状态时，进行播放
        player.playWhenReady = true

        //5. 调用prepare开始加载准备数据，该方法时异步方法，不会阻塞ui线程
        player.prepare()
        player.play() //  此时处于 STATE_BUFFERING = 2;
        lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
//                player.pause()
                player.stop()
                player.release()
            }
        })

        return player;

    }

    val EFFECTS_ARRAY :List<EffectsTopBean> = arrayListOf<EffectsTopBean>().apply {
        var titleOne = EffectsTopBean("滤波器类", e_child = buildEffectRealData("滤波器类"))
        add(titleOne)
        titleOne = EffectsTopBean("合成效果器", e_child = buildEffectRealData("合成效果器"))
        add(titleOne)
        titleOne = EffectsTopBean("音量效果器", e_child = buildEffectRealData("音量效果器"))
        add(titleOne)
        titleOne = EffectsTopBean("剪辑效果器", e_child = buildEffectRealData("剪辑效果器"))
        add(titleOne)
        titleOne = EffectsTopBean("混音效果器", e_child = buildEffectRealData("混音效果器"))
        add(titleOne)
        titleOne = EffectsTopBean("节奏和音高效果器", e_child = buildEffectRealData("节奏和音高效果器"))
        add(titleOne)
        titleOne = EffectsTopBean("主线路效果器", e_child = buildEffectRealData("主线路效果器"))
        add(titleOne)
        titleOne = EffectsTopBean("特殊效果器", e_child = buildEffectRealData("特殊效果器"))
        add(titleOne)
        titleOne = EffectsTopBean("其他", e_child = buildEffectRealData("其他"))
        add(titleOne)
    }
    private fun buildEffectRealData(s: String): List<EffectsBean> {
        val list:ArrayList<EffectsBean> = arrayListOf()
        /**
         *
        childBean = EffectsBean(c_name = "11111", e_name = "11111", content = "1111111")
        list.add(childBean)
         *
         */
        var childBean: EffectsBean
        when(s){
            "滤波器类"->{
                childBean = EffectsBean(c_name = "通用?", e_name = "allpass", content = "RBJ all-pass biquad IIR filter")
                list.add(childBean)
                childBean = EffectsBean(c_name = "带通滤波器", e_name = "bandpass", content = "RBJ band-pass biquad IIR filter")
                list.add(childBean)
                childBean = EffectsBean(c_name = "带阻滤波器", e_name = "bandreject", content = "RBJ band-reject biquad IIR filter")
                list.add(childBean)
                childBean = EffectsBean(c_name = "乐队?", e_name = "band", content = "SPKit resonator band-pass IIR filter")
                list.add(childBean)
                childBean = EffectsBean(c_name = "贝斯", e_name = "bass", content = "Tone control: RBJ shelving biquad IIR filter")
                list.add(childBean)
                childBean = EffectsBean(c_name = "均衡器", e_name = "equalizer", content = "RBJ peaking equalisation biquad IIR filter")
                list.add(childBean)
                childBean = EffectsBean(c_name = "FFT卷积FIR滤波器", e_name = "firfit+", content = "FFT convolution FIR filter using given freq. response")
                list.add(childBean)
                childBean = EffectsBean(c_name = "高通滤波", e_name = "highpass", content = "High-pass filter: Single pole or RBJ biquad IIR")
                list.add(childBean)
                childBean = EffectsBean(c_name = "希尔伯特变换滤波器", e_name = "hilbert", content = "Hilbert transform filter (90 degrees phase shift)")
                list.add(childBean)
                childBean = EffectsBean(c_name = "低通滤波器", e_name = "lowpass", content = "Low-pass filter: single pole or RBJ biquad IIR")
                list.add(childBean)
                childBean = EffectsBean(c_name = "高通/带通/抑制FIR", e_name = "sinc", content = "Sinc-windowed low/high-pass/band-pass/reject FIR")
                list.add(childBean)
                childBean = EffectsBean(c_name = "高音", e_name = "treble", content = "Tone control: RBJ shelving biquad IIR filter")
                list.add(childBean)
            }
            "合成效果器"->{
                childBean = EffectsBean(c_name = "混合", e_name = "chorus", content = "Make a single instrument sound like many")
                list.add(childBean)
                childBean = EffectsBean(c_name = "延迟效果", e_name = "delay", content = "Delay one or more channels")
                list.add(childBean)
                childBean = EffectsBean(c_name = "回音/回想", e_name = "echo", content = "Add an echo")
                list.add(childBean)
                childBean = EffectsBean(c_name = "回音/回想 多个", e_name = "echos", content = "Add a sequence of echos")
                list.add(childBean)
                childBean = EffectsBean(c_name = "立体声?", e_name = "flanger", content = "Stereo flanger")
                list.add(childBean)
                childBean = EffectsBean(c_name = "非线性失真?", e_name = "overdrive", content = "Non-linear distortion")
                list.add(childBean)
                childBean = EffectsBean(c_name = "相位器", e_name = "phaser", content = "Phase shifter")
                list.add(childBean)
                childBean = EffectsBean(c_name = "音频重复", e_name = "repeat", content = "Loop the audio a number of times")
                list.add(childBean)
                childBean = EffectsBean(c_name = "混响", e_name = "reverb", content = "Add reverberation")
                list.add(childBean)
                childBean = EffectsBean(c_name = "颠倒", e_name = "reverse", content = "Reverse the audio (to search for Satanic messages")
                list.add(childBean)
                childBean = EffectsBean(c_name = "颤音/震音", e_name = "tremolo", content = "Sinusoidal volume modulation")
                list.add(childBean)
            }
            "音量效果器"->{
                childBean = EffectsBean(c_name = "压伸", e_name = "compand", content = "Signal level compression/expansion/limiting")
                list.add(childBean)
                childBean = EffectsBean(c_name = "差异/对比", e_name = "contrast", content = "Phase contrast volume enhancement")
                list.add(childBean)
                childBean = EffectsBean(c_name = "直流偏移", e_name = "dcshift", content = "Apply or remove DC offset")
                list.add(childBean)
                childBean = EffectsBean(c_name = "淡入/淡出", e_name = "fade", content = "Apply a fade-in and/or fade-out to the audio")
                list.add(childBean)
                childBean = EffectsBean(c_name = "增进/获得", e_name = "gain", content = "Apply gain or attenuation; normalise/equalise/balance/headroom")
                list.add(childBean)
                childBean = EffectsBean(c_name = "响度/音量", e_name = "loudness", content = "Gain control with ISO 226 loudness compensation")
                list.add(childBean)
                childBean = EffectsBean(c_name = "多维度压伸", e_name = "mcompand", content = "Multi-band compression/expansion/limiting")
                list.add(childBean)
                childBean = EffectsBean(c_name = "标准/常态/正常化", e_name = "norm", content = "Normalise to 0dB (or other)")
                list.add(childBean)
                childBean = EffectsBean(c_name = "音频音量", e_name = "vol", content = "Adjust audio volume")
                list.add(childBean)
            }
            "剪辑效果器"->{
                childBean = EffectsBean(c_name = "铺垫", e_name = "pad", content = "Pad (usually) the ends of the audio with silence")
                list.add(childBean)
                childBean = EffectsBean(c_name = "失声", e_name = "silence", content = "Remove portions of silence from the audio")
                list.add(childBean)
                childBean = EffectsBean(c_name = "同步/交叉", e_name = "splice", content = "Perform the equivalent of a cross-faded tape splice")
                list.add(childBean)
                childBean = EffectsBean(c_name = "裁剪", e_name = "trim", content = "Cuts portions out of the audio")
                list.add(childBean)
                childBean = EffectsBean(c_name = "语音端点检测", e_name = "vad", content = "Voice activity detector")
                list.add(childBean)
            }
            "混音效果器"->{
                childBean = EffectsBean(c_name = "多通道", e_name = "channels", content = "Auto mix or duplicate to change number of channels")
                list.add(childBean)
                childBean = EffectsBean(c_name = "分开/分离", e_name = "divide+", content = "Divide sample values by those in the 1st channel (W.I.P.)")
                list.add(childBean)
                childBean = EffectsBean(c_name = "任意混合", e_name = "remix", content = "Produce arbitrarily mixed output channels")
                list.add(childBean)
                childBean = EffectsBean(c_name = "交换立体声通道", e_name = "swap", content = "Swap stereo channels")
                list.add(childBean)
            }
            "节奏和音高效果器"->{
                childBean = EffectsBean(c_name = "弯曲/扭曲", e_name = "bend", content = "Bend pitch at given times without changing tempo")
                list.add(childBean)
                childBean = EffectsBean(c_name = "调整音高", e_name = "pitch", content = "Adjust pitch (= key) without changing tempo")
                list.add(childBean)
                childBean = EffectsBean(c_name = "加速/节奏加快", e_name = "speed", content = "Adjust pitch & tempo together")
                list.add(childBean)
                childBean = EffectsBean(c_name = "延伸/延长", e_name = "stretch", content = "Adjust tempo without changing pitch (simple alg.)")
                list.add(childBean)
                childBean = EffectsBean(c_name = "拍子/节奏", e_name = "tempo", content = "Adjust tempo without changing pitch (WSOLA alg.)")
                list.add(childBean)
            }
            "主线路效果器"->{
                childBean = EffectsBean(c_name = "抖动/颤抖", e_name = "dither", content = "Add dither noise to increase quantisation SNR")
                list.add(childBean)
                childBean = EffectsBean(c_name = "更改采样率", e_name = "rate", content = "Change audio sampling rate")
                list.add(childBean)
            }

            "特殊效果器"->{
                childBean = EffectsBean(c_name = "迪姆/deemph", e_name = "deemph", content = "ISO 908 CD de-emphasis (shelving) IIR filter")
                list.add(childBean)
                childBean = EffectsBean(c_name = "耳机效果", e_name = "earwax", content = "Process CD audio to best effect for headphone use")
                list.add(childBean)
                childBean = EffectsBean(c_name = "除噪/降噪", e_name = "noisered", content = "Filter out noise from the audio")
                list.add(childBean)
                childBean = EffectsBean(c_name = "卡拉ok/ktv", e_name = "oops", content = "Out Of Phase Stereo (or Karaoke) effect")
                list.add(childBean)
                childBean = EffectsBean(c_name = "riaa效果", e_name = "riaa", content = "RIAA vinyl playback equalisation")
                list.add(childBean)
            }

            "其他"->{
                childBean = EffectsBean(c_name = "噪音传感器", e_name = "noiseprof", content = "Produce a DFT profile of the audio (use with noisered)")
                list.add(childBean)
                childBean = EffectsBean(c_name = "光谱图", e_name = "spectrogram", content = "graph signal level vs. frequency & time (needs libpng)")
                list.add(childBean)
                childBean = EffectsBean(c_name = "stat", e_name = "stat", content = "Enumerate audio peak & RMS levels, approx. freq., etc.")
                list.add(childBean)
                childBean = EffectsBean(c_name = "stats", e_name = "stats", content = "Multichannel aware stat")
                list.add(childBean)
                childBean = EffectsBean(c_name = "ladspa/拉德斯帕", e_name = "ladspa", content = "Apply LADSPA plug-in effects e.g. CMT (Computer Music Toolkit)")
                list.add(childBean)
                childBean = EffectsBean(c_name = "合成/调制音频音调或噪声信号", e_name = "synth", content = "Synthesise/modulate audio tones or noise signals")
                list.add(childBean)
                childBean = EffectsBean(c_name = "新文件", e_name = "newfile", content = "Create a new output file when an effects chain ends.")
                list.add(childBean)
                childBean = EffectsBean(c_name = "多链效果重启", e_name = "restart", content = "Restart 1st effects chain when multiple chains exist")
                list.add(childBean)
                childBean = EffectsBean(c_name = "二阶IIR滤波器", e_name = "biquad", content = "2nd-order IIR filter using externally provided coefficients")
                list.add(childBean)
                childBean = EffectsBean(c_name = "丢弃降低采样/压缩", e_name = "downsample", content = "Reduce sample rate by discarding samples")
                list.add(childBean)
                childBean = EffectsBean(c_name = "FFT卷积FIR滤波器", e_name = "fir", content = "FFT convolution FIR filter using externally provided coefficients")
                list.add(childBean)
                childBean = EffectsBean(c_name = "零填充提高采样率", e_name = "upsample", content = "Increase sample rate by zero stuffing")
                list.add(childBean)
            }


        }
        return list;
    }

}