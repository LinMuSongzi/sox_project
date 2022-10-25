package com.example.cpp

import android.media.MediaDescription
import android.media.browse.MediaBrowser
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaItem.CREATOR
import com.google.android.exoplayer2.MediaItem.fromUri
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerControlView
import java.io.File

object SoxUtil {

    const val TAG = "SOX_UTIL"
    val FILE_PARENT = SoxApplication.INSTANCE.cacheDir
    val FILE_MP3 = File(FILE_PARENT,"qsws.mp3")

    //    @JvmStatic
//    external fun sum(one: Int,tow:Int):Int


    @RequiresApi(Build.VERSION_CODES.M)
    fun exoPlaySImple(lifecycleOwner: LifecycleOwner, playerView: PlayerControlView) {
        val ouputPlayFile = FILE_MP3

        if(!ouputPlayFile.exists()){
            Toast.makeText(playerView.context,"不存在播放文件",Toast.LENGTH_SHORT).show()
            return
        }

        //1. 创建播放器
        val player = ExoPlayer.Builder(playerView.context).build()
//        player.printCurPlaybackState("init")  //  此时处于STATE_IDLE = 1;

        //2. 播放器和播放器容器绑定
        playerView.player = player

        //3. 设置数据源
        //音频
        // val mediaItem = MediaBrowser.MediaItem(MediaDescription.Builder().setMediaUri(Uri.parse("file:///android_asset/千山万水.mp3")).build(),0)

        player.setMediaItem(fromUri(ouputPlayFile.toUri()))

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
    external fun buildMusic(fileInputPath: String = "file:///android_asset/千山万水.mp3", fileOutputPath: String = FILE_MP3.absolutePath): Int

    @JvmStatic
    external fun initSox(): Int
}