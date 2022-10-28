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
import com.example.cpp.data.EffectsBean
import com.example.cpp.vm.MusicEffectsViewModel
import com.example.cpp.vm.MusicEffectsViewModel.Companion.CHOOSE_EFFECY_KEY
import com.google.android.exoplayer2.util.MediaFormatUtil
import com.musongzi.comment.ExtensionMethod.getSaveStateValue
import com.musongzi.core.base.business.BaseWrapBusiness
import com.musongzi.core.base.vm.MszViewModel
import java.io.*
import java.lang.reflect.Field
import java.util.concurrent.BlockingQueue
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import kotlin.experimental.or
import kotlin.math.abs

class SoudSoxBusiness : BaseWrapBusiness<MszViewModel<*, *>>(), DefaultLifecycleObserver {

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
            handlerBusiness()
            while (business.readLeng != -1 && lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                business.readLeng = inputStream.read(byteRead)
                val ebean = CHOOSE_EFFECY_KEY.getSaveStateValue<EffectsBean>(business.iAgent)
//                Log.i(TAG, "run: r_name = ${ebean?.r_name}")
                business.blockingQueue.put(SoxUtil.buildMusicByEffectInfo(ebean, byteRead.copyOf()))
            }
            business.destroyMeminfo()
        }

        private fun handlerBusiness() {

//            val lifecycle = soudSoxBusiness.lifecycleOwner.lifecycle
            inputStream = if (soudSoxBusiness.inputPath != null) {
                val inputFile = File(soudSoxBusiness.inputPath)
                Log.i(
                    TAG,
                    "audio_Format: 文件中长度 ${inputFile.length()} 字节 , 约 ${inputFile.length() / 1024} kb"
                )
                FileInputStream(inputFile)
            } else {
                soudSoxBusiness.inputStreamMethod?.invoke()!!
            }
            val byteHelps = ByteArray(20)

            var top = inputStream.read(byteHelps, 0, 20)

            val audio_Format = inputStream.read(byteHelps, 0, 2).let {

                Log.i(
                    TAG,
                    "audio_Format audio_Format byteHelps: ${byteHelps[0]} , ${byteHelps[1]}\n"
                )
                byteHelps[0]
            }//1代表PCM无损格式

            val channels = inputStream.read(byteHelps, 0, 2).let {

                Log.i(TAG, "audio_Format channels byteHelps: ${byteHelps[0]} , ${byteHelps[1]}\n")
                byteHelps[0]
            }//1或2
            val sampleByteArray = ByteArray(4);

            val simpleRate = inputStream.read(sampleByteArray).let {
                Log.i(
                    TAG,
                    "audio_Format simpleRate byteHelps: ${sampleByteArray[0]} , ${sampleByteArray[1]} , ${sampleByteArray[2]} , ${sampleByteArray[3]}\n"
                )
                val ret = 44100//abs(sampleByteArray[0] * 1 * sampleByteArray[1])
//                Log.i(TAG, "run: ${Int.in}")
                Log.i(TAG, "audio_Format simpleRate byteHelps:ret =  $ret\n")
                ret
            }//采样率(Sample, Rate)

            inputStream.read(byteHelps, 0, 4)

            val minSize = inputStream.read(byteHelps, 0, 2).let {
                Log.i(TAG, "audio_Format minSize byteHelps: ${byteHelps[0]} , ${byteHelps[1]}\n")
                byteHelps[1].toInt().shl(8).or(byteHelps[0].toInt())
            }

            val bit = inputStream.read(byteHelps, 0, 2).let {
                Log.i(TAG, "audio_Format bit byteHelps: ${byteHelps[0]} , ${byteHelps[1]}\n")
                byteHelps[0]
            }
            AUDIO_FORMAT = if (bit.toInt() == 16) {
                ENCODING_PCM_16BIT
            } else {
                ENCODING_PCM_8BIT
            }

            inputStream.read(sampleByteArray).apply {
                //"data"子块 (0x64617461),4字节
                Log.i(
                    TAG,
                    "audio_Format data子块 byteHelps: ${sampleByteArray[0]} , ${sampleByteArray[1]} , ${sampleByteArray[2]} , ${sampleByteArray[3]} \n"
                )
            }

            //子块数据域大小（SubChunk Size）
            top = inputStream.read(sampleByteArray).apply {
                //4字节,子块数据域大小（SubChunk Size）
                Log.i(
                    TAG,
                    "audio_Format 4字节,子块数据域大小 byteHelps: ${sampleByteArray[0]} , ${sampleByteArray[1]} , ${sampleByteArray[2]} , ${sampleByteArray[3]} \n"
                )
            }

            Log.i(
                TAG, "run:\n audio_Format = $audio_Format \n channels = $channels \n " +
                        "simpleRate = $simpleRate \n minSize = $minSize \n bit = $AUDIO_FORMAT\n"
            )

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
                soudSoxBusiness.audioTrack = audioTrack
                soudSoxBusiness.audioTrack?.play()
                soudSoxBusiness.executors.execute(MusicPlayRunnable(soudSoxBusiness))
            }
            // SampleRate * Channels * BitsPerSample / 8
            val size = abs(SAMPLE_RATE_INHZ) *  channels  * bit / 8
            byteRead = ByteArray(size)
            soudSoxBusiness.readyWrite = ByteArray(size)
            soudSoxBusiness.readLeng = top
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