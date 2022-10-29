package com.example.cpp

import android.media.*
import android.media.AudioFormat.*
import android.media.AudioTrack.MODE_STREAM
import android.media.AudioTrack.getMinBufferSize
import android.media.MediaFormat.MIMETYPE_VIDEO_AVC
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.example.cpp.SoudSoxBusiness.Companion.TAG
import com.example.cpp.business.IMusicAnalysis
import com.example.cpp.business.WavAnalysisBusiness
import com.example.cpp.data.EffectsBean
import com.example.cpp.vm.MusicEffectsViewModel
import com.example.cpp.vm.MusicEffectsViewModel.Companion.CHOOSE_EFFECY_KEY
import com.google.android.exoplayer2.util.MediaFormatUtil
import com.musongzi.comment.ExtensionMethod.getNextBusiness
import com.musongzi.comment.ExtensionMethod.getSaveStateValue
import com.musongzi.core.base.business.BaseMapBusiness
import com.musongzi.core.base.business.BaseWrapBusiness
import com.musongzi.core.base.vm.MszViewModel
import java.io.*
import java.lang.reflect.Field
import java.util.concurrent.BlockingQueue
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import kotlin.experimental.and
import kotlin.experimental.or
import kotlin.math.abs

class SoudSoxBusiness : BaseMapBusiness<MszViewModel<*, *>>(), DefaultLifecycleObserver {

    //    lateinit var lifecycleOwner: LifecycleOwner
    var inputPath: String? = null
    lateinit var readyWrite: ByteArray
//    constructor(lifecycleOwner: LifecycleOwner, inputStreamMethod: () -> InputStream) : this(
//        lifecycleOwner
//    ) {
//        this.inputStreamMethod = inputStreamMethod
//    }

    var audioTrack: AudioTrack? = null
    var inputStreamMethod: (() -> InputStream?)? = null

    private var init = false

    var blockingQueue: BlockingQueue<ByteArray> = LinkedBlockingQueue(1)
    val executors: ExecutorService = Executors.newFixedThreadPool(2)
    override fun onCreate(owner: LifecycleOwner) {
        initPre()
    }

    fun observer(inputStreamMethod: (() -> InputStream?)? = null) {
        synchronized(SoudSoxBusiness::class.java) {
            if (init) {
                return
            }
            init = true
        }
        this.inputStreamMethod = inputStreamMethod
        if (audioTrack == null) {
            iAgent.getThisLifecycle()?.lifecycle?.addObserver(this)
        }
    }

    fun observer(p: String) {
        synchronized(SoudSoxBusiness::class.java) {
            if (init) {
                return
            }
            init = true
        }
        inputPath = p
//        this.inputStreamMethod = inputStreamMethod
        if (audioTrack == null) {
            iAgent.getThisLifecycle()?.lifecycle?.addObserver(this)
        }
    }


    private fun initPre() {
        val handlerThread = HandlerThread("aaa")
        handlerThread.start()

//        this.audioTrack = audioTrack
        executors.execute(MusicReadRunnable(this))

    }

    class MusicReadRunnable(var soudSoxBusiness: SoudSoxBusiness, bSize: ByteArray? = null) :
        java.lang.Runnable {

        lateinit var inputStream: InputStream
        lateinit var byteRead: ByteArray

        init {
            if (bSize != null) {
                byteRead = bSize
            }
        }


        override fun run() {
            val business = soudSoxBusiness
            val lifecycle = soudSoxBusiness.iAgent.getThisLifecycle()?.lifecycle ?: return
            preAnalysisMusicInfo()
            while (business.readLeng != -1 && lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                business.readLeng = inputStream.read(byteRead)
                val ebean = CHOOSE_EFFECY_KEY.getSaveStateValue<EffectsBean>(business.iAgent)
//                Log.i(TAG, "run: r_name = ${ebean?.r_name}")
                business.blockingQueue.put(SoxUtil.buildMusicByEffectInfo(ebean, byteRead.copyOf()))
            }
            business.destroyMeminfo()
        }

        private fun preAnalysisMusicInfo() {
            val lenght: Long?
            val business = soudSoxBusiness
            inputStream = if (business.inputPath != null) {
                val inputFile = File(business.inputPath!!)
                lenght = inputFile.length()
                Log.i(TAG, "audio_Format: 文件中长度 $lenght 字节 , 约 ${inputFile.length() / 1024} kb")
                FileInputStream(inputFile)
            } else {
                business.inputStreamMethod?.invoke()!!
            }

            val musicInfo = WavAnalysisBusiness::class.java.getNextBusiness(business)?.getMusicInfoByMusic(inputStream) ?: return

            Handler(Looper.getMainLooper()).post {
                val audioTrack = AudioTrack(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build(),
                    Builder().setSampleRate(SAMPLE_RATE_INHZ).setEncoding(AUDIO_FORMAT)
                        .setChannelMask(CHANNEL_OUT_MONO).build(),
                    minBufferSize,
                    MODE_STREAM,
                    AudioManager.AUDIO_SESSION_ID_GENERATE
                )
                business.audioTrack = audioTrack
                business.audioTrack?.play()
                business.executors.execute(MusicPlayRunnable(business))
            }
            // SampleRate * Channels * BitsPerSample / 8
            val size = musicInfo.simpleRate * musicInfo.channel * musicInfo.bit / 8
            /**
             *
             *
             *
             */

            byteRead = ByteArray(size)
            business.readyWrite = ByteArray(size)
            business.readLeng = musicInfo.headBitSize
        }


    }

    private fun destroyMeminfo() {
        executors.shutdownNow()
        audioTrack?.stop()
        audioTrack?.release()
        audioTrack = null
    }

    var readLeng = 0;

    class MusicPlayRunnable(var soudSoxBusiness: SoudSoxBusiness) : java.lang.Runnable {

        override fun run() {
            val lifecycle = soudSoxBusiness.iAgent.getThisLifecycle() ?: return
            while (lifecycle.lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) {
                val byteArray = soudSoxBusiness.blockingQueue.take()
//                if (byteArray != null) {
//                Log.i(TAG, "run: write")
                soudSoxBusiness.audioTrack?.write(byteArray, 0, soudSoxBusiness.readLeng)
//                }
            }
        }

    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        destroyMeminfo()
    }

    companion object {
        var AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        var SAMPLE_RATE_INHZ = 44100
        val minBufferSize =
            getMinBufferSize(SAMPLE_RATE_INHZ, AudioFormat.CHANNEL_OUT_MONO, AUDIO_FORMAT)

        const val TAG = "SoudSoxBusiness"

//        @JvmStatic
//        fun instance(lifecycleOwner: LifecycleOwner, inputPath: String) =
//            SoudSoxBusiness(lifecycleOwner, inputPath)
//
//        @JvmStatic
//        fun instance(lifecycleOwner: LifecycleOwner, input: () -> InputStream) =
//            SoudSoxBusiness(lifecycleOwner, input)


        fun String.pathConvetInputStream() = FileInputStream(File(this))

        fun String.pathConvetOutPutStream() = FileOutputStream(File(this))

        fun Int.convertOutputStreamBySize() = ByteArrayOutputStream(this);

        fun Int.convertInputStreamBySize() = ByteArrayOutputStream(this);

//        fun String.openSoxBusinessByInputStream(lifecycleOwner: LifecycleOwner) =
//            instance(lifecycleOwner) {
//                pathConvetInputStream()
//            }
    }


}
