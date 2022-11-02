package com.example.cpp.business

import android.media.*
import android.media.AudioFormat.*
import android.media.AudioTrack.MODE_STREAM
import android.media.AudioTrack.getMinBufferSize
import android.os.Environment
import android.os.HandlerThread
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.example.cpp.SoxUtil
import com.example.cpp.data.EffectsBean
import com.example.cpp.data.MusicInfo
import com.example.cpp.vm.MusicEffectsViewModel.Companion.CHOOSE_EFFECY_KEY
import com.musongzi.comment.ExtensionMethod.getNextBusiness
import com.musongzi.comment.ExtensionMethod.getSaveStateValue
import com.musongzi.core.base.business.BaseMapBusiness
import com.musongzi.core.base.vm.MszViewModel
import okhttp3.internal.notify
import okhttp3.internal.wait
import java.io.*
import java.util.concurrent.BlockingQueue
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue

class SoudSoxBusiness : BaseMapBusiness<MszViewModel<*, *>>(), DefaultLifecycleObserver,
    ISoudPlayBusiness {

    private var musicInfo: MusicInfo? = null
    var inputPath: String? = null

    //    lateinit var readyWrite: ByteArray
    var audioTrack: AudioTrack? = null
    var inputStreamMethod: (() -> InputStream?)? = null
    val offset = 44;
    var headBytes = ByteArray(offset)
    val LOCK = Object();


    private var init = 0


    var blockingQueue: BlockingQueue<ByteArray> = LinkedBlockingQueue(1)

    //    var blockingQueueInt: BlockingQueue<Int> = LinkedBlockingQueue(1)
    val executors: ExecutorService = Executors.newFixedThreadPool(2)
    override fun onCreate(owner: LifecycleOwner) {
        initPre()
    }

    override fun observer(inputStreamMethod: (() -> InputStream?)?) {
        checkInit();
        this.inputStreamMethod = inputStreamMethod
        if (audioTrack == null) {
            iAgent.getThisLifecycle()?.lifecycle?.addObserver(this)
        }
    }

    override fun observer(p: String) {
        checkInit()
        inputPath = p
//        this.inputStreamMethod = inputStreamMethod
        if (audioTrack == null) {
            iAgent.getThisLifecycle()?.lifecycle?.addObserver(this)
        }
    }

    private fun checkInit() {
        synchronized(SoudSoxBusiness::class.java) {
            if (init.and(START_FLAG) > 0) {
                return
            }
            init = init.or(START_FLAG)
        }
    }


    private fun initPre() {
        val handlerThread = HandlerThread("aaa")
        handlerThread.start()

//        this.audioTrack = audioTrack
        PATH_OUTPUT.delete()
        executors.execute(MusicReadRunnable(this))

    }

    class MusicReadRunnable(var soudSoxBusiness: SoudSoxBusiness, bSize: ByteArray? = null) :
        java.lang.Runnable {

        lateinit var inputStream: InputStream

        /**
         * 44 + simplerate * bit * channels / 8
         */
        lateinit var byteRead: ByteArray

        init {
            if (bSize != null) {
                byteRead = bSize
            }
        }


        override fun run() {
            simpleRead222()
        }

        /**
         * 解析wav音频，预处理
         */
        private fun simpleRead222() {
            val business = soudSoxBusiness
            val lifecycle = soudSoxBusiness.iAgent.getThisLifecycle()?.lifecycle ?: return
            preAnalysisMusicInfo()
            business.readSize =
                inputStream.read(byteRead, business.offset, byteRead.size - business.offset)
//            business.readSize = inputStream.read(byteRead)

            var last = System.currentTimeMillis()

            while (lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED) && business.readSize != -1) {
                val ebean = CHOOSE_EFFECY_KEY.getSaveStateValue<EffectsBean>(business.iAgent)
                ebean?.apply {
                    if (values == null) {
                        values = arrayOf("5")
                    }
                    val array: Array<String> = values!!
                    if (System.currentTimeMillis() - last > 1000) {
                        array[0] = (array[0].toInt() + 5).toString();
                        if (array[0] == "80") {
                            array[0] = "10"
                        }
                    }
                }
                val writeByte = SoxUtil.buildMusicByEffectInfo(
                    ebean,
                    byteRead.copyOf()
                )

                business.audioTrack?.write(writeByte, business.offset, writeByte.size-business.offset)

                business.readSize =
                    inputStream.read(byteRead, business.offset, byteRead.size - business.offset)
            }
            inputStream.close()
            business.destroyMeminfo()
            business.handlerEnd(READ_END_FLAG)
        }

//        private fun simpleRead1() {
//            val business = soudSoxBusiness
//            val lifecycle = soudSoxBusiness.iAgent.getThisLifecycle()?.lifecycle ?: return
//            preAnalysisMusicInfo()
//            while (business.readSize != -1 && lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
//                business.readSize = inputStream.read(byteRead)
//                val ebean = CHOOSE_EFFECY_KEY.getSaveStateValue<EffectsBean>(business.iAgent)
//                business.blockingQueue.put(SoxUtil.buildMusicByEffectInfo(ebean, byteRead.copyOf()))
//            }
//            business.destroyMeminfo()
//
//        }

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
//            business.headBytes = ByteArray(44)
            business.musicInfo = WavAnalysisBusiness::class.java.getNextBusiness(business)
                ?.getMusicInfoByMusic(inputStream, business.headBytes) ?: return

            val musicInfo: MusicInfo = business.musicInfo!!

            Log.i(TAG, "preAnalysisMusicInfo: $musicInfo")
//            inputStream.read(business.headBytes, 24, 20)
            business.iAgent.runOnUiThread {
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
//                business.executors.execute(MusicPlayRunnable(business))
                synchronized(SoudSoxBusiness::class.java) {
                    SoudSoxBusiness::class.java.notify()
                }
            }
            synchronized(SoudSoxBusiness::class.java) {
                SoudSoxBusiness::class.java.wait()
            }
            // SampleRate * Channels * BitsPerSample / 8
            val size = musicInfo.getDataSize() / 10
            val rate = size * 10
            byteRead = ByteArray(musicInfo.headBitSize + size.toInt())
            WavAnalysisBusiness.writeWaveFileHeader(
                business.headBytes,
                size,
                size + business.offset,
                musicInfo.simpleRate,
                musicInfo.channel,
                rate.toInt()
            )
            System.arraycopy(business.headBytes, 0, byteRead, 0, business.offset)
            business.readSize = musicInfo.headBitSize
        }


    }

    var writeByte: ByteArray? = null

    /**
     * 写入数据，播放音乐
     */
    class MusicPlayRunnable(var soudSoxBusiness: SoudSoxBusiness) : Runnable {


        override fun run() {
            val business = soudSoxBusiness;
            val lifecycle = business.iAgent.getThisLifecycle() ?: return
            while (lifecycle.lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED) && soudSoxBusiness.init.and(
                    READ_END_FLAG
                ) == 0
            ) {

                synchronized(SoudSoxBusiness::class.java) {
                    business.writeByte = business.openStream()
                    SoudSoxBusiness::class.java.notify()
                    SoudSoxBusiness::class.java.wait()
                }
                business.writeByte?.let {
                    business.audioTrack?.write(
                        it, 0, it.size
                    )
                }

            }

            business.handlerEnd(WRITE_END_FLAG)
        }


    }

    private fun handlerEnd(orFlag: Int) {
        synchronized(SoudSoxBusiness::class.java) {
            init.or(orFlag)
            if (init.and(STATE_MASK_FLAG) > STATE_MASK_FLAG) {
                init = NORMAL_FLAG
                iAgent.getThisLifecycle()?.lifecycle?.removeObserver(this)
            }
        }
    }

    private fun openStream(): ByteArray? {
        var returnByteArray: ByteArray?
        if (musicInfo == null || !PATH_OUTPUT.exists()) {
            return null;
        }


        return musicInfo?.let {
//                Log.i(TAG, "openStream: PATH_OUTPUT " + PATH_OUTPUT.length())
            val fileInputStream = FileInputStream(PATH_OUTPUT)
            returnByteArray = ByteArray(it.getDataSize().toInt())
            fileInputStream.read(ByteArray(it.headBitSize))
            fileInputStream.read(returnByteArray)
            fileInputStream.close()
            if (PATH_OUTPUT.exists()) {
                PATH_OUTPUT.delete()
            }
            returnByteArray
        }
    }

    private fun destroyMeminfo() {
//        executors.shutdownNow()
        audioTrack?.stop()
        audioTrack?.release()
        audioTrack = null
    }

    var readSize = 0;
    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        destroyMeminfo()
    }

    companion object {

        private const val NORMAL_FLAG = 0
        private const val START_FLAG = 1;
        private const val READ_END_FLAG = 2;
        private const val WRITE_END_FLAG = 4;

        private const val STATE_MASK_FLAG = START_FLAG.or(WRITE_END_FLAG).or(READ_END_FLAG)

        const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        const val SAMPLE_RATE_INHZ = 44100
        val minBufferSize =
            getMinBufferSize(SAMPLE_RATE_INHZ, AudioFormat.CHANNEL_OUT_MONO, AUDIO_FORMAT)
        val PATH_OUTPUT = File(Environment.getExternalStorageDirectory(), "output_test.mp3")
        const val TAG = "SoudSoxBusiness"

        @JvmStatic
        fun String.pathConvetInputStream() = FileInputStream(File(this))

        @JvmStatic
        fun String.pathConvetOutPutStream() = FileOutputStream(File(this))

        @JvmStatic
        fun Int.convertOutputStreamBySize() = ByteArrayOutputStream(this);

        @JvmStatic
        fun Int.convertInputStreamBySize() = ByteArrayOutputStream(this);
    }


}
