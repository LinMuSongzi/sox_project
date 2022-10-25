package com.example.cpp

import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem.fromUri
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.STATE_READY
import com.google.android.exoplayer2.ui.PlayerControlView
import java.io.File

object SoxUtil {

    val BASE_URL: String = "http://192.168.1.106:8080"
    const val TAG = "SOX_UTIL"
    val FILE_PARENT = SoxApplication.INSTANCE.cacheDir
    val FILE_MP3 = File(FILE_PARENT, "qsws.mp3")

    //    @JvmStatic
//    external fun sum(one: Int,tow:Int):Int


    @RequiresApi(Build.VERSION_CODES.M)
    fun exoPlaySImple(
        lifecycleOwner: LifecycleOwner,
        playerView: PlayerControlView,
        path: String? = "${BASE_URL}/千山万水.mp3"
    ) {

        if (playerView.tag != null) {
            val p = playerView.tag as? Player
            if (true == p?.isPlaying) {
                p?.pause()
            } else {
                p?.play()
            }
            return
        }

        var ouputPlayFile: File? = null
        if (path == null) {
            ouputPlayFile = FILE_MP3
            if (!ouputPlayFile.exists()) {
                Toast.makeText(playerView.context, "不存在播放文件", Toast.LENGTH_SHORT).show()
                return
            }
        }

        //1. 创建播放器
        val player = ExoPlayer.Builder(playerView.context).build()
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (STATE_READY == playbackState) {
                    playerView.tag = player
                }
            }
        })
//        player.printCurPlaybackState("init")  //  此时处于STATE_IDLE = 1;

        //2. 播放器和播放器容器绑定
        playerView.player = player

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
        lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
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
    external fun buildMusic(
        fileInputPath: String = (Environment.getExternalStorageDirectory().absolutePath + File.separator + "千山万水.mp3").apply {
            if (File(this).exists()) {
                Log.i(
                    TAG,
                    "buildMusic: 存在~"
                )
            }
        },
        fileOutputPath: String = FILE_MP3.absolutePath
    ): Int

    @JvmStatic
    external fun initSox(): Int


    @JvmStatic
    external fun exeuteComment(order: String): Int

}