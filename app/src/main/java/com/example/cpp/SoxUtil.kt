package com.example.cpp

import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.cpp.FileUtil.copyFile
import com.example.cpp.business.SoxAudioProcessors
import com.example.cpp.data.EffectsBean
import com.example.cpp.vm.MusicEffectsViewModel
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem.fromUri
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.STATE_READY
import com.google.android.exoplayer2.audio.AudioCapabilities
import com.google.android.exoplayer2.audio.AudioSink
import com.google.android.exoplayer2.audio.DefaultAudioSink
import com.google.android.exoplayer2.ui.PlayerControlView
import com.musongzi.core.base.business.BaseMapBusiness
import com.musongzi.core.base.vm.DataDriveViewModel
import com.musongzi.core.util.ActivityThreadHelp
import java.io.File

object SoxUtil {

    val BASE_URL: String = "http://192.168.1.106:8080"
    const val TAG = "SOX_UTIL"
    val FILE_PARENT = ActivityThreadHelp.getCurrentApplication().cacheDir
    val FILE_MP3 = File(FILE_PARENT, "qsws.wav")

    //    @JvmStatic
//    external fun sum(one: Int,tow:Int):Int


    @RequiresApi(Build.VERSION_CODES.M)
    fun <T : DataDriveViewModel<*>> exoPlaySImple(
        viewModel: T,
        path: String? = "${BASE_URL}/我的刻苦铭心的恋人.mp3"
    ) {
        val context = (viewModel.getThisLifecycle() as? Context)?:(viewModel.getThisLifecycle() as Fragment).requireContext()
        val ePlayer = viewModel.localSavedStateHandle().get<ExoPlayer>("ExoPlayer")
        if (ePlayer != null) {
//            val p = playerView.tag as? Player
            if (ePlayer.isPlaying) {
                ePlayer.pause()
            } else {
                ePlayer.play()
            }
            return
        }

        var ouputPlayFile: File? = null
        if (path == null) {
            ouputPlayFile = FILE_MP3
            if (!ouputPlayFile.exists()) {
                Toast.makeText(context, "不存在播放文件", Toast.LENGTH_SHORT).show()
                return
            }
        }

        //1. 创建播放器
        val player = ExoPlayer.Builder(context).setRenderersFactory(object :DefaultRenderersFactory(context){
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
                    .setAudioProcessors(arrayOf(SoxAudioProcessors()))
                    .setOffloadMode(
                        if (enableOffload) DefaultAudioSink.OFFLOAD_MODE_ENABLED_GAPLESS_REQUIRED else DefaultAudioSink.OFFLOAD_MODE_DISABLED
                    )
                    .build()
            }
        }).build()
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (STATE_READY == playbackState) {
                    viewModel.localSavedStateHandle()["ExoPlayer"] = player
                }
            }
        })
//        player.printCurPlaybackState("init")  //  此时处于STATE_IDLE = 1;

        //2. 播放器和播放器容器绑定
//        playerView?.player = player

        //3. 设置数据源
        //音频
        // val mediaItem = MediaBrowser.MediaItem(MediaDescription.Builder().setMediaUri(Uri.parse("file:///android_asset/千山万水.mp3")).build(),0)
        if (path == null) {
            player.setMediaItem(fromUri(ouputPlayFile!!.toUri()))
        } else {
            Log.i(TAG, "exoPlaySImple: $path")
            player.setMediaItem(fromUri(path))
        }
        //4.当Player处于STATE_READY状态时，进行播放
        player.playWhenReady = true

        //5. 调用prepare开始加载准备数据，该方法时异步方法，不会阻塞ui线程
        player.prepare()
        player.play() //  此时处于 STATE_BUFFERING = 2;
        viewModel.getThisLifecycle()?.lifecycle?.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
//                player.pause()
                player.stop()
                player.release()
            }
        })


    }

    //
    @JvmStatic
    @Deprecated("测试~")
    external fun subtraction(one: Int, tow: Int): Int

    @JvmStatic
    external fun buildMusic2(i:String,fileOutputPath: String = (FILE_MP3.absolutePath).apply {
        Log.i(TAG, "buildMusic: output file = $this")
    }):Int

    @Nullable
    fun buildMusicByEffectInfo(effectsBean: EffectsBean?,byteArray: ByteArray):ByteArray{
       return buildMusicByEffectInfo(effectsBean?.r_name,effectsBean?.charParams,byteArray)
    }

    @JvmStatic
    @Nullable
    external fun buildMusicByEffectInfo(effectRealName: String?,charArray: CharArray?,byteArray: ByteArray):ByteArray

    @Nullable
    fun buildMusicByEffectInfoFile(effectsBean: EffectsBean?,path:String,byteArray: ByteArray):Int{
        return buildMusicByEffectInfoFile(effectsBean?.r_name, effectsBean?.values?.get(0),path,effectsBean?.charParams,byteArray)
    }

//    @JvmStatic
//    @Nullable
//    external fun buildMusicByEffectInfoFile(effectRealName: String?,values:Array<String>?,path:String,charArray: CharArray?,byteArray: ByteArray):Int

    @JvmStatic
    @Nullable
    external fun buildMusicByEffectInfoFile(effectRealName: String?,values:String?,path:String,charArray: CharArray?,byteArray: ByteArray):Int

    @JvmStatic
    external fun buildMusic(
        fileInputPath: String =  //"${BASE_URL}/我的刻苦铭心的恋人.mp3"
            (Environment.getExternalStorageDirectory().absolutePath + File.separator + "千山万水.mp3").apply {
                if (File(this).exists()) {
                    Log.i(
                        TAG,
                        "buildMusic: 存在~ $this"
                    )
                }
            },
        fileOutputPath: String = (FILE_MP3.absolutePath).apply {
            Log.i(TAG, "buildMusic: output file = $this")
        }
    ): Int

//    @JvmStatic
//    external fun handlerSteam(byteArray: ByteArray):ByteArray

    @JvmStatic
    external fun initSox(): Int


    @JvmStatic
    external fun exeuteComment(order: String): Int

    fun copy(mp3: String, file: File) {
        copyFile(File(mp3), file)
    }

    fun show(s: String) {
        Toast.makeText(ActivityThreadHelp.getCurrentApplication(),s,Toast.LENGTH_SHORT).show()
    }


}