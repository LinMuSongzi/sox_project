package com.example.cpp.business

//import com.example.cpp.business.ISteamConsumption.Companion.*
import android.content.Context
import androidx.core.net.toUri
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.cpp.business.ISteamConsumption.Companion.INIT_PLAYER_STATE
import com.example.cpp.business.ISteamConsumption.Companion.MASK_STATE
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.audio.*
import com.musongzi.core.base.business.BaseMapBusiness
import com.musongzi.core.itf.IHodlerReentrantLock
import com.musongzi.core.itf.ILifeObject
import com.psyone.sox.NewHandlerSoxAudioProcessor
import com.psyone.sox.SoxAudioProcessor
import io.reactivex.rxjava3.core.Observable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.locks.ReentrantLock

class ExoPlayerSupportBusiness : BaseMapBusiness<ILifeObject>(),
    ExecutorService by Executors.newCachedThreadPool(), IHodlerReentrantLock, IMusicStore {

    val reentrantLock = ReentrantLock()
    lateinit var outsidMusicStore: IMusicStore
    var runnable: ExoRunnable? = null

    fun observer(music: IMusicStore) {
        if(runnable == null) {
            outsidMusicStore = music
            runnable = ExoRunnable(this)
        }
        if(runnable?.isReady() != true) {
            iAgent.getThisLifecycle()?.lifecycle?.addObserver(runnable!!)
        }else{

        }
    }


    class ExoRunnable(val business: ExoPlayerSupportBusiness) : Runnable, DefaultLifecycleObserver,
        ISteamConsumption, Player.Listener {

        var state: Int = ISteamConsumption.NORMAL_STATE

        lateinit var exoPlayer :ExoPlayer

        override fun onCreate(owner: LifecycleOwner) {
            business.execute(this)
        }

        override fun run() {
            initHandler()
        }

        override fun initHandler() {
            business.reentrantLock.lock()
            if (state.and(INIT_PLAYER_STATE) == 0) {
                exoPlayer = buildExoplayer(business)
                state.or(INIT_PLAYER_STATE)
            }
            business.reentrantLock.unlock()
        }

        private fun buildExoplayer(business: ExoPlayerSupportBusiness): ExoPlayer {
            val context = business.iAgent as Context
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
                        .setOffloadMode(
                            if (enableOffload) DefaultAudioSink.OFFLOAD_MODE_ENABLED_GAPLESS_REQUIRED else DefaultAudioSink.OFFLOAD_MODE_DISABLED
                        ).setAudioProcessors(arrayOf(NewHandlerSoxAudioProcessor()))
                        .build()
                }

            }).build()

//            player.set

            player.addListener(this)
            business.getMusicPath().subscribe{
                player.playWhenReady = true
                player.setMediaItem(MediaItem.fromUri(it.toUri()))
                player.prepare()
                player.play() //  此时处于 STATE_BUFFERING = 2;
                business.iAgent.getThisLifecycle()?.lifecycle?.addObserver(object : DefaultLifecycleObserver {
                    override fun onDestroy(owner: LifecycleOwner) {
                        player.stop()
                        player.release()
                    }
                })

            }

            return player
        }

        override fun isReady(): Boolean {
            return state.and(MASK_STATE) > 0
        }

        override fun handler(byteArray: ByteArray, offset: Int, length: Long) {

        }

        override fun onEvents(player: Player, events: Player.Events) {

        }


    }

    override fun getHolderReentrantLock(): ReentrantLock {
        return reentrantLock
    }

    override fun getMusicPath(): Observable<String> {
       return outsidMusicStore.getMusicPath()
    }

//
//    class MyPlayer : SimpleExoPlayer{
//
//    }

}

